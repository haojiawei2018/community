package org.hopeframework.biz.api.service.flashcard;

import org.hopeframework.biz.api.config.oss.AliyunOssProperties;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardFileMapper;
import org.hopeframework.biz.api.service.impl.flashcard.FlashcardAppImageServiceImpl;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FlashcardAppImageServiceImplTest {

    @Test
    public void cropsLargeJpegToFlashcardRatioBeforeOssUpload() throws Exception {
        BufferedImage source = new BufferedImage(4000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = source.createGraphics();
        graphics.setColor(Color.ORANGE);
        graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        graphics.dispose();

        Object compressed = compress(write(source, "jpg"), "large.jpg");

        assertEquals(780, intField(compressed, "width"));
        assertEquals(1000, intField(compressed, "height"));
        assertEquals("jpg", field(compressed, "format"));
        assertTrue((Boolean) field(compressed, "resized"));
    }

    @Test
    public void preservesAlphaForTransparentPngLayer() throws Exception {
        BufferedImage source = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        source.setRGB(10, 10, 0x55FF0000);

        Object compressed = compress(write(source, "png"), "foreground.png", "FOREGROUND");
        byte[] bytes = (byte[]) field(compressed, "bytes");
        BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(bytes));

        assertEquals("png", field(compressed, "format"));
        assertTrue(decoded.getColorModel().hasAlpha());
        assertEquals(39, decoded.getWidth());
        assertEquals(50, decoded.getHeight());
        assertTrue(maxAlpha(decoded) > 0);
    }

    @Test
    public void compositesTransparentForegroundOverSubject() throws Exception {
        BufferedImage subject = new BufferedImage(78, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D subjectGraphics = subject.createGraphics();
        subjectGraphics.setColor(Color.BLUE);
        subjectGraphics.fillRect(0, 0, 78, 100);
        subjectGraphics.dispose();

        BufferedImage foreground = new BufferedImage(78, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D foregroundGraphics = foreground.createGraphics();
        foregroundGraphics.setColor(Color.RED);
        foregroundGraphics.fillRect(29, 40, 20, 20);
        foregroundGraphics.dispose();

        FlashcardAppImageServiceImpl service = new FlashcardAppImageServiceImpl(
                new AliyunOssProperties(), mock(FlashcardFileMapper.class));
        Method method = FlashcardAppImageServiceImpl.class.getDeclaredMethod(
                "composeImages", BufferedImage.class, BufferedImage.class, BufferedImage.class);
        method.setAccessible(true);
        BufferedImage result = (BufferedImage) method.invoke(service, subject, foreground, null);

        assertEquals(Color.BLUE.getRGB() & 0xFFFFFF, result.getRGB(10, 10) & 0xFFFFFF);
        assertEquals(Color.RED.getRGB() & 0xFFFFFF, result.getRGB(39, 50) & 0xFFFFFF);
    }

    @Test
    public void keepsSubjectVisibleWhenForegroundIsOpaquePhoto() throws Exception {
        BufferedImage subject = new BufferedImage(78, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D subjectGraphics = subject.createGraphics();
        subjectGraphics.setColor(Color.BLUE);
        subjectGraphics.fillRect(0, 0, 78, 100);
        subjectGraphics.dispose();

        BufferedImage foreground = new BufferedImage(78, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D foregroundGraphics = foreground.createGraphics();
        foregroundGraphics.setColor(Color.RED);
        foregroundGraphics.fillRect(0, 0, 78, 100);
        foregroundGraphics.dispose();

        BufferedImage result = compose(subject, foreground, null);
        Color corner = new Color(result.getRGB(2, 2));
        Color center = new Color(result.getRGB(39, 50));

        assertTrue(corner.getBlue() > corner.getRed());
        assertTrue(center.getRed() > center.getBlue());
        assertTrue(center.getBlue() > 0);
    }

    @Test
    public void screenEffectKeepsDarkPixelsAndAddsHighlights() throws Exception {
        BufferedImage subject = new BufferedImage(39, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D subjectGraphics = subject.createGraphics();
        subjectGraphics.setColor(new Color(60, 70, 80));
        subjectGraphics.fillRect(0, 0, 39, 50);
        subjectGraphics.dispose();

        BufferedImage effect = new BufferedImage(39, 50, BufferedImage.TYPE_INT_RGB);
        effect.setRGB(2, 2, Color.BLACK.getRGB());
        effect.setRGB(20, 25, Color.WHITE.getRGB());

        BufferedImage result = compose(subject, null, effect);
        Color darkResult = new Color(result.getRGB(2, 2));
        Color lightResult = new Color(result.getRGB(20, 25));

        assertEquals(new Color(60, 70, 80), darkResult);
        assertTrue(lightResult.getRed() > 60);
        assertTrue(lightResult.getGreen() > 70);
        assertTrue(lightResult.getBlue() > 80);
    }

    private BufferedImage compose(BufferedImage subject, BufferedImage foreground,
                                  BufferedImage effect) throws Exception {
        FlashcardAppImageServiceImpl service = new FlashcardAppImageServiceImpl(
                new AliyunOssProperties(), mock(FlashcardFileMapper.class));
        Method method = FlashcardAppImageServiceImpl.class.getDeclaredMethod(
                "composeImages", BufferedImage.class, BufferedImage.class, BufferedImage.class);
        method.setAccessible(true);
        return (BufferedImage) method.invoke(service, subject, foreground, effect);
    }

    private Object compress(byte[] bytes, String name) throws Exception {
        return compress(bytes, name, "SUBJECT");
    }

    private Object compress(byte[] bytes, String name, String layerType) throws Exception {
        FlashcardAppImageServiceImpl service = new FlashcardAppImageServiceImpl(
                new AliyunOssProperties(), mock(FlashcardFileMapper.class));
        Method method = FlashcardAppImageServiceImpl.class.getDeclaredMethod(
                "compress", byte[].class, String.class, String.class);
        method.setAccessible(true);
        return method.invoke(service, bytes, name, layerType);
    }

    private int maxAlpha(BufferedImage image) {
        int max = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                max = Math.max(max, (image.getRGB(x, y) >>> 24) & 0xFF);
            }
        }
        return max;
    }

    private byte[] write(BufferedImage image, String format) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, format, output);
        return output.toByteArray();
    }

    private int intField(Object target, String name) throws Exception {
        return (Integer) field(target, name);
    }

    private Object field(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }
}
