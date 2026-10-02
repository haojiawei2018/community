-- 闪卡正式账号与物理设备解绑：同一设备可注册、登录多个账号。
-- device_id 仅用于兼容旧版游客登录，闪卡仍只通过 creator_user_id 关联内部用户ID。
ALTER TABLE flashcard_app_user
    DROP INDEX uk_flashcard_app_user_device,
    MODIFY COLUMN device_id VARCHAR(128) DEFAULT NULL COMMENT '仅兼容旧版设备游客登录，正式账号不绑定设备',
    ADD KEY idx_flashcard_app_user_device (tenant_id, device_id, deleted);
