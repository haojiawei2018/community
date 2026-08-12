-- 社区业务版：成员每日签到记录
CREATE TABLE IF NOT EXISTS `member_check_in` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT 'Internal community scope',
  `member_id` bigint(20) unsigned NOT NULL,
  `check_in_date` date NOT NULL,
  `streak_days` int(10) unsigned NOT NULL DEFAULT 1,
  `points` int(10) unsigned NOT NULL DEFAULT 10,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_member_check_in_date` (`tenant_id`, `member_id`, `check_in_date`),
  KEY `idx_tenant_member_check_in_time` (`tenant_id`, `member_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Member daily check-in record';
