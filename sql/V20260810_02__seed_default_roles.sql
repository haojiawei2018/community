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
