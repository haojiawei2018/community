-- GamingCommunity 开源业务版完整初始化 SQL
-- 生成日期：2026-08-10
-- 目标：MySQL 5.7+，请在一个空数据库中执行。
-- 包含账号、成员、RBAC、游戏、圈子、帖子、评论、互动、审核、通知和审计。
-- 不包含 SaaS 租户运营、平台管理员、套餐、订阅、计费或商业权益。
-- 可重复执行建表和种子语句，但不用于自动修复已有同名表的错误字段。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------
-- tenant_agreement
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_agreement` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `agreement_type` varchar(32) NOT NULL COMMENT 'TERMS/PRIVACY/COMMUNITY_RULES',
  `version_no` varchar(32) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
  `published_at` datetime DEFAULT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_agreement_version` (`tenant_id`, `agreement_type`, `version_no`),
  KEY `idx_tenant_agreement_status` (`tenant_id`, `agreement_type`, `status`, `published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant agreement version';

-- ---------------------------------------------------------
-- iam_user
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `iam_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Global user ID',
  `username` varchar(64) DEFAULT NULL,
  `nickname` varchar(64) NOT NULL,
  `avatar_url` varchar(512) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `gender` tinyint(1) unsigned NOT NULL DEFAULT 0 COMMENT '0 unknown, 1 male, 2 female',
  `bio` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/LOCKED/CLOSED',
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

-- ---------------------------------------------------------
-- iam_user_identity
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `iam_user_identity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `identity_type` varchar(32) NOT NULL COMMENT 'PASSWORD/MOBILE/EMAIL/WECHAT',
  `identity_value` varchar(255) NOT NULL COMMENT 'Login identifier or third-party open ID',
  `credential_hash` varchar(255) DEFAULT NULL COMMENT 'Password or credential hash',
  `union_id` varchar(128) DEFAULT NULL,
  `verified` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_identity` (`identity_type`, `identity_value`),
  KEY `idx_identity_user` (`user_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Global user login identity';

-- ---------------------------------------------------------
-- iam_refresh_token
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `iam_refresh_token` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `user_id` bigint(20) unsigned NOT NULL,
  `member_id` bigint(20) unsigned NOT NULL,
  `token_hash` varchar(255) NOT NULL,
  `device_id` varchar(128) DEFAULT NULL,
  `client_type` varchar(32) DEFAULT NULL COMMENT 'WECHAT/ANDROID/IOS/H5/ADMIN',
  `expires_at` datetime NOT NULL,
  `revoked_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_refresh_hash` (`tenant_id`, `token_hash`),
  KEY `idx_tenant_refresh_user` (`tenant_id`, `user_id`, `expires_at`),
  KEY `idx_tenant_refresh_member` (`tenant_id`, `member_id`, `expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Hashed refresh token';

-- ---------------------------------------------------------
-- tenant_member
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `user_id` bigint(20) unsigned NOT NULL,
  `display_name` varchar(64) NOT NULL,
  `avatar_url` varchar(512) DEFAULT NULL,
  `bio` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/MUTED/BANNED/LEFT',
  `mute_until` datetime DEFAULT NULL,
  `joined_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_active_at` datetime DEFAULT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_user` (`tenant_id`, `user_id`),
  KEY `idx_tenant_member_status` (`tenant_id`, `status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User membership inside a tenant';

-- ---------------------------------------------------------
-- user_agreement_acceptance
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_agreement_acceptance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `user_id` bigint(20) unsigned NOT NULL,
  `member_id` bigint(20) unsigned NOT NULL,
  `agreement_id` bigint(20) unsigned NOT NULL,
  `agreement_type` varchar(32) NOT NULL,
  `version_no` varchar(32) NOT NULL,
  `accepted_ip` varchar(64) DEFAULT NULL,
  `accepted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_user_agreement` (`tenant_id`, `user_id`, `agreement_id`),
  KEY `idx_tenant_agreement_user` (`tenant_id`, `agreement_type`, `user_id`, `accepted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User agreement acceptance evidence';

-- ---------------------------------------------------------
-- tenant_role
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `role_code` varchar(64) NOT NULL,
  `role_name` varchar(64) NOT NULL,
  `role_type` varchar(32) NOT NULL DEFAULT 'CUSTOM' COMMENT 'OWNER/ADMIN/MODERATOR/CIRCLE_ADMIN/MEMBER/CUSTOM',
  `description` varchar(255) DEFAULT NULL,
  `system_role` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`),
  KEY `idx_tenant_role_type` (`tenant_id`, `role_type`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant role';

-- ---------------------------------------------------------
-- sys_permission
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(128) NOT NULL,
  `permission_name` varchar(128) NOT NULL,
  `module_code` varchar(64) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_permission_module` (`module_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Platform permission catalog';

-- ---------------------------------------------------------
-- tenant_member_role
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_member_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_member_role` (`tenant_id`, `member_id`, `role_id`),
  KEY `idx_tenant_role_member` (`tenant_id`, `role_id`, `member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant member role relation';

-- ---------------------------------------------------------
-- tenant_role_permission
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_role_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `role_id` bigint(20) unsigned NOT NULL,
  `permission_id` bigint(20) unsigned NOT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_role_permission` (`tenant_id`, `role_id`, `permission_id`),
  KEY `idx_tenant_permission_role` (`tenant_id`, `permission_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant role permission relation';

-- ---------------------------------------------------------
-- game_catalog
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `game_catalog` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `game_code` varchar(64) NOT NULL,
  `game_name` varchar(128) NOT NULL,
  `icon_url` varchar(512) DEFAULT NULL,
  `cover_url` varchar(512) DEFAULT NULL,
  `description` text NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_game_code` (`game_code`),
  KEY `idx_game_status` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Platform game catalog';

-- ---------------------------------------------------------
-- tenant_game
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_game` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `game_id` bigint(20) unsigned NOT NULL,
  `display_name` varchar(128) DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_game` (`tenant_id`, `game_id`),
  KEY `idx_tenant_game_status` (`tenant_id`, `status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Games enabled by a tenant';

-- ---------------------------------------------------------
-- forum_circle
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_circle` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `tenant_game_id` bigint(20) unsigned DEFAULT NULL,
  `circle_code` varchar(64) NOT NULL,
  `circle_name` varchar(128) NOT NULL,
  `icon_url` varchar(512) DEFAULT NULL,
  `cover_url` varchar(512) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `join_mode` varchar(32) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/APPROVAL/CLOSED',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `member_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `post_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_circle_code` (`tenant_id`, `circle_code`),
  KEY `idx_tenant_circle_game` (`tenant_id`, `tenant_game_id`, `status`),
  KEY `idx_tenant_circle_sort` (`tenant_id`, `status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant forum circle';

-- ---------------------------------------------------------
-- forum_circle_member
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_circle_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `circle_id` bigint(20) unsigned NOT NULL,
  `member_id` bigint(20) unsigned NOT NULL,
  `member_role` varchar(32) NOT NULL DEFAULT 'MEMBER' COMMENT 'OWNER/ADMIN/MEMBER',
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `joined_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_circle_member` (`tenant_id`, `circle_id`, `member_id`),
  KEY `idx_tenant_member_circle` (`tenant_id`, `member_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Circle membership';

-- ---------------------------------------------------------
-- forum_section
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_section` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `circle_id` bigint(20) unsigned NOT NULL,
  `section_code` varchar(64) NOT NULL,
  `section_name` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `icon_url` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_circle_section` (`tenant_id`, `circle_id`, `section_code`),
  KEY `idx_tenant_section_sort` (`tenant_id`, `circle_id`, `status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Forum section under a circle';

-- ---------------------------------------------------------
-- forum_topic
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_topic` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `circle_id` bigint(20) unsigned DEFAULT NULL,
  `topic_name` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `cover_url` varchar(512) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `start_at` datetime DEFAULT NULL,
  `end_at` datetime DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_topic_circle` (`tenant_id`, `circle_id`, `status`),
  KEY `idx_tenant_topic_time` (`tenant_id`, `start_at`, `end_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant forum topic';

-- ---------------------------------------------------------
-- tenant_banner
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_banner` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `banner_title` varchar(255) DEFAULT NULL,
  `image_url` varchar(1024) NOT NULL,
  `link_type` varchar(32) NOT NULL DEFAULT 'NONE' COMMENT 'NONE/POST/CIRCLE/WEB',
  `link_value` varchar(1024) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `start_at` datetime DEFAULT NULL,
  `end_at` datetime DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_banner_status` (`tenant_id`, `status`, `sort_order`),
  KEY `idx_tenant_banner_time` (`tenant_id`, `start_at`, `end_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant home banner';

-- ---------------------------------------------------------
-- tenant_announcement
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tenant_announcement` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `title` varchar(255) NOT NULL,
  `content` longtext NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PUBLISHED/ARCHIVED',
  `is_top` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `published_at` datetime DEFAULT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_announcement_feed` (`tenant_id`, `status`, `is_top`, `published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant announcement';

-- ---------------------------------------------------------
-- forum_tag
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `tag_name` varchar(64) NOT NULL,
  `use_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_tag_name` (`tenant_id`, `tag_name`),
  KEY `idx_tenant_tag_hot` (`tenant_id`, `status`, `use_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant content tag';

-- ---------------------------------------------------------
-- forum_post
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_post` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `circle_id` bigint(20) unsigned NOT NULL,
  `section_id` bigint(20) unsigned DEFAULT NULL,
  `topic_id` bigint(20) unsigned DEFAULT NULL,
  `author_member_id` bigint(20) unsigned NOT NULL,
  `post_type` varchar(32) NOT NULL DEFAULT 'ARTICLE' COMMENT 'ARTICLE/IMAGE/QUESTION',
  `title` varchar(255) NOT NULL,
  `summary` varchar(512) DEFAULT NULL,
  `content` longtext NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PENDING/PUBLISHED/REJECTED/HIDDEN/DELETED',
  `visibility` varchar(32) NOT NULL DEFAULT 'PUBLIC' COMMENT 'PUBLIC/MEMBER_ONLY/PRIVATE',
  `is_top` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `is_featured` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `allow_comment` tinyint(1) unsigned NOT NULL DEFAULT 1,
  `view_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `like_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `comment_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `favorite_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `published_at` datetime DEFAULT NULL,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_post_feed` (`tenant_id`, `status`, `is_top`, `published_at`),
  KEY `idx_tenant_post_circle` (`tenant_id`, `circle_id`, `section_id`, `status`, `published_at`),
  KEY `idx_tenant_post_author` (`tenant_id`, `author_member_id`, `status`, `created_at`),
  KEY `idx_tenant_post_topic` (`tenant_id`, `topic_id`, `status`, `published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Forum post';

-- ---------------------------------------------------------
-- file_resource
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `file_resource` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `uploader_member_id` bigint(20) unsigned DEFAULT NULL,
  `storage_provider` varchar(32) NOT NULL DEFAULT 'LOCAL',
  `bucket_name` varchar(128) DEFAULT NULL,
  `object_key` varchar(512) NOT NULL,
  `original_name` varchar(255) DEFAULT NULL,
  `content_type` varchar(128) DEFAULT NULL,
  `file_size` bigint(20) unsigned NOT NULL DEFAULT 0,
  `file_hash` varchar(128) DEFAULT NULL,
  `access_url` varchar(1024) NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_object_key` (`tenant_id`, `object_key`),
  KEY `idx_tenant_file_uploader` (`tenant_id`, `uploader_member_id`, `created_at`),
  KEY `idx_tenant_file_hash` (`tenant_id`, `file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Managed file resource';

-- ---------------------------------------------------------
-- forum_post_media
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_post_media` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `post_id` bigint(20) unsigned NOT NULL,
  `file_id` bigint(20) unsigned DEFAULT NULL,
  `media_type` varchar(32) NOT NULL COMMENT 'IMAGE/VIDEO/FILE',
  `media_url` varchar(1024) NOT NULL,
  `cover_url` varchar(1024) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `duration_seconds` int(11) DEFAULT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_post_media` (`tenant_id`, `post_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post media';

-- ---------------------------------------------------------
-- forum_post_tag
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_post_tag` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `post_id` bigint(20) unsigned NOT NULL,
  `tag_id` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_post_tag` (`tenant_id`, `post_id`, `tag_id`),
  KEY `idx_tenant_tag_post` (`tenant_id`, `tag_id`, `post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post tag relation';

-- ---------------------------------------------------------
-- forum_comment
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `post_id` bigint(20) unsigned NOT NULL,
  `author_member_id` bigint(20) unsigned NOT NULL,
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Direct parent comment ID',
  `root_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Root comment ID',
  `reply_member_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Member being replied to',
  `content` text NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PUBLISHED/REJECTED/HIDDEN/DELETED',
  `like_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `reply_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_comment_post` (`tenant_id`, `post_id`, `status`, `created_at`),
  KEY `idx_tenant_comment_root` (`tenant_id`, `root_id`, `status`, `created_at`),
  KEY `idx_tenant_comment_author` (`tenant_id`, `author_member_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post comment and nested reply';

-- ---------------------------------------------------------
-- forum_reaction
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_reaction` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `target_type` varchar(32) NOT NULL COMMENT 'POST/COMMENT',
  `target_id` bigint(20) unsigned NOT NULL,
  `reaction_type` varchar(32) NOT NULL DEFAULT 'LIKE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_reaction` (`tenant_id`, `member_id`, `target_type`, `target_id`, `reaction_type`),
  KEY `idx_tenant_reaction_target` (`tenant_id`, `target_type`, `target_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post and comment reaction';

-- ---------------------------------------------------------
-- forum_favorite
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_favorite` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `post_id` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_favorite` (`tenant_id`, `member_id`, `post_id`),
  KEY `idx_tenant_favorite_post` (`tenant_id`, `post_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post favorite';

-- ---------------------------------------------------------
-- forum_view_log
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_view_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned DEFAULT NULL,
  `post_id` bigint(20) unsigned NOT NULL,
  `client_ip` varchar(64) DEFAULT NULL,
  `viewed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_view_post` (`tenant_id`, `post_id`, `viewed_at`),
  KEY `idx_tenant_view_member` (`tenant_id`, `member_id`, `viewed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post view log';

-- ---------------------------------------------------------
-- user_follow
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_follow` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `follower_member_id` bigint(20) unsigned NOT NULL,
  `followed_member_id` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_follow` (`tenant_id`, `follower_member_id`, `followed_member_id`),
  KEY `idx_tenant_followed` (`tenant_id`, `followed_member_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant-scoped user follow';

-- ---------------------------------------------------------
-- user_block
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_block` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `blocked_member_id` bigint(20) unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_block` (`tenant_id`, `member_id`, `blocked_member_id`),
  KEY `idx_tenant_blocked` (`tenant_id`, `blocked_member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant-scoped user block';

-- ---------------------------------------------------------
-- forum_report
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `forum_report` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `reporter_member_id` bigint(20) unsigned NOT NULL,
  `target_type` varchar(32) NOT NULL COMMENT 'POST/COMMENT/MEMBER',
  `target_id` bigint(20) unsigned NOT NULL,
  `reason_code` varchar(64) NOT NULL,
  `reason_text` varchar(1024) DEFAULT NULL,
  `evidence_json` longtext NULL,
  `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/PROCESSING/RESOLVED/REJECTED',
  `handler_member_id` bigint(20) unsigned DEFAULT NULL,
  `handled_at` datetime DEFAULT NULL,
  `handle_result` varchar(1024) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_report_status` (`tenant_id`, `status`, `created_at`),
  KEY `idx_tenant_report_target` (`tenant_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User content and member report';

-- ---------------------------------------------------------
-- content_review
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `content_review` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `target_type` varchar(32) NOT NULL COMMENT 'POST/COMMENT/PROFILE/FILE/REPORT',
  `target_id` bigint(20) unsigned NOT NULL,
  `review_source` varchar(32) NOT NULL DEFAULT 'SYSTEM' COMMENT 'SYSTEM/MANUAL/REPORT',
  `review_status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  `risk_level` varchar(32) DEFAULT NULL,
  `risk_labels` varchar(1024) DEFAULT NULL,
  `content_snapshot` longtext NULL,
  `reviewer_member_id` bigint(20) unsigned DEFAULT NULL,
  `review_comment` varchar(1024) DEFAULT NULL,
  `reviewed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_review_status` (`tenant_id`, `review_status`, `created_at`),
  KEY `idx_tenant_review_target` (`tenant_id`, `target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Content review record';

-- ---------------------------------------------------------
-- moderation_action
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `moderation_action` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `operator_member_id` bigint(20) unsigned DEFAULT NULL,
  `target_type` varchar(32) NOT NULL COMMENT 'POST/COMMENT/MEMBER/CIRCLE',
  `target_id` bigint(20) unsigned NOT NULL,
  `action_type` varchar(32) NOT NULL COMMENT 'HIDE/RESTORE/MUTE/UNMUTE/BAN/UNBAN/DELETE',
  `reason` varchar(1024) DEFAULT NULL,
  `effective_until` datetime DEFAULT NULL,
  `source_review_id` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_action_target` (`tenant_id`, `target_type`, `target_id`, `created_at`),
  KEY `idx_tenant_action_operator` (`tenant_id`, `operator_member_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Moderation action audit';

-- ---------------------------------------------------------
-- notification
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `receiver_member_id` bigint(20) unsigned NOT NULL,
  `sender_member_id` bigint(20) unsigned DEFAULT NULL,
  `notification_type` varchar(32) NOT NULL COMMENT 'COMMENT/REPLY/LIKE/FOLLOW/SYSTEM/REVIEW',
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(1024) NOT NULL,
  `biz_type` varchar(32) DEFAULT NULL,
  `biz_id` bigint(20) unsigned DEFAULT NULL,
  `read_status` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `read_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_notification_receiver` (`tenant_id`, `receiver_member_id`, `read_status`, `created_at`),
  KEY `idx_tenant_notification_biz` (`tenant_id`, `biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant member notification';

-- ---------------------------------------------------------
-- sensitive_word
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sensitive_word` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `word_text` varchar(255) NOT NULL,
  `match_type` varchar(32) NOT NULL DEFAULT 'CONTAINS',
  `risk_level` varchar(32) NOT NULL DEFAULT 'MEDIUM',
  `replacement` varchar(255) DEFAULT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'ACTIVE',
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_sensitive_word` (`tenant_id`, `word_text`),
  KEY `idx_tenant_sensitive_status` (`tenant_id`, `status`, `risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant sensitive word';

-- ---------------------------------------------------------
-- sys_audit_log
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_audit_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Null for system operation',
  `operator_user_id` bigint(20) unsigned DEFAULT NULL,
  `operator_member_id` bigint(20) unsigned DEFAULT NULL,
  `operation_module` varchar(64) NOT NULL,
  `operation_type` varchar(64) NOT NULL,
  `operation_target` varchar(255) DEFAULT NULL,
  `request_method` varchar(16) DEFAULT NULL,
  `request_uri` varchar(512) DEFAULT NULL,
  `client_ip` varchar(64) DEFAULT NULL,
  `trace_id` varchar(64) DEFAULT NULL,
  `success` tinyint(1) unsigned NOT NULL DEFAULT 1,
  `detail_json` longtext NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_audit_tenant_time` (`tenant_id`, `created_at`),
  KEY `idx_audit_operator_time` (`operator_user_id`, `created_at`),
  KEY `idx_audit_trace` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Community operation audit';

-- ---------------------------------------------------------
-- demo（保留脚手架 DemoController 兼容）
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS `demo` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        varchar(64)  DEFAULT NULL COMMENT '名称',
  `phone`       varchar(20)  DEFAULT NULL COMMENT '手机号',
  `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='演示表';

-- 默认综合圈子
INSERT INTO `forum_circle`
  (`tenant_id`, `circle_code`, `circle_name`, `description`, `join_mode`, `status`, `sort_order`)
SELECT 1, 'general', '综合交流', '分享游戏心得、攻略和社区动态', 'OPEN', 'ACTIVE', 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `forum_circle` WHERE `tenant_id` = 1 AND `circle_code` = 'general'
);

-- =========================================================
-- Seed: V20260810_01__seed_p0_permissions.sql
-- =========================================================
-- Gaming Community open-source business permission catalog
-- MySQL 5.7+, safe to execute repeatedly because permission_code is unique.

INSERT IGNORE INTO `sys_permission` (`permission_code`, `permission_name`, `module_code`, `description`) VALUES
('tenant.config.read', '查看租户配置', 'tenant', '查看当前租户品牌和功能配置'),
('tenant.config.write', '修改租户配置', 'tenant', '修改当前租户品牌和功能配置'),
('member.read', '查看成员', 'member', '查看当前租户成员列表和详情'),
('member.status.write', '修改成员状态', 'member', '禁言、封禁、解禁当前租户成员'),
('member.role.write', '分配成员角色', 'member', '调整当前租户成员角色'),
('game.manage', '管理租户游戏', 'game', '启用和维护租户游戏'),
('circle.manage', '管理圈子板块', 'forum', '维护圈子、板块、话题和标签'),
('post.create', '发布帖子', 'forum', '创建和提交帖子'),
('comment.create', '发表评论', 'forum', '创建评论和回复'),
('content.review', '审核内容', 'moderation', '审核帖子、评论和用户资料'),
('report.handle', '处理举报', 'moderation', '查看并处理当前租户举报'),
('announcement.manage', '管理公告', 'operation', '维护轮播图和公告'),
('audit.read', '查看审计日志', 'system', '查看当前租户操作审计日志');

-- =========================================================
-- Seed: V20260810_02__seed_default_roles.sql
-- =========================================================
-- Gaming Community open-source default roles and role permissions.
-- The open-source edition uses tenant_id = 1 as an internal community scope.
-- MySQL 5.7+, safe to execute repeatedly.

INSERT IGNORE INTO `tenant_role`
(`tenant_id`, `role_code`, `role_name`, `role_type`, `description`, `system_role`)
VALUES
(1, 'OWNER', '社区所有者', 'SYSTEM', '社区最高权限角色', 1),
(1, 'ADMIN', '社区管理员', 'SYSTEM', '管理社区业务和成员', 1),
(1, 'MODERATOR', '内容版主', 'SYSTEM', '审核内容并处理举报', 1),
(1, 'MEMBER', '普通成员', 'SYSTEM', '发布帖子和评论', 1);

INSERT IGNORE INTO `tenant_role_permission` (`tenant_id`, `role_id`, `permission_id`)
SELECT 1, r.id, p.id
FROM `tenant_role` r
CROSS JOIN `sys_permission` p
WHERE r.tenant_id = 1 AND r.deleted = 0 AND r.role_code IN ('OWNER', 'ADMIN');

INSERT IGNORE INTO `tenant_role_permission` (`tenant_id`, `role_id`, `permission_id`)
SELECT 1, r.id, p.id
FROM `tenant_role` r
JOIN `sys_permission` p ON p.permission_code IN
  ('member.read', 'member.status.write', 'circle.manage', 'content.review', 'report.handle')
WHERE r.tenant_id = 1 AND r.deleted = 0 AND r.role_code = 'MODERATOR';

INSERT IGNORE INTO `tenant_role_permission` (`tenant_id`, `role_id`, `permission_id`)
SELECT 1, r.id, p.id
FROM `tenant_role` r
JOIN `sys_permission` p ON p.permission_code IN ('post.create', 'comment.create')
WHERE r.tenant_id = 1 AND r.deleted = 0 AND r.role_code = 'MEMBER';

SET FOREIGN_KEY_CHECKS = 1;
-- 初始化完成。开源单社区内部 tenant_id 固定为 1。
