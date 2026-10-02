-- AI铁盒独立数据表。部署前手工执行；不会修改已有项目表。
CREATE TABLE IF NOT EXISTS ironbox_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    username VARCHAR(32) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(1000) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ironbox_user_name (tenant_id, username),
    KEY idx_ironbox_user_status (tenant_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI铁盒独立用户';

CREATE TABLE IF NOT EXISTS ironbox_box (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(50) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    category VARCHAR(20) NOT NULL,
    tags_json JSON NOT NULL,
    original_url VARCHAR(1200) NOT NULL,
    medium_url VARCHAR(1400) NOT NULL,
    thumbnail_url VARCHAR(1400) NOT NULL,
    object_key VARCHAR(300) NOT NULL,
    width INT NOT NULL DEFAULT 0,
    height INT NOT NULL DEFAULT 0,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(30) NOT NULL,
    visibility VARCHAR(10) NOT NULL DEFAULT 'public',
    status VARCHAR(20) NOT NULL DEFAULT 'published',
    like_count INT NOT NULL DEFAULT 0,
    collect_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    open_count BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ironbox_box_object (tenant_id, object_key),
    KEY idx_ironbox_box_public (tenant_id, visibility, status, deleted_at, id),
    KEY idx_ironbox_box_user (tenant_id, user_id, status, deleted_at, id),
    KEY idx_ironbox_box_category (tenant_id, category, status, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI铁盒作品';

CREATE TABLE IF NOT EXISTS ironbox_like (
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    box_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, user_id, box_id),
    KEY idx_ironbox_like_box (tenant_id, box_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI铁盒点赞';

CREATE TABLE IF NOT EXISTS ironbox_collect (
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    box_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, user_id, box_id),
    KEY idx_ironbox_collect_box (tenant_id, box_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI铁盒收藏';

CREATE TABLE IF NOT EXISTS ironbox_comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    box_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_ironbox_comment_box (tenant_id, box_id, deleted_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI铁盒一级评论';
