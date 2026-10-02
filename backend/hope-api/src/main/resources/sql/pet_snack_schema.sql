-- 宠物零食小程序独立业务表。与社区、预约、闪卡业务完全隔离。
-- 本脚本需要手工执行，应用启动时不会自动修改数据库。

CREATE TABLE IF NOT EXISTS pet_snack_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    app_id VARCHAR(64) NOT NULL,
    openid VARCHAR(128) NOT NULL,
    unionid VARCHAR(128) DEFAULT NULL,
    nickname VARCHAR(100) DEFAULT NULL,
    avatar_url VARCHAR(500) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_ip VARCHAR(64) DEFAULT NULL,
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_snack_user_openid (tenant_id, app_id, openid, deleted),
    KEY idx_pet_snack_user_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食小程序微信用户';

CREATE TABLE IF NOT EXISTS pet_snack_store (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    store_name VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    service_tags TEXT COMMENT '服务标签JSON数组',
    service_phone VARCHAR(30) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_store (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食店铺';

CREATE TABLE IF NOT EXISTS pet_snack_banner (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    subtitle VARCHAR(255) DEFAULT NULL,
    image_url VARCHAR(500) NOT NULL,
    link_type VARCHAR(30) NOT NULL DEFAULT 'NONE',
    link_value VARCHAR(100) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_banner (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食首页轮播图';

CREATE TABLE IF NOT EXISTS pet_snack_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    icon_url VARCHAR(500) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_snack_category_code (tenant_id, category_code, deleted),
    KEY idx_pet_snack_category (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食商品分类';

CREATE TABLE IF NOT EXISTS pet_snack_product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    cover_url VARCHAR(500) DEFAULT NULL,
    gallery_urls TEXT COMMENT '详情图JSON数组',
    description VARCHAR(1000) DEFAULT NULL,
    purchase_notice TEXT,
    sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    market_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    sales_count INT NOT NULL DEFAULT 0,
    hot TINYINT NOT NULL DEFAULT 0,
    new_product TINYINT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_product_category (tenant_id, category_id, status, deleted, sort_order),
    KEY idx_pet_snack_product_home (tenant_id, hot, new_product, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食商品';

CREATE TABLE IF NOT EXISTS pet_snack_product_sku (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(64) NOT NULL,
    sku_name VARCHAR(100) NOT NULL,
    sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    market_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_snack_sku_code (tenant_id, sku_code, deleted),
    KEY idx_pet_snack_sku_product (tenant_id, product_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食商品规格库存';

CREATE TABLE IF NOT EXISTS pet_snack_address (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(30) NOT NULL,
    province VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) DEFAULT NULL,
    is_default TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_address_user (tenant_id, user_id, deleted, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食用户收货地址';

CREATE TABLE IF NOT EXISTS pet_snack_coupon (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    coupon_name VARCHAR(100) NOT NULL,
    threshold_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    valid_from DATETIME NOT NULL,
    valid_to DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_coupon_valid (tenant_id, status, deleted, valid_from, valid_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食优惠券模板';

CREATE TABLE IF NOT EXISTS pet_snack_user_coupon (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    used_order_id BIGINT DEFAULT NULL,
    used_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_user_coupon (tenant_id, user_id, status, deleted),
    KEY idx_pet_snack_user_coupon_order (tenant_id, used_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食用户优惠券';

CREATE TABLE IF NOT EXISTS pet_snack_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    order_no VARCHAR(40) NOT NULL,
    client_request_no VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    user_coupon_id BIGINT DEFAULT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(30) NOT NULL,
    receiver_address VARCHAR(500) NOT NULL,
    remark VARCHAR(500) DEFAULT NULL,
    product_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    freight_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_PAYMENT',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    transaction_id VARCHAR(100) DEFAULT NULL,
    expires_at DATETIME DEFAULT NULL,
    paid_at DATETIME DEFAULT NULL,
    cancelled_at DATETIME DEFAULT NULL,
    completed_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_snack_order_no (tenant_id, order_no),
    UNIQUE KEY uk_pet_snack_order_request (tenant_id, user_id, client_request_no, deleted),
    KEY idx_pet_snack_order_user (tenant_id, user_id, status, deleted, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食订单';

CREATE TABLE IF NOT EXISTS pet_snack_order_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    sku_name VARCHAR(100) NOT NULL,
    cover_url VARCHAR(500) DEFAULT NULL,
    unit_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantity INT NOT NULL DEFAULT 1,
    subtotal_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_order_item (tenant_id, order_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食订单商品快照';

CREATE TABLE IF NOT EXISTS pet_snack_refund (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    refund_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_snack_refund_no (tenant_id, refund_no),
    KEY idx_pet_snack_refund_order (tenant_id, order_id, user_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食退款售后单';

CREATE TABLE IF NOT EXISTS pet_snack_feedback (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    order_id BIGINT DEFAULT NULL,
    feedback_type VARCHAR(30) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    image_urls TEXT,
    contact VARCHAR(100) DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_feedback_user (tenant_id, user_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食问题反馈';

CREATE TABLE IF NOT EXISTS pet_snack_help_article (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    summary VARCHAR(255) DEFAULT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pet_snack_help (tenant_id, status, deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物零食帮助中心';

-- 与当前静态小程序一致的初始化数据。tenant_id 默认按当前单社区配置使用 1。
INSERT INTO pet_snack_store
    (id, tenant_id, store_name, logo_url, description, service_tags, service_phone, status, sort_order, deleted)
VALUES
    (1, 1, 'PawPaw 宠物零食铺', '/static/pet-banners/dog-snacks.jpg',
     '精选天然好原料，坚持少添加，为猫猫狗狗准备安心又美味的每日奖励。',
     '["品质严选","每日发货"]', NULL, 'ACTIVE', 1, 0)
ON DUPLICATE KEY UPDATE store_name = VALUES(store_name), logo_url = VALUES(logo_url),
    description = VALUES(description), service_tags = VALUES(service_tags), status = 'ACTIVE', deleted = 0;

INSERT INTO pet_snack_category
    (id, tenant_id, category_code, category_name, status, sort_order, deleted)
VALUES
    (1, 1, 'RECOMMEND', '推荐', 'ACTIVE', 1, 0),
    (2, 1, 'DOG', '狗狗零食', 'ACTIVE', 2, 0),
    (3, 1, 'CAT', '猫咪零食', 'ACTIVE', 3, 0),
    (4, 1, 'MEAT', '肉干肉条', 'ACTIVE', 4, 0),
    (5, 1, 'FREEZE_DRIED', '冻干零食', 'ACTIVE', 5, 0),
    (6, 1, 'DENTAL', '磨牙洁齿', 'ACTIVE', 6, 0),
    (7, 1, 'TRAINING', '训练奖励', 'ACTIVE', 7, 0),
    (8, 1, 'NUTRITION', '营养补充', 'ACTIVE', 8, 0),
    (9, 1, 'GIFT', '零食礼盒', 'ACTIVE', 9, 0)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name), status = 'ACTIVE',
    sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO pet_snack_banner
    (id, tenant_id, title, subtitle, image_url, link_type, link_value, status, sort_order, deleted)
VALUES
    (1, 1, '鲜肉好零食', '真材实料，毛孩子吃得更开心', '/static/pet-banners/dog-snacks.jpg', 'PRODUCT', '1', 'ACTIVE', 1, 0),
    (2, 1, '冻干上新', '鸡肉与三文鱼的双重鲜香', '/static/pet-banners/cat-snacks.jpg', 'PRODUCT', '2', 'ACTIVE', 2, 0),
    (3, 1, '健康囤货季', '犬猫零食一次选齐', '/static/pet-banners/healthy-snacks.jpg', 'CATEGORY', '1', 'ACTIVE', 3, 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), subtitle = VALUES(subtitle),
    image_url = VALUES(image_url), link_type = VALUES(link_type), link_value = VALUES(link_value),
    status = 'ACTIVE', sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO pet_snack_product
    (id, tenant_id, category_id, product_name, cover_url, gallery_urls, description, purchase_notice,
     sale_price, market_price, sales_count, hot, new_product, status, sort_order, deleted)
VALUES
    (1, 1, 2, '低温烘焙鸡胸肉干 100g', '/static/pet-banners/dog-snacks.jpg', '["/static/pet-banners/dog-snacks.jpg"]', '原切鸡胸肉，低温慢烘', '请存放于阴凉干燥处，开封后尽快食用。', 29.90, 35.90, 326, 1, 0, 'ACTIVE', 1, 0),
    (2, 1, 3, '原切三文鱼冻干粒 80g', '/static/pet-banners/cat-snacks.jpg', '["/static/pet-banners/cat-snacks.jpg"]', '三文鱼原切冻干，酥脆鲜香', '三个月以下幼宠请在家长陪同下食用。', 35.90, 42.90, 268, 1, 1, 'ACTIVE', 2, 0),
    (3, 1, 6, '洁齿磨牙骨混合装', '/static/pet-banners/healthy-snacks.jpg', '["/static/pet-banners/healthy-snacks.jpg"]', '日常磨牙洁齿训练奖励', '请根据宠物体型选择合适规格。', 19.90, 25.90, 189, 1, 0, 'ACTIVE', 3, 0),
    (4, 1, 4, '鸭肉条犬猫通用零食', '/static/pet-banners/healthy-snacks.jpg', '["/static/pet-banners/healthy-snacks.jpg"]', '肉香浓郁，软硬适中', '开封后请密封保存。', 42.00, 49.90, 156, 1, 0, 'ACTIVE', 4, 0),
    (5, 1, 5, '新品冻干鸡肉小方块', '/static/pet-banners/cat-snacks.jpg', '["/static/pet-banners/cat-snacks.jpg"]', '方便喂食的小方块冻干', '可直接喂食或复水后拌粮。', 39.90, 45.90, 52, 0, 1, 'ACTIVE', 5, 0),
    (6, 1, 9, '犬猫零食尝鲜礼盒', '/static/pet-banners/healthy-snacks.jpg', '["/static/pet-banners/healthy-snacks.jpg"]', '多种口味一次尝鲜', '具体口味以礼盒内商品标签为准。', 45.90, 59.90, 24, 0, 1, 'ACTIVE', 6, 0)
ON DUPLICATE KEY UPDATE product_name = VALUES(product_name), cover_url = VALUES(cover_url),
    gallery_urls = VALUES(gallery_urls), description = VALUES(description), purchase_notice = VALUES(purchase_notice),
    sale_price = VALUES(sale_price), market_price = VALUES(market_price), sales_count = VALUES(sales_count),
    hot = VALUES(hot), new_product = VALUES(new_product), status = 'ACTIVE', sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO pet_snack_product_sku
    (id, tenant_id, product_id, sku_code, sku_name, sale_price, market_price, stock, status, sort_order, deleted)
VALUES
    (1, 1, 1, 'PS-CHICKEN-100', '100g/袋', 29.90, 35.90, 200, 'ACTIVE', 1, 0),
    (2, 1, 2, 'PS-SALMON-80', '80g/袋', 35.90, 42.90, 160, 'ACTIVE', 1, 0),
    (3, 1, 3, 'PS-DENTAL-MIX', '混合装', 19.90, 25.90, 300, 'ACTIVE', 1, 0),
    (4, 1, 4, 'PS-DUCK-100', '100g/袋', 42.00, 49.90, 120, 'ACTIVE', 1, 0),
    (5, 1, 5, 'PS-CHICKEN-CUBE', '80g/袋', 39.90, 45.90, 100, 'ACTIVE', 1, 0),
    (6, 1, 6, 'PS-GIFT-TRIAL', '尝鲜礼盒', 45.90, 59.90, 80, 'ACTIVE', 1, 0)
ON DUPLICATE KEY UPDATE product_id = VALUES(product_id), sku_name = VALUES(sku_name),
    sale_price = VALUES(sale_price), market_price = VALUES(market_price), stock = VALUES(stock),
    status = 'ACTIVE', sort_order = VALUES(sort_order), deleted = 0;

INSERT INTO pet_snack_help_article
    (id, tenant_id, title, summary, content, status, sort_order, deleted)
VALUES
    (1, 1, '如何保存宠物零食？', '开封后的保存方式', '请将零食放在阴凉干燥处，开封后密封保存并尽快食用。', 'ACTIVE', 1, 0),
    (2, 1, '幼宠可以食用吗？', '幼宠喂食注意事项', '三个月以下幼宠或肠胃敏感宠物，请先咨询专业兽医并少量尝试。', 'ACTIVE', 2, 0),
    (3, 1, '订单与售后说明', '发货、收货与售后', '订单支付后进入备货流程。如发现包装破损或商品异常，请在问题反馈中提交订单和图片。', 'ACTIVE', 3, 0)
ON DUPLICATE KEY UPDATE title = VALUES(title), summary = VALUES(summary), content = VALUES(content),
    status = 'ACTIVE', sort_order = VALUES(sort_order), deleted = 0;
