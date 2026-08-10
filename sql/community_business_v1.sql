-- Open-source community business baseline
-- MySQL 5.7+. The default single-community scope id is 1.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `iam_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `nickname` varchar(64) NOT NULL,
  `avatar_url` varchar(512) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `gender` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `bio` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `last_login_at` datetime DEFAULT NULL,
  `last_login_ip` varchar(64) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_mobile` (`mobile`),
  UNIQUE KEY `uk_user_email` (`email`),
  KEY `idx_user_status` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Global user account';

CREATE TABLE IF NOT EXISTS `iam_user_identity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `identity_type` varchar(32) NOT NULL,
  `identity_value` varchar(255) NOT NULL,
  `credential_hash` varchar(255) DEFAULT NULL,
  `union_id` varchar(128) DEFAULT NULL,
  `verified` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_identity` (`identity_type`, `identity_value`),
  KEY `idx_identity_user` (`user_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User login identity';

CREATE TABLE IF NOT EXISTS `iam_refresh_token` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `user_id` bigint(20) unsigned NOT NULL,
  `member_id` bigint(20) unsigned NOT NULL,
  `token_hash` varchar(255) NOT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `client_type` varchar(32) DEFAULT NULL,
  `expires_at` datetime NOT NULL,
  `revoked_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_refresh_hash` (`tenant_id`, `token_hash`),
  KEY `idx_scope_refresh_user` (`tenant_id`, `user_id`, `expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Hashed refresh token';

CREATE TABLE IF NOT EXISTS `tenant_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `user_id` bigint(20) unsigned NOT NULL,
  `display_name` varchar(64) NOT NULL,
  `avatar_url` varchar(512) DEFAULT NULL,
  `bio` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `mute_until` datetime DEFAULT NULL,
  `joined_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active_at` datetime DEFAULT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_user` (`tenant_id`, `user_id`),
  KEY `idx_scope_member_status` (`tenant_id`, `status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Community member';

CREATE TABLE IF NOT EXISTS `tenant_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `role_code` varchar(64) NOT NULL,
  `role_name` varchar(64) NOT NULL,
  `role_type` varchar(32) NOT NULL DEFAULT 'CUSTOM',
  `description` varchar(255) DEFAULT NULL,
  `system_role` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_role_code` (`tenant_id`, `role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Community role';

CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(128) NOT NULL,
  `permission_name` varchar(128) NOT NULL,
  `module_code` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Business permission catalog';

CREATE TABLE IF NOT EXISTS `tenant_member_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_member_role` (`tenant_id`, `member_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Member role relation';

CREATE TABLE IF NOT EXISTS `tenant_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `role_id` bigint(20) unsigned NOT NULL,
  `permission_id` bigint(20) unsigned NOT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scope_role_permission` (`tenant_id`, `role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Role permission relation';

SET FOREIGN_KEY_CHECKS = 1;
