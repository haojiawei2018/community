package org.hopeframework.biz.api.common.tenant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** 全局表不追加 tenant_id，其余业务表默认按租户隔离。 */
public final class TenantTableRegistry {

    private static final Set<String> GLOBAL_TABLES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "iam_user",
            "iam_user_identity",
            "game_catalog",
            "sys_permission",
            "platform_role",
            "platform_user_role",
            "platform_role_permission",
            "demo"
    )));

    private TenantTableRegistry() {
    }

    public static boolean isGlobalTable(String tableName) {
        return tableName != null && GLOBAL_TABLES.contains(tableName.toLowerCase());
    }
}
