-- 帖子图片关联表（完整初始化 SQL 已包含，本文件用于已有数据库增量升级）
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
