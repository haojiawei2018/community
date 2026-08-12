package org.hopeframework.biz.api.service.file;

import org.hopeframework.biz.api.config.oss.AliyunOssProperties;
import org.hopeframework.biz.api.service.impl.file.AliyunOssImageStorageService;
import org.hopeframework.core.exception.HopeException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AliyunOssImageStorageServiceTest {

    private AliyunOssProperties properties;
    private AliyunOssImageStorageService service;

    @Before
    public void setUp() {
        properties = new AliyunOssProperties();
        properties.setEndpoint("https://oss-cn-hangzhou.aliyuncs.com");
        properties.setAccessKeyId("test-key-id");
        properties.setAccessKeySecret("test-key-secret");
        properties.setBucketName("test-bucket");
        properties.setMaxImageSize(DataSize.ofBytes(4));
        service = new AliyunOssImageStorageService(properties);
    }

    @Test
    public void rejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);
        HopeException exception = captureException(file);
        assertEquals(400, exception.getCode());
        assertEquals("请选择要上传的图片", exception.getMessage());
    }

    @Test
    public void rejectsNonImageFile() {
        MockMultipartFile file = new MockMultipartFile("file", "readme.txt", "text/plain", new byte[]{1});
        HopeException exception = captureException(file);
        assertEquals(400, exception.getCode());
        assertEquals("仅支持 jpg、jpeg、png、gif、webp、bmp 图片", exception.getMessage());
    }

    @Test
    public void rejectsOversizedImage() {
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", new byte[5]);
        HopeException exception = captureException(file);
        assertEquals(400, exception.getCode());
    }

    @Test
    public void rejectsMissingConfigurationBeforeUpload() {
        properties.setEndpoint("");
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", new byte[]{1});
        HopeException exception = captureException(file);
        assertEquals(500, exception.getCode());
        assertEquals("阿里云 OSS 配置不完整", exception.getMessage());
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
