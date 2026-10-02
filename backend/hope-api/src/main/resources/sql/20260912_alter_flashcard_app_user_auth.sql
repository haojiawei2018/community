-- 闪卡独立APP手机号及Apple登录字段；正式库执行一次。
ALTER TABLE flashcard_app_user
    ADD COLUMN phone VARCHAR(20) DEFAULT NULL COMMENT '手机号登录账号' AFTER client_type,
    ADD COLUMN apple_subject VARCHAR(255) DEFAULT NULL COMMENT 'Apple用户唯一sub' AFTER phone,
    ADD COLUMN apple_email VARCHAR(255) DEFAULT NULL COMMENT 'Apple返回邮箱' AFTER apple_subject,
    ADD UNIQUE KEY uk_flashcard_app_user_phone (tenant_id, phone, deleted),
    ADD UNIQUE KEY uk_flashcard_app_user_apple (tenant_id, apple_subject, deleted);
