-- 闪卡独立APP最小表结构（与论坛、预约项目共用数据库，但业务表完全隔离）。
-- 所有表统一使用 flashcard_app_ 前缀；本脚本需手工执行，应用启动不会自动改库。

CREATE TABLE IF NOT EXISTS flashcard_app_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '闪卡APP用户ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    device_id VARCHAR(128) DEFAULT NULL COMMENT '仅兼容旧版设备游客登录，正式账号不绑定设备',
    client_type VARCHAR(30) NOT NULL DEFAULT 'APP' COMMENT 'APP/MP-WEIXIN/H5',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号登录账号',
    apple_subject VARCHAR(255) DEFAULT NULL COMMENT 'Apple用户唯一sub',
    apple_email VARCHAR(255) DEFAULT NULL COMMENT 'Apple返回邮箱',
    nickname VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    bio VARCHAR(500) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_flashcard_app_user_device (tenant_id, device_id, deleted),
    UNIQUE KEY uk_flashcard_app_user_phone (tenant_id, phone, deleted),
    UNIQUE KEY uk_flashcard_app_user_apple (tenant_id, apple_subject, deleted),
    KEY idx_flashcard_app_user_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡独立APP用户';

CREATE TABLE IF NOT EXISTS flashcard_app_card (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '闪卡ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    creator_user_id BIGINT DEFAULT NULL COMMENT '创建该卡的闪卡APP用户ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    card_name VARCHAR(100) NOT NULL COMMENT '卡牌名称',
    rarity VARCHAR(10) NOT NULL COMMENT 'N/R/SR/SSR/UR',
    series_name VARCHAR(100) DEFAULT NULL COMMENT '所属系列',
    description VARCHAR(1000) DEFAULT NULL,
    image_url VARCHAR(500) NOT NULL,
    edition_no VARCHAR(50) DEFAULT NULL COMMENT '卡牌编号',
    favorite_count BIGINT NOT NULL DEFAULT 0,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE' COMMENT 'PUBLIC/PRIVATE',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_flashcard_app_card_square (tenant_id, visibility, status, deleted, favorite_count),
    KEY idx_flashcard_app_card_creator (tenant_id, creator_user_id, status, deleted, created_at),
    KEY idx_flashcard_app_card_category (tenant_id, category_code, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡独立APP卡牌';

CREATE TABLE IF NOT EXISTS flashcard_app_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    user_id BIGINT NOT NULL COMMENT '闪卡APP用户ID',
    card_id BIGINT NOT NULL COMMENT '闪卡ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_app_favorite (tenant_id, user_id, card_id),
    KEY idx_flashcard_app_favorite_card (tenant_id, card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP收藏关系';

CREATE TABLE IF NOT EXISTS flashcard_app_file (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '上传文件ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    user_id BIGINT NOT NULL COMMENT '闪卡APP用户ID',
    layer_type VARCHAR(20) NOT NULL COMMENT 'SUBJECT/FOREGROUND/EFFECT',
    original_name VARCHAR(255) DEFAULT NULL,
    object_name VARCHAR(500) NOT NULL COMMENT '阿里云OSS对象名',
    file_url VARCHAR(500) NOT NULL COMMENT '阿里云OSS访问地址',
    original_size BIGINT NOT NULL COMMENT '压缩前字节数',
    file_size BIGINT NOT NULL COMMENT '压缩后字节数',
    width INT NOT NULL,
    height INT NOT NULL,
    image_format VARCHAR(10) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_app_file_object (tenant_id, object_name, deleted),
    KEY idx_flashcard_app_file_user (tenant_id, user_id, layer_type, deleted, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP独立上传记录';

CREATE TABLE IF NOT EXISTS flashcard_app_generation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '生成任务主键',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    task_no VARCHAR(40) NOT NULL COMMENT '生成任务编号',
    user_id BIGINT NOT NULL COMMENT '闪卡APP用户ID',
    subject_file_id BIGINT NOT NULL,
    foreground_file_id BIGINT DEFAULT NULL,
    effect_file_id BIGINT DEFAULT NULL,
    prompt_text VARCHAR(500) DEFAULT NULL,
    style_code VARCHAR(30) NOT NULL,
    rarity_code VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PROCESSING/SUCCEEDED/FAILED',
    progress INT NOT NULL DEFAULT 0,
    card_id BIGINT DEFAULT NULL,
    error_message VARCHAR(500) DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_app_generation_no (tenant_id, task_no, deleted),
    KEY idx_flashcard_app_generation_user (tenant_id, user_id, status, deleted, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP生成任务';

CREATE TABLE IF NOT EXISTS flashcard_app_home_banner (
    id BIGINT NOT NULL AUTO_INCREMENT, tenant_id BIGINT NOT NULL, card_id BIGINT NOT NULL,
    eyebrow VARCHAR(100) DEFAULT NULL, title VARCHAR(200) DEFAULT NULL,
    subtitle VARCHAR(500) DEFAULT NULL, image_url VARCHAR(500) DEFAULT NULL,
    sort_order INT NOT NULL DEFAULT 0, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_home_banner_card (tenant_id, card_id, deleted),
    KEY idx_flashcard_home_banner_sort (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP首页轮播配置';

CREATE TABLE IF NOT EXISTS flashcard_app_home_card (
    id BIGINT NOT NULL AUTO_INCREMENT, tenant_id BIGINT NOT NULL, card_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0, status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_home_card (tenant_id, card_id, deleted),
    KEY idx_flashcard_home_card_sort (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP首页闪卡列表配置';

-- 与当前闪卡前端展示一致的初始广场数据。生成服务接入后，新卡由任务成功回写。
INSERT INTO flashcard_app_card
    (id, tenant_id, creator_user_id, category_code, card_name, rarity, series_name,
     description, image_url, edition_no, favorite_count, visibility, status, deleted)
VALUES
    (2001, 1, NULL, 'CHINESE', '九尾狐', 'SSR', '山海异闻',
     '一九尾现，天下安。山海之间，自有她的传说。', '/static/flashcards/fox-spirit.png', '0018', 12400, 'PUBLIC', 'ACTIVE', 0),
    (2002, 1, NULL, 'MYTH', '齐天大圣', 'SSR', '东方神话',
     '踏碎凌霄，放肆桀骜，烈焰中仍守一颗赤子心。', '/static/flashcards/monkey-king.png', '0028', 8700, 'PUBLIC', 'ACTIVE', 0),
    (2003, 1, NULL, 'CHINESE', '青龙', 'SR', '四象神兽',
     '苍龙出海，星河入梦，守望东方万里云天。', '/static/flashcards/azure-dragon.png', '0038', 5200, 'PUBLIC', 'ACTIVE', 0),
    (2004, 1, NULL, 'ORIGINAL', '月下花神', 'SSR', '花灵纪',
     '月华凝作花露，于静夜唤醒一园春色。', '/static/flashcards/fox-spirit.png', '0048', 4100, 'PUBLIC', 'ACTIVE', 0),
    (2005, 1, NULL, 'GAME', '不死鸟', 'SR', '幻想图鉴',
     '余烬散去之前，新的羽翼已经生长。', '/static/flashcards/monkey-king.png', '0058', 3800, 'PUBLIC', 'ACTIVE', 0),
    (2006, 1, NULL, 'MYTH', '白泽', 'SR', '山海异闻',
     '通万物之情，晓天下鬼神，行于云海之间。', '/static/flashcards/azure-dragon.png', '0068', 2900, 'PUBLIC', 'ACTIVE', 0)
ON DUPLICATE KEY UPDATE
    category_code = VALUES(category_code), card_name = VALUES(card_name), rarity = VALUES(rarity),
    series_name = VALUES(series_name), description = VALUES(description), image_url = VALUES(image_url),
    edition_no = VALUES(edition_no), visibility = VALUES(visibility), status = VALUES(status), deleted = 0;
