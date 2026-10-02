package org.hopeframework.biz.api.service.impl.flashcard;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OSSObject;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.config.oss.AliyunOssProperties;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardImageUploadResponse;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardFileMapper;
import org.hopeframework.biz.api.model.flashcard.FlashcardFile;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppImageService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FlashcardAppImageServiceImpl implements IFlashcardAppImageService {
    private static final Set<String> LAYER_TYPES = new HashSet<>(
            Arrays.asList("SUBJECT", "FOREGROUND", "EFFECT", "LINEART", "BACK", "PROFILE_AVATAR"));
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png"));
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final int MAX_IMAGE_SIDE = 2048;
    private static final int CARD_RATIO_WIDTH = 39;
    private static final int CARD_RATIO_HEIGHT = 50;
    private static final long MAX_PIXELS = 40_000_000L;
    private static final float JPEG_QUALITY = 0.82F;

    private final AliyunOssProperties properties;
    private final FlashcardFileMapper fileMapper;

    public FlashcardAppImageServiceImpl(AliyunOssProperties properties, FlashcardFileMapper fileMapper) {
        this.properties = properties;
        this.fileMapper = fileMapper;
    }

    @Override
    public FlashcardImageUploadResponse upload(MultipartFile file, String layerTypeText) {
        AuthPrincipal principal = requireFlashcardUser();
        String layerType = normalizeLayerType(layerTypeText);
        validateConfiguration();
        validateFile(file);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        byte[] originalBytes = readBytes(file);
        CompressedImage compressed = compress(originalBytes, originalName, layerType);
        String objectName = buildObjectName(compressed.format);
        uploadToOss(objectName, compressed);

        FlashcardFile storedFile = new FlashcardFile();
        storedFile.setUserId(principal.getUserId());
        storedFile.setLayerType(layerType);
        storedFile.setOriginalName(originalName);
        storedFile.setObjectName(objectName);
        storedFile.setFileUrl(buildPublicUrl(objectName));
        storedFile.setOriginalSize((long) originalBytes.length);
        storedFile.setFileSize((long) compressed.bytes.length);
        storedFile.setWidth(compressed.width);
        storedFile.setHeight(compressed.height);
        storedFile.setImageFormat(compressed.format.toUpperCase(Locale.ROOT));
        storedFile.setCreatedAt(new Date());
        storedFile.setDeleted(0);
        fileMapper.insert(storedFile);

        FlashcardImageUploadResponse response = new FlashcardImageUploadResponse();
        response.setFileId(storedFile.getId());
        response.setUrl(storedFile.getFileUrl());
        response.setObjectName(objectName);
        response.setOriginalName(originalName);
        response.setOriginalSize(originalBytes.length);
        response.setSize(compressed.bytes.length);
        response.setWidth(compressed.width);
        response.setHeight(compressed.height);
        response.setFormat(compressed.format.toUpperCase(Locale.ROOT));
        // 所有图片都会重新编码以去除元数据；大图还会按最长边限制缩放。
        response.setCompressed(true);
        return response;
    }

    @Override
    public String composeAndUpload(FlashcardFile subject, FlashcardFile foreground, FlashcardFile effect) {
        if (subject == null) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "生成闪卡缺少主体图片");
        }
        validateConfiguration();
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
            BufferedImage subjectImage = readOssImage(ossClient, subject);
            BufferedImage foregroundImage = foreground == null ? null : readOssImage(ossClient, foreground);
            BufferedImage effectImage = effect == null ? null : readOssImage(ossClient, effect);
            BufferedImage composed = composeImages(subjectImage, foregroundImage, effectImage);
            byte[] bytes = encodeJpeg(composed);
            String objectName = buildGeneratedObjectName();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setContentType("image/jpeg");
            try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
                ossClient.putObject(properties.getBucketName(), objectName, input, metadata);
            }
            return buildPublicUrl(objectName);
        } catch (OSSException exception) {
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(),
                    "闪卡图层合成访问阿里云OSS失败：" + exception.getErrorCode(), exception);
        } catch (ClientException exception) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "闪卡图层合成无法连接阿里云OSS", exception);
        } catch (IOException exception) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "闪卡图层合成失败", exception);
        } finally {
            if (ossClient != null) ossClient.shutdown();
        }
    }

    private BufferedImage readOssImage(OSS ossClient, FlashcardFile file) throws IOException {
        OSSObject object = ossClient.getObject(properties.getBucketName(), file.getObjectName());
        try (InputStream input = object.getObjectContent()) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) throw new IOException("OSS object is not a valid image: " + file.getObjectName());
            return image;
        }
    }

    private BufferedImage composeImages(BufferedImage subject, BufferedImage foreground, BufferedImage effect) {
        CardSize cardSize = cardSize(subject.getWidth(), subject.getHeight());
        int width = cardSize.width;
        int height = cardSize.height;
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, width, height);
            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.drawImage(coverImage(subject, width, height), 0, 0, null);
            if (foreground != null && foreground.getColorModel().hasAlpha()) {
                drawContainedLayer(graphics, foreground, width, height, 1F);
            } else {
                // 普通照片不能整幅覆盖主体，缩成居中软边画面参与构图。
                drawSoftForeground(graphics, foreground, width, height);
            }
            if (effect != null && effect.getColorModel().hasAlpha()) {
                drawContainedLayer(graphics, effect, width, height, 0.78F);
            }
        } finally {
            graphics.dispose();
        }
        // 不透明光效图片使用滤色混合：暗部不遮挡底图，只把亮部和色彩叠加到成图。
        if (effect != null && !effect.getColorModel().hasAlpha()) {
            screenBlend(target, effect, 0.42F);
        }
        return target;
    }

    private void drawSoftForeground(Graphics2D graphics, BufferedImage layer,
                                    int canvasWidth, int canvasHeight) {
        if (layer == null) return;
        double scale = Math.min((double) canvasWidth / layer.getWidth(),
                (double) canvasHeight / layer.getHeight()) * 0.84D;
        int width = Math.max(1, (int) Math.round(layer.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(layer.getHeight() * scale));
        int x = (canvasWidth - width) / 2;
        int y = (canvasHeight - height) / 2;

        BufferedImage softened = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D layerGraphics = softened.createGraphics();
        try {
            layerGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            layerGraphics.drawImage(layer, x, y, width, height, null);
        } finally {
            layerGraphics.dispose();
        }

        int feather = Math.max(12, Math.min(width, height) / 9);
        for (int py = y; py < y + height; py++) {
            for (int px = x; px < x + width; px++) {
                int argb = softened.getRGB(px, py);
                int alpha = (argb >>> 24) & 0xFF;
                int edge = Math.min(Math.min(px - x, x + width - 1 - px),
                        Math.min(py - y, y + height - 1 - py));
                float ratio = Math.min(1F, Math.max(0F, (float) edge / feather));
                float smooth = ratio * ratio * (3F - 2F * ratio);
                int softenedAlpha = Math.round(alpha * smooth * 0.94F);
                softened.setRGB(px, py, (softenedAlpha << 24) | (argb & 0xFFFFFF));
            }
        }
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.drawImage(softened, 0, 0, null);
    }

    private void screenBlend(BufferedImage base, BufferedImage effect, float strength) {
        BufferedImage fitted = coverImage(effect, base.getWidth(), base.getHeight());
        for (int y = 0; y < base.getHeight(); y++) {
            for (int x = 0; x < base.getWidth(); x++) {
                int baseRgb = base.getRGB(x, y);
                int effectRgb = fitted.getRGB(x, y);
                int red = screenChannel((baseRgb >>> 16) & 0xFF, (effectRgb >>> 16) & 0xFF, strength);
                int green = screenChannel((baseRgb >>> 8) & 0xFF, (effectRgb >>> 8) & 0xFF, strength);
                int blue = screenChannel(baseRgb & 0xFF, effectRgb & 0xFF, strength);
                base.setRGB(x, y, 0xFF000000 | (red << 16) | (green << 8) | blue);
            }
        }
    }

    private int screenChannel(int base, int effect, float strength) {
        int screened = 255 - ((255 - base) * (255 - effect) / 255);
        return Math.max(0, Math.min(255, Math.round(base + (screened - base) * strength)));
    }

    private BufferedImage coverImage(BufferedImage source, int width, int height) {
        double scale = Math.max((double) width / source.getWidth(), (double) height / source.getHeight());
        int drawWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int drawHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(source, (width - drawWidth) / 2, (height - drawHeight) / 2,
                    drawWidth, drawHeight, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private void drawContainedLayer(Graphics2D graphics, BufferedImage layer,
                                    int canvasWidth, int canvasHeight, float opacity) {
        if (layer == null) return;
        double scale = Math.min((double) canvasWidth / layer.getWidth(),
                (double) canvasHeight / layer.getHeight());
        int width = Math.max(1, (int) Math.round(layer.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(layer.getHeight() * scale));
        int x = (canvasWidth - width) / 2;
        int y = (canvasHeight - height) / 2;
        graphics.setComposite(AlphaComposite.SrcOver.derive(opacity));
        graphics.drawImage(layer, x, y, width, height, null);
        graphics.setComposite(AlphaComposite.SrcOver);
    }

    private AuthPrincipal requireFlashcardUser() {
        AuthPrincipal principal = AuthContext.require();
        if (!principal.isFlashcardUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录信息不属于闪卡APP");
        }
        return principal;
    }

    private String normalizeLayerType(String layerTypeText) {
        String layerType = StringUtils.hasText(layerTypeText)
                ? layerTypeText.trim().toUpperCase(Locale.ROOT) : "SUBJECT";
        if (!LAYER_TYPES.contains(layerType)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(),
                    "layerType只能是SUBJECT、LINEART、BACK、FOREGROUND或EFFECT");
        }
        return layerType;
    }

    private void validateConfiguration() {
        if (isMissing(properties.getEndpoint()) || isMissing(properties.getAccessKeyId())
                || isMissing(properties.getAccessKeySecret()) || isMissing(properties.getBucketName())) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "闪卡APP的阿里云OSS配置不完整");
        }
    }

    private boolean isMissing(String value) {
        if (!StringUtils.hasText(value)) return true;
        String trimmed = value.trim();
        return trimmed.startsWith("${") && trimmed.endsWith("}");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "请选择要上传的图片");
        }
        if (properties.getMaxImageSize() != null
                && file.getSize() > properties.getMaxImageSize().toBytes()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(),
                    "闪卡图片大小不能超过 " + properties.getMaxImageSize());
        }
        String extension = extension(StringUtils.cleanPath(file.getOriginalFilename()));
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "闪卡APP仅支持JPG、JPEG和PNG图片");
        }
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "读取上传图片失败", exception);
        }
    }

    private CompressedImage compress(byte[] originalBytes, String originalName, String layerType) {
        try {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (source == null) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "上传文件不是有效图片");
            }
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();
            if (sourceWidth <= 0 || sourceHeight <= 0
                    || (long) sourceWidth * (long) sourceHeight > MAX_PIXELS) {
                throw new HopeException(HttpStatus.BAD_REQUEST.value(), "图片像素尺寸过大");
            }

            CardSize cardSize = cardSize(sourceWidth, sourceHeight);
            int width = cardSize.width;
            int height = cardSize.height;
            boolean png = "png".equals(extension(originalName)) && source.getColorModel().hasAlpha();
            String format = png ? "png" : "jpg";
            boolean contain = png && !"SUBJECT".equals(layerType);
            BufferedImage target = adaptToCard(source, width, height, png, contain);
            byte[] encoded = png ? encodePng(target) : encodeJpeg(target);
            return new CompressedImage(encoded, width, height, format,
                    sourceWidth != width || sourceHeight != height);
        } catch (HopeException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "压缩上传图片失败", exception);
        }
    }

    private CardSize cardSize(int sourceWidth, int sourceHeight) {
        int maxUnit = MAX_IMAGE_SIDE / CARD_RATIO_HEIGHT;
        int widthUnit = sourceWidth / CARD_RATIO_WIDTH;
        int heightUnit = sourceHeight / CARD_RATIO_HEIGHT;
        int unit = Math.max(1, Math.min(maxUnit, Math.min(widthUnit, heightUnit)));
        return new CardSize(CARD_RATIO_WIDTH * unit, CARD_RATIO_HEIGHT * unit);
    }

    private BufferedImage adaptToCard(BufferedImage source, int width, int height,
                                      boolean alpha, boolean contain) {
        int imageType = alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage target = new BufferedImage(width, height, imageType);
        Graphics2D graphics = target.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (alpha) {
                graphics.setComposite(AlphaComposite.Src);
            } else {
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, width, height);
            }
            double scale = contain
                    ? Math.min((double) width / source.getWidth(), (double) height / source.getHeight())
                    : Math.max((double) width / source.getWidth(), (double) height / source.getHeight());
            int drawWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
            int drawHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
            graphics.drawImage(source, (width - drawWidth) / 2, (height - drawHeight) / 2,
                    drawWidth, drawHeight, null);
        } finally {
            graphics.dispose();
        }
        return target;
    }

    private byte[] encodePng(BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!ImageIO.write(image, "png", output)) {
            throw new IOException("PNG writer is unavailable");
        }
        return output.toByteArray();
    }

    private byte[] encodeJpeg(BufferedImage image) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) throw new IOException("JPEG writer is unavailable");
        ImageWriter writer = writers.next();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(imageOutput);
            ImageWriteParam parameter = writer.getDefaultWriteParam();
            if (parameter.canWriteCompressed()) {
                parameter.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                parameter.setCompressionQuality(JPEG_QUALITY);
            }
            writer.write(null, new IIOImage(image, null, null), parameter);
        } finally {
            writer.dispose();
        }
        return output.toByteArray();
    }

    private void uploadToOss(String objectName, CompressedImage image) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.bytes.length);
        metadata.setContentType("image/" + ("jpg".equals(image.format) ? "jpeg" : "png"));
        OSS ossClient = null;
        try (ByteArrayInputStream input = new ByteArrayInputStream(image.bytes)) {
            ossClient = new OSSClientBuilder().build(
                    properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
            ossClient.putObject(properties.getBucketName(), objectName, input, metadata);
        } catch (OSSException exception) {
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(),
                    "闪卡图片上传阿里云OSS失败：" + exception.getErrorCode(), exception);
        } catch (ClientException exception) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "无法连接闪卡APP的阿里云OSS", exception);
        } catch (IOException exception) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "关闭图片上传流失败", exception);
        } finally {
            if (ossClient != null) ossClient.shutdown();
        }
    }

    private String buildObjectName(String format) {
        String root = trimSlashes(properties.getObjectPrefix());
        String prefix = StringUtils.hasText(root) ? root + "/flashcard-app" : "flashcard-app";
        return prefix + "/" + LocalDate.now().format(DATE_PATH) + "/"
                + UUID.randomUUID().toString().replace("-", "") + "." + format;
    }

    private String buildGeneratedObjectName() {
        String root = trimSlashes(properties.getObjectPrefix());
        String prefix = StringUtils.hasText(root) ? root + "/flashcard-app" : "flashcard-app";
        return prefix + "/generated/v3/" + LocalDate.now().format(DATE_PATH) + "/"
                + UUID.randomUUID().toString().replace("-", "") + ".jpg";
    }

    private String buildPublicUrl(String objectName) {
        String domain = properties.getPublicDomain();
        if (!StringUtils.hasText(domain)) {
            String endpoint = properties.getEndpoint().replaceFirst("^https?://", "");
            domain = "https://" + properties.getBucketName() + "." + endpoint;
        }
        return domain.replaceAll("/+$", "") + "/" + objectName;
    }

    private String extension(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension == null ? "" : extension.toLowerCase(Locale.ROOT);
    }

    private String trimSlashes(String value) {
        return value == null ? "" : value.replaceAll("^/+|/+$", "");
    }

    private static final class CompressedImage {
        private final byte[] bytes;
        private final int width;
        private final int height;
        private final String format;
        private final boolean resized;

        private CompressedImage(byte[] bytes, int width, int height, String format, boolean resized) {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
            this.format = format;
            this.resized = resized;
        }
    }

    private static final class CardSize {
        private final int width;
        private final int height;

        private CardSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
