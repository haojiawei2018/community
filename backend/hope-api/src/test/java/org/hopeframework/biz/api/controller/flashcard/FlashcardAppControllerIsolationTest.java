package org.hopeframework.biz.api.controller.flashcard;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlashcardAppControllerIsolationTest {

    @Test
    public void usesDedicatedFlashcardAppRoutePrefix() {
        RequestMapping mapping = FlashcardAppController.class.getAnnotation(RequestMapping.class);

        assertEquals(1, mapping.value().length);
        assertEquals("/api/v1/flashcard-app", mapping.value()[0]);
    }

    @Test
    public void exposesDedicatedCompressedImageUploadRoute() throws Exception {
        Method method = Arrays.stream(FlashcardAppController.class.getMethods())
                .filter(item -> "uploadImage".equals(item.getName()))
                .findFirst().orElseThrow(NoSuchMethodException::new);
        PostMapping mapping = method.getAnnotation(PostMapping.class);

        assertEquals("/files/images", mapping.value()[0]);
        assertTrue(Arrays.asList(mapping.consumes()).contains(MediaType.MULTIPART_FORM_DATA_VALUE));
    }
}
