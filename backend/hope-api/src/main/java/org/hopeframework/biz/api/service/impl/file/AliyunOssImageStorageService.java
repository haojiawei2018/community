package org.hopeframework.biz.api.service.impl.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.hopeframework.biz.api.config.oss.AliyunOssProperties;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.service.file.IImageStorageService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AliyunOssImageStorageService implements IImageStorageService {
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp"));
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final AliyunOssProperties properties;

    public AliyunOssImageStorageService(AliyunOssProperties properties) {
        this.properties = properties;
    }

    @Override
    public ImageUploadResponse upload(MultipartFile file) {
        validateConfiguration();
        validateFile(file);
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String objectName = buildObjectName(getExtension(originalName));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        OSS ossClient = new OSSClientBuilder().build(
                properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(properties.getBucketName(), objectName, inputStream, metadata);
        } catch (IOException | RuntimeException ex) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片上传到阿里云 OSS 失败", ex);
        } finally {
            ossClient.shutdown();
        }
        return new ImageUploadResponse(buildPublicUrl(objectName), objectName, originalName, file.getSize());
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getEndpoint())
                || !StringUtils.hasText(properties.getAccessKeyId())
                || !StringUtils.hasText(properties.getAccessKeySecret())
                || !StringUtils.hasText(properties.getBucketName())) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "阿里云 OSS 配置不完整");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "请选择要上传的图片");
        }
        if (properties.getMaxImageSize() != null && file.getSize() > properties.getMaxImageSize().toBytes()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "图片大小不能超过 " + properties.getMaxImageSize());
        }
        String contentType = file.getContentType();
        String extension = getExtension(StringUtils.cleanPath(file.getOriginalFilename()));
        if (!StringUtils.hasText(contentType)
                || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")
                || !IMAGE_EXTENSIONS.contains(extension)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "仅支持 jpg、jpeg、png、gif、webp、bmp 图片");
        }
    }

    private String getExtension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension == null ? "" : extension.toLowerCase(Locale.ROOT);
    }

    private String buildObjectName(String extension) {
        String prefix = trimSlashes(properties.getObjectPrefix());
        String datePath = LocalDate.now().format(DATE_PATH);
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        return (StringUtils.hasText(prefix) ? prefix + "/" : "") + datePath + "/" + fileName;
    }

    private String buildPublicUrl(String objectName) {
        String domain = properties.getPublicDomain();
        if (!StringUtils.hasText(domain)) {
            String endpoint = properties.getEndpoint().replaceFirst("^https?://", "");
            domain = "https://" + properties.getBucketName() + "." + endpoint;
        }
        return domain.replaceAll("/+$", "") + "/" + objectName;
    }

    private String trimSlashes(String value) {
        return value == null ? "" : value.replaceAll("^/+|/+$", "");
    }
}
