-- 用户举报与屏蔽功能增量脚本，可重复执行。
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
