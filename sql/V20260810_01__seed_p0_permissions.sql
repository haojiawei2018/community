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
