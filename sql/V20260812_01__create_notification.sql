-- 社区业务版：用户通知表（可重复执行）
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `receiver_member_id` bigint NOT NULL,
  `sender_member_id` bigint DEFAULT NULL,
  `notification_type` varchar(32) NOT NULL COMMENT 'COMMENT/REPLY/LIKE/FOLLOW/SYSTEM/REVIEW',
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(1024) NOT NULL,
  `biz_type` varchar(32) DEFAULT NULL,
  `biz_id` bigint DEFAULT NULL,
  `read_status` tinyint NOT NULL DEFAULT 0,
  `read_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_tenant_notification_receiver` (`tenant_id`, `receiver_member_id`, `read_status`, `created_at`),
  KEY `idx_tenant_notification_biz` (`tenant_id`, `biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tenant member notification';
