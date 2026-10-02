-- 闪藏馆后台首页配置。轮播图与首页闪卡列表分别持久化，且只能引用用户真实生成的闪卡。
CREATE TABLE IF NOT EXISTS flashcard_app_home_banner (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    eyebrow VARCHAR(100) DEFAULT NULL,
    title VARCHAR(200) DEFAULT NULL,
    subtitle VARCHAR(500) DEFAULT NULL,
    image_url VARCHAR(500) DEFAULT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_home_banner_card (tenant_id, card_id, deleted),
    KEY idx_flashcard_home_banner_sort (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP首页轮播配置';

CREATE TABLE IF NOT EXISTS flashcard_app_home_card (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_flashcard_home_card (tenant_id, card_id, deleted),
    KEY idx_flashcard_home_card_sort (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='闪卡APP首页闪卡列表配置';
