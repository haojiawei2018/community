-- 预约小程序最小表结构（与论坛共用 hope 数据库）
-- 所有预约业务表统一使用 booking_ 前缀；booking_user 独立保存预约小程序微信用户。
-- 本文件为手工执行脚本，应用启动时不会自动改动共享数据库。

CREATE TABLE IF NOT EXISTS booking_store (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    store_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    logo_url VARCHAR(500) DEFAULT NULL COMMENT '店铺Logo',
    address VARCHAR(255) DEFAULT NULL COMMENT '店铺地址',
    latitude DECIMAL(10, 7) DEFAULT NULL COMMENT '纬度',
    longitude DECIMAL(10, 7) DEFAULT NULL COMMENT '经度',
    business_hours VARCHAR(100) DEFAULT NULL COMMENT '营业时间展示文案',
    service_phone VARCHAR(30) DEFAULT NULL COMMENT '客服电话',
    slot_config TEXT COMMENT '每日档期JSON配置',
    advance_minutes INT NOT NULL DEFAULT 120 COMMENT '最少提前预约分钟数',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_booking_store_tenant_status (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约店铺';

-- 指定日期档期覆盖：有记录时该日期优先使用这里的档期；删除记录后恢复店铺默认每日档期。
CREATE TABLE IF NOT EXISTS booking_store_slot_override (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    store_id BIGINT NOT NULL COMMENT '店铺ID',
    slot_date DATE NOT NULL COMMENT '指定日期',
    slot_config TEXT NOT NULL COMMENT '指定日期档期JSON配置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_booking_store_slot_date (store_id, slot_date),
    KEY idx_booking_store_slot_tenant (tenant_id, store_id, slot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约店铺指定日期档期';

CREATE TABLE IF NOT EXISTS booking_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约小程序用户ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    iam_user_id BIGINT NOT NULL COMMENT '旧库兼容列，不关联iam_user',
    member_id BIGINT NOT NULL COMMENT '旧库兼容列，不关联tenant_member',
    app_id VARCHAR(64) NOT NULL COMMENT '微信小程序AppID',
    openid VARCHAR(128) NOT NULL COMMENT '微信openid',
    unionid VARCHAR(128) DEFAULT NULL COMMENT '微信unionid',
    nickname VARCHAR(100) DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_booking_user_openid (tenant_id, app_id, openid, deleted),
    UNIQUE KEY uk_booking_user_iam (tenant_id, iam_user_id, deleted),
    KEY idx_booking_user_member (tenant_id, member_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约小程序微信用户';

CREATE TABLE IF NOT EXISTS booking_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    icon_url VARCHAR(500) DEFAULT NULL COMMENT '分类图标或精灵图',
    icon_index INT NOT NULL DEFAULT 0 COMMENT '精灵图位置序号',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_booking_category_code (tenant_id, category_code, deleted),
    KEY idx_booking_category_sort (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约商品分类';

CREATE TABLE IF NOT EXISTS booking_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    store_id BIGINT NOT NULL COMMENT '所属店铺ID',
    category_id BIGINT NOT NULL COMMENT '所属分类ID',
    product_name VARCHAR(150) NOT NULL COMMENT '商品名称',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '列表封面或精灵图',
    cover_index INT NOT NULL DEFAULT 0 COMMENT '精灵图位置序号',
    gallery_urls TEXT COMMENT '详情轮播图JSON数组',
    description VARCHAR(1000) DEFAULT NULL COMMENT '商品简介',
    notice_content TEXT COMMENT '预定须知',
    unavailable_content TEXT COMMENT '店内不提供说明',
    sale_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
    deposit_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '预约定金',
    featured TINYINT NOT NULL DEFAULT 0 COMMENT '是否首页推荐',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/INACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_booking_product_store (tenant_id, store_id, status, deleted, sort_order),
    KEY idx_booking_product_category (tenant_id, category_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约商品';

CREATE TABLE IF NOT EXISTS booking_appointment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    tenant_id BIGINT NOT NULL COMMENT '社区/租户ID',
    appointment_no VARCHAR(40) NOT NULL COMMENT '预约单号',
    user_id BIGINT NOT NULL COMMENT 'iam_user.id',
    member_id BIGINT NOT NULL COMMENT 'tenant_member.id',
    store_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL COMMENT '到店日期',
    slot_time TIME NOT NULL COMMENT '到店档期',
    people_count INT NOT NULL DEFAULT 1,
    sale_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
    deposit_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '本次支付定金',
    remark VARCHAR(500) DEFAULT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT 'PENDING_PAYMENT/CONFIRMED/REFUND_PENDING/CANCELLED',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'UNPAID/PAID/REFUNDED',
    transaction_id VARCHAR(100) DEFAULT NULL COMMENT '微信支付单号',
    expires_at DATETIME DEFAULT NULL COMMENT '待支付占位过期时间',
    paid_at DATETIME DEFAULT NULL,
    cancelled_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_booking_appointment_no (appointment_no),
    KEY idx_booking_appointment_slot (tenant_id, store_id, appointment_date, slot_time, status, deleted),
    KEY idx_booking_appointment_member (tenant_id, member_id, status, deleted, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约单';

-- 以下为与当前静态页面一致的演示数据。资源地址暂用小程序本地 static 路径，接入上传后可改为 OSS URL。
SET @booking_slots = '[{"time":"07:30","capacity":2},{"time":"08:30","capacity":2},{"time":"10:30","capacity":2},{"time":"12:30","capacity":2},{"time":"14:30","capacity":2},{"time":"16:30","capacity":2},{"time":"17:30","capacity":2}]';
SET @booking_gallery = '["/static/booking/hanfu-hero-green.jpg","/static/booking/hanfu-hero.jpg","/static/booking/hanfu-hero-pink.jpg"]';
SET @booking_notice = '亲爱的客户，为了确保您能获得最佳服务体验，请在预约下单之前仔细阅读并确认。预约时间为到店时间，并非化妆开始时间；请预留挑选服装和试穿时间。暑假高峰期可能存在等候，请耐心等待。预约到店前24小时内无法退款，请慎重下单。';

INSERT INTO booking_store
    (id, tenant_id, store_name, logo_url, address, latitude, longitude, business_hours, service_phone,
     slot_config, advance_minutes, status, sort_order, deleted)
VALUES
    (1001, 1, '大喜汉服【旗舰店】', NULL, '山东济南市历下区县西巷6号', 36.6660000, 117.0200000,
     '08:30-21:00', '18660123456', @booking_slots, 120, 'ACTIVE', 1, 0),
    (1002, 1, '大喜汉服【曲水亭街】', NULL, '山东济南市历下区辘轳把子街22号大喜汉服', 36.6710000, 117.0250000,
     '08:30-21:00', '18660123456', @booking_slots, 120, 'ACTIVE', 2, 0)
ON DUPLICATE KEY UPDATE
    store_name = VALUES(store_name), logo_url = VALUES(logo_url), address = VALUES(address), business_hours = VALUES(business_hours),
    service_phone = VALUES(service_phone), slot_config = VALUES(slot_config), advance_minutes = VALUES(advance_minutes),
    status = VALUES(status), sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO booking_category
    (id, tenant_id, category_code, category_name, icon_url, icon_index, status, sort_order, deleted)
VALUES
    (1101, 1, 'XIAOTANG', '小唐风¥169', '/static/booking/hanfu-products.jpg', 0, 'ACTIVE', 1, 0),
    (1102, 1, 'AIMAO', '埃及猫¥269', '/static/booking/hanfu-products.jpg', 1, 'ACTIVE', 2, 0),
    (1103, 1, 'DATANG', '大唐风¥269', '/static/booking/hanfu-products.jpg', 2, 'ACTIVE', 3, 0),
    (1104, 1, 'YUJI', '虞姬小唯¥169', '/static/booking/hanfu-products.jpg', 3, 'ACTIVE', 4, 0),
    (1105, 1, 'GAIHUA', '大爆改花神¥269', '/static/booking/hanfu-products.jpg', 4, 'ACTIVE', 5, 0),
    (1106, 1, 'HUASHEN', '帷帽花神¥269', '/static/booking/hanfu-products.jpg', 5, 'ACTIVE', 6, 0),
    (1107, 1, 'MERMAID', '人鱼公主¥269', '/static/booking/hanfu-products.jpg', 6, 'ACTIVE', 7, 0),
    (1108, 1, 'MORE', '更多', '/static/booking/hanfu-products.jpg', 7, 'ACTIVE', 8, 0)
ON DUPLICATE KEY UPDATE
    category_name = VALUES(category_name), icon_url = VALUES(icon_url), icon_index = VALUES(icon_index),
    status = VALUES(status), sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO booking_product
    (id, tenant_id, store_id, category_id, product_name, cover_url, cover_index, gallery_urls,
     description, notice_content, unavailable_content, sale_price, deposit_amount, featured, status, sort_order, deleted)
VALUES
    (1201, 1, 1001, 1101, '小唐风', '/static/booking/hanfu-products.jpg', 0, @booking_gallery, '汉服妆造套餐', @booking_notice, '胸贴、汉服鞋', 169.00, 50.00, 1, 'ACTIVE', 1, 0),
    (1202, 1, 1001, 1102, '埃及猫', '/static/booking/hanfu-products.jpg', 1, @booking_gallery, '主题妆造套餐', @booking_notice, '胸贴、汉服鞋', 269.00, 50.00, 1, 'ACTIVE', 2, 0),
    (1203, 1, 1001, 1103, '大唐风', '/static/booking/hanfu-products.jpg', 2, @booking_gallery, '大唐风汉服妆造套餐', @booking_notice, '胸贴、汉服鞋', 269.00, 50.00, 1, 'ACTIVE', 3, 0),
    (1204, 1, 1001, 1104, '虞姬小唯', '/static/booking/hanfu-products.jpg', 3, @booking_gallery, '古风汉服妆造套餐', @booking_notice, '胸贴、汉服鞋', 169.00, 50.00, 1, 'ACTIVE', 4, 0),
    (1205, 1, 1001, 1105, '大爆改花神', '/static/booking/hanfu-products.jpg', 4, @booking_gallery, '花神主题妆造套餐', @booking_notice, '胸贴、汉服鞋', 269.00, 50.00, 1, 'ACTIVE', 5, 0),
    (1206, 1, 1001, 1106, '帷帽花神', '/static/booking/hanfu-products.jpg', 5, @booking_gallery, '帷帽花神主题妆造套餐', @booking_notice, '胸贴、汉服鞋', 269.00, 50.00, 1, 'ACTIVE', 6, 0),
    (1207, 1, 1001, 1107, '人鱼公主', '/static/booking/hanfu-products.jpg', 6, @booking_gallery, '人鱼公主主题妆造套餐', @booking_notice, '胸贴、汉服鞋', 269.00, 50.00, 1, 'ACTIVE', 7, 0),
    (1208, 1, 1001, 1108, '高定十二星座', '/static/booking/hanfu-products.jpg', 7, @booking_gallery, '高定主题妆造套餐', @booking_notice, '胸贴、汉服鞋', 299.00, 50.00, 0, 'ACTIVE', 8, 0)
ON DUPLICATE KEY UPDATE
    store_id = VALUES(store_id), category_id = VALUES(category_id), product_name = VALUES(product_name),
    cover_url = VALUES(cover_url), cover_index = VALUES(cover_index), gallery_urls = VALUES(gallery_urls),
    description = VALUES(description), notice_content = VALUES(notice_content), unavailable_content = VALUES(unavailable_content),
    sale_price = VALUES(sale_price), deposit_amount = VALUES(deposit_amount), featured = VALUES(featured),
    status = VALUES(status), sort_order = VALUES(sort_order), deleted = 0;

