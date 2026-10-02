package org.hopeframework.biz.api.service.impl.file;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.hopeframework.biz.api.config.cos.TencentCosProperties;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.hopeframework.biz.api.service.file.IImageStorageService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "storage.image", name = "provider", havingValue = "tencent")
public class TencentCosImageStorageService implements IImageStorageService {
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp"));
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final TencentCosProperties properties;

    public TencentCosImageStorageService(TencentCosProperties properties) {
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

        COSClient cosClient = null;
        try (InputStream inputStream = file.getInputStream()) {
            COSCredentials credentials = new BasicCOSCredentials(properties.getSecretId(), properties.getSecretKey());
            ClientConfig clientConfig = new ClientConfig(new Region(properties.getRegion()));
            cosClient = new COSClient(credentials, clientConfig);
            cosClient.putObject(new PutObjectRequest(
                    properties.getBucketName(), objectName, inputStream, metadata));
        } catch (CosServiceException exception) {
            if (isConfigurationError(exception)) {
                throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "腾讯云 COS 配置无效，请检查 SecretId、SecretKey、Region、Bucket 和访问权限", exception);
            }
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(),
                    "腾讯云 COS 上传失败：" + safeErrorCode(exception.getErrorCode()), exception);
        } catch (CosClientException exception) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "无法连接腾讯云 COS，请检查 Region 和服务器网络", exception);
        } catch (IOException exception) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "图片上传到腾讯云 COS 失败", exception);
        } finally {
            if (cosClient != null) cosClient.shutdown();
        }
        return new ImageUploadResponse(buildPublicUrl(objectName), objectName, originalName, file.getSize());
    }

    private void validateConfiguration() {
        if (isMissing(properties.getSecretId())
                || isMissing(properties.getSecretKey())
                || isMissing(properties.getRegion())
                || isMissing(properties.getBucketName())) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "未配置腾讯云 COS 信息，请配置 SecretId、SecretKey、Region 和 Bucket");
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

    private boolean isMissing(String value) {
        if (!StringUtils.hasText(value)) return true;
        String trimmed = value.trim();
        return trimmed.startsWith("${") && trimmed.endsWith("}");
    }

    private boolean isConfigurationError(CosServiceException exception) {
        int statusCode = exception.getStatusCode();
        String errorCode = exception.getErrorCode();
        return statusCode == 403 || statusCode == 404
                || "InvalidAccessKeyId".equals(errorCode)
                || "SignatureDoesNotMatch".equals(errorCode)
                || "AccessDenied".equals(errorCode)
                || "NoSuchBucket".equals(errorCode);
    }

    private String safeErrorCode(String errorCode) {
        return StringUtils.hasText(errorCode) ? errorCode : "UNKNOWN";
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
            domain = "https://" + properties.getBucketName() + ".cos."
                    + properties.getRegion() + ".myqcloud.com";
        }
        return domain.replaceAll("/+$", "") + "/" + objectName;
    }

    private String trimSlashes(String value) {
        return value == null ? "" : value.replaceAll("^/+|/+$", "");
    }
}
