package org.hopeframework.biz.api.common.tenant;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TenantContextTest {

    @After
    public void tearDown() {
        TenantContext.clear();
    }

    @Test
    public void shouldStoreAndClearTenant() {
        TenantContext.set(1001L, "official");

        assertEquals(Long.valueOf(1001L), TenantContext.getTenantId());
        assertEquals("official", TenantContext.getTenantCode());

        TenantContext.clear();
        assertNull(TenantContext.getTenantId());
        assertNull(TenantContext.getTenantCode());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRejectTenantDataAccessWithoutContext() {
        TenantContext.requireTenantId();
    }
}
