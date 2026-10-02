package org.hopeframework.biz.api.controller.petsnack;

import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.auto.UserLoginToken;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PetSnackControllerIsolationTest {

    @Test
    public void usesDedicatedPetSnackRouteAndPrincipalType() {
        RequestMapping mapping = PetSnackController.class.getAnnotation(RequestMapping.class);
        assertEquals("/api/v1/pet-snack", mapping.value()[0]);

        AuthPrincipal principal = new AuthPrincipal(1L, 1L, 1L, "PET_SNACK");
        assertTrue(principal.isPetSnackUser());
    }

    @Test
    public void keepsBrowsingPublicAndOrderCreationProtected() throws Exception {
        Method bootstrap = Arrays.stream(PetSnackController.class.getMethods())
                .filter(method -> "bootstrap".equals(method.getName()))
                .findFirst().orElseThrow(NoSuchMethodException::new);
        Method createOrder = Arrays.stream(PetSnackController.class.getMethods())
                .filter(method -> "createOrder".equals(method.getName()))
                .findFirst().orElseThrow(NoSuchMethodException::new);

        assertNotNull(bootstrap.getAnnotation(PassToken.class));
        assertNotNull(createOrder.getAnnotation(UserLoginToken.class));
    }
}
