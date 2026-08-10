-- Hope Framework 脚手架演示表
-- 使用前先在本地 MySQL 创建数据库：CREATE DATABASE IF NOT EXISTS hope DEFAULT CHARACTER SET utf8mb4;
CREATE TABLE IF NOT EXISTS `demo` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        varchar(64)  DEFAULT NULL COMMENT '名称',
  `phone`       varchar(20)  DEFAULT NULL COMMENT '手机号',
  `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='演示表';

INSERT INTO `demo` (`name`, `phone`, `remark`, `create_time`) VALUES
('张三', '13800000001', '示例数据', NOW()),
('李四', '13800000002', '示例数据', NOW()),
('王五', '13800000003', '示例数据', NOW());