-- 小松的电视机最小后端表结构。
-- 与其他项目共用数据库，但表、路由、身份类型和 Token 签名均独立；本脚本需手工执行。

CREATE TABLE IF NOT EXISTS xiaosong_tv_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '小松的电视机用户ID',
    tenant_id BIGINT NOT NULL COMMENT '服务端租户上下文ID，不接受客户端传入',
    username VARCHAR(32) DEFAULT NULL COMMENT '密码登录用户名，统一存小写',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号验证码登录账号',
    password_hash VARCHAR(100) DEFAULT NULL COMMENT 'BCrypt密码摘要',
    nickname VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_xiaosong_tv_user_username (tenant_id, username, deleted),
    UNIQUE KEY uk_xiaosong_tv_user_phone (tenant_id, phone, deleted),
    KEY idx_xiaosong_tv_user_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小松的电视机独立用户';

CREATE TABLE IF NOT EXISTS xiaosong_tv_cd (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'CD ID',
    tenant_id BIGINT NOT NULL COMMENT '服务端租户上下文ID，不接受客户端传入',
    user_id BIGINT NOT NULL COMMENT '从Token身份获取的小松用户ID',
    name VARCHAR(30) NOT NULL COMMENT 'CD名称，业务限制最多6个字符',
    sounds_json JSON NOT NULL COMMENT '九种声音的启用状态及音量',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_xiaosong_tv_cd_user (tenant_id, user_id, deleted, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小松的电视机用户CD';
