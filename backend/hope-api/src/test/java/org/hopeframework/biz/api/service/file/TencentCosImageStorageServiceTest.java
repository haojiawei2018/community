package org.hopeframework.biz.api.service.file;

import org.hopeframework.biz.api.config.cos.TencentCosProperties;
import org.hopeframework.biz.api.service.impl.file.TencentCosImageStorageService;
import org.hopeframework.core.exception.HopeException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TencentCosImageStorageServiceTest {

    private TencentCosProperties properties;
    private TencentCosImageStorageService service;

    @Before
    public void setUp() {
        properties = new TencentCosProperties();
        properties.setSecretId("test-secret-id");
        properties.setSecretKey("test-secret-key");
        properties.setRegion("ap-guangzhou");
        properties.setBucketName("test-bucket-1250000000");
        properties.setMaxImageSize(DataSize.ofBytes(4));
        service = new TencentCosImageStorageService(properties);
    }

    @Test
    public void rejectsEmptyFile() {
        HopeException exception = captureException(
                new MockMultipartFile("file", "empty.png", "image/png", new byte[0]));
        assertEquals(400, exception.getCode());
        assertEquals("请选择要上传的图片", exception.getMessage());
    }

    @Test
    public void rejectsNonImageFile() {
        HopeException exception = captureException(
                new MockMultipartFile("file", "readme.txt", "text/plain", new byte[]{1}));
        assertEquals(400, exception.getCode());
        assertEquals("仅支持 jpg、jpeg、png、gif、webp、bmp 图片", exception.getMessage());
    }

    @Test
    public void rejectsOversizedImage() {
        HopeException exception = captureException(
                new MockMultipartFile("file", "large.png", "image/png", new byte[5]));
        assertEquals(400, exception.getCode());
    }

    @Test
    public void rejectsMissingConfigurationBeforeUpload() {
        properties.setSecretId("");
        HopeException exception = captureException(
                new MockMultipartFile("file", "image.png", "image/png", new byte[]{1}));
        assertEquals(503, exception.getCode());
        assertEquals("未配置腾讯云 COS 信息，请配置 SecretId、SecretKey、Region 和 Bucket", exception.getMessage());
    }

    @Test
    public void rejectsUnresolvedConfigurationPlaceholderBeforeUpload() {
        properties.setRegion("${TENCENT_COS_REGION}");
        HopeException exception = captureException(
                new MockMultipartFile("file", "image.png", "image/png", new byte[]{1}));
        assertEquals(503, exception.getCode());
        assertEquals("未配置腾讯云 COS 信息，请配置 SecretId、SecretKey、Region 和 Bucket", exception.getMessage());
    }

    private HopeException captureException(MockMultipartFile file) {
        try {
            service.upload(file);
            fail("Expected HopeException");
            return null;
        } catch (HopeException exception) {
            return exception;
        }
    }
}
