-- 仅供已经执行过旧版 xiaosong_tv_schema.sql 的环境升级使用；新环境无需重复执行。
ALTER TABLE xiaosong_tv_user
    MODIFY COLUMN username VARCHAR(32) DEFAULT NULL COMMENT '密码登录用户名，统一存小写',
    MODIFY COLUMN password_hash VARCHAR(100) DEFAULT NULL COMMENT 'BCrypt密码摘要',
    ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '手机号验证码登录账号' AFTER username,
    ADD UNIQUE KEY uk_xiaosong_tv_user_phone (tenant_id, phone, deleted);
