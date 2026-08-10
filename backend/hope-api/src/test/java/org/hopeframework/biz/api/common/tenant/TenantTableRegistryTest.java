package org.hopeframework.biz.api.common.tenant;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TenantTableRegistryTest {

    @Test
    public void shouldOnlyExcludeGlobalTables() {
        assertTrue(TenantTableRegistry.isGlobalTable("IAM_USER"));
        assertFalse(TenantTableRegistry.isGlobalTable("forum_post"));
        assertFalse(TenantTableRegistry.isGlobalTable("tenant_member"));
    }
}
