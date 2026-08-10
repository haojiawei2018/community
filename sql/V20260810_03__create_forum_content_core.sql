-- P0 forum content core for the open single-community edition.
-- MySQL 5.7+, tenant scope 1 is resolved by the server.

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `forum_circle` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1,
  `circle_code` varchar(64) NOT NULL,
  `circle_name` varchar(128) NOT NULL,
  `icon_url` varchar(512) DEFAULT NULL,
  `cover_url` varchar(512) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `join_mode` varchar(32) NOT NULL DEFAULT 'OPEN',
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
  KEY `idx_tenant_circle_sort` (`tenant_id`, `status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Forum circle';

CREATE TABLE IF NOT EXISTS `forum_post` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1,
  `circle_id` bigint(20) unsigned NOT NULL,
  `author_member_id` bigint(20) unsigned NOT NULL,
  `post_type` varchar(32) NOT NULL DEFAULT 'ARTICLE',
  `title` varchar(255) NOT NULL,
  `summary` varchar(512) DEFAULT NULL,
  `content` longtext NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'DRAFT',
  `visibility` varchar(32) NOT NULL DEFAULT 'PUBLIC',
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
  KEY `idx_tenant_post_circle` (`tenant_id`, `circle_id`, `status`, `published_at`),
  KEY `idx_tenant_post_author` (`tenant_id`, `author_member_id`, `status`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Forum post';

CREATE TABLE IF NOT EXISTS `forum_comment` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1,
  `post_id` bigint(20) unsigned NOT NULL,
  `author_member_id` bigint(20) unsigned NOT NULL,
  `content` text NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'PUBLISHED',
  `like_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `reply_count` bigint(20) unsigned NOT NULL DEFAULT 0,
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint(20) unsigned DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_comment_post` (`tenant_id`, `post_id`, `status`, `created_at`),
  KEY `idx_tenant_comment_author` (`tenant_id`, `author_member_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Post comment';

CREATE TABLE IF NOT EXISTS `forum_reaction` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1,
  `member_id` bigint(20) unsigned NOT NULL,
  `target_type` varchar(32) NOT NULL,
  `target_id` bigint(20) unsigned NOT NULL,
  `reaction_type` varchar(32) NOT NULL DEFAULT 'LIKE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_reaction` (`tenant_id`, `member_id`, `target_type`, `target_id`, `reaction_type`),
  KEY `idx_tenant_reaction_target` (`tenant_id`, `target_type`, `target_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Forum reaction';

INSERT INTO `forum_circle`
  (`tenant_id`, `circle_code`, `circle_name`, `description`, `join_mode`, `status`, `sort_order`)
SELECT 1, 'general', '综合交流', '分享游戏心得、攻略和社区动态', 'OPEN', 'ACTIVE', 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `forum_circle` WHERE `tenant_id` = 1 AND `circle_code` = 'general'
);
