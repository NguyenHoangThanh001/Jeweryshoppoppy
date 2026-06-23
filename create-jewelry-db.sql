-- ============================================================
-- MySQL Schema generated from Spring Boot Entity Models
-- ============================================================

CREATE DATABASE IF NOT EXISTS `create-jewelry-db`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `create-jewelry-db`;

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- account
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS account (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    full_name    VARCHAR(255),
    email        VARCHAR(255) UNIQUE,
    phone        VARCHAR(255) UNIQUE,
    password     VARCHAR(255),
    role         VARCHAR(50),
    point        INT          NOT NULL DEFAULT 0,
    account_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- cashier
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cashier (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(255),
    is_deleted TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- category
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255),
    description TEXT,
    is_deleted  TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- material
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS material (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    name           VARCHAR(255),
    description    TEXT,
    diamond_origin VARCHAR(255),
    create_at      DATETIME,
    is_deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- certificate
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS certificate (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    number       VARCHAR(255),
    url          VARCHAR(500),
    expired_date DATE,
    start_at     DATE,
    is_deleted   TINYINT(1)   NOT NULL DEFAULT 0,
    material_id  BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_certificate_material FOREIGN KEY (material_id) REFERENCES material (id)
);

-- ------------------------------------------------------------
-- product
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255),
    description TEXT,
    price       FLOAT         NOT NULL DEFAULT 0,
    code        VARCHAR(255),
    quantity    INT           NOT NULL DEFAULT 0,
    image       VARCHAR(500),
    is_deleted  TINYINT(1)    NOT NULL DEFAULT 0,
    category_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- ------------------------------------------------------------
-- product_material  (Material <-> Product many-to-many)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_material (
    material_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    PRIMARY KEY (material_id, product_id),
    CONSTRAINT fk_pm_material FOREIGN KEY (material_id) REFERENCES material (id),
    CONSTRAINT fk_pm_product  FOREIGN KEY (product_id)  REFERENCES product  (id)
);

-- ------------------------------------------------------------
-- size
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS size (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255),
    description TEXT,
    is_deleted  TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- product_size  (Size <-> Product many-to-many)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_size (
    size_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (size_id, product_id),
    CONSTRAINT fk_ps_size    FOREIGN KEY (size_id)    REFERENCES size    (id),
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES product (id)
);

-- ------------------------------------------------------------
-- voucher
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS voucher (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    code       VARCHAR(255),
    start_at   DATETIME,
    end_at     DATETIME,
    create_at  DATETIME,
    value      FLOAT        NOT NULL DEFAULT 0,
    is_deleted TINYINT(1)   NOT NULL DEFAULT 0,
    manager_id BIGINT,
    order_id   BIGINT,          -- FK set after orders table
    PRIMARY KEY (id),
    CONSTRAINT fk_voucher_manager FOREIGN KEY (manager_id) REFERENCES account (id)
);

-- ------------------------------------------------------------
-- shift
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shift (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    from_time  DATETIME,
    to_time    DATETIME,
    status     VARCHAR(50),
    staff_id   BIGINT,
    cashier_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_shift_staff   FOREIGN KEY (staff_id)   REFERENCES account (id),
    CONSTRAINT fk_shift_cashier FOREIGN KEY (cashier_id) REFERENCES cashier (id)
);

-- ------------------------------------------------------------
-- orders
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    create_at    DATETIME,
    total_amount FLOAT        NOT NULL DEFAULT 0,
    status       VARCHAR(50),
    description  TEXT,
    point        INT          NOT NULL DEFAULT 0,
    account_id   BIGINT,
    customer_id  BIGINT,
    shift_id     BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_created_by FOREIGN KEY (account_id)  REFERENCES account (id),
    CONSTRAINT fk_orders_customer   FOREIGN KEY (customer_id) REFERENCES account (id),
    CONSTRAINT fk_orders_shift      FOREIGN KEY (shift_id)    REFERENCES shift   (id)
);

-- Now we can add the FK from voucher -> orders
ALTER TABLE voucher
    ADD CONSTRAINT fk_voucher_order FOREIGN KEY (order_id) REFERENCES orders (id);

-- ------------------------------------------------------------
-- order_buy
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_buy (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    total       FLOAT  NOT NULL DEFAULT 0,
    customer_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_buy_customer FOREIGN KEY (customer_id) REFERENCES account (id)
);

-- ------------------------------------------------------------
-- order_detail
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_detail (
    id          BIGINT     NOT NULL AUTO_INCREMENT,
    quantity    INT        NOT NULL DEFAULT 0,
    is_buy_back TINYINT(1) NOT NULL DEFAULT 0,
    order_id    BIGINT,
    product_id  BIGINT,
    size_id     BIGINT,
    order_buy_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_od_order     FOREIGN KEY (order_id)     REFERENCES orders     (id),
    CONSTRAINT fk_od_product   FOREIGN KEY (product_id)   REFERENCES product    (id),
    CONSTRAINT fk_od_size      FOREIGN KEY (size_id)      REFERENCES size       (id),
    CONSTRAINT fk_od_order_buy FOREIGN KEY (order_buy_id) REFERENCES order_buy  (id)
);

-- ------------------------------------------------------------
-- guarantee
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS guarantee (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    start_at        DATETIME,
    end_at          DATETIME,
    status          VARCHAR(50),
    order_detail_id BIGINT UNIQUE,    -- OneToOne
    PRIMARY KEY (id),
    CONSTRAINT fk_guarantee_order_detail FOREIGN KEY (order_detail_id) REFERENCES order_detail (id)
);

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- transaction
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `transaction` (
    id              CHAR(36)     NOT NULL,
    username        VARCHAR(255),
    billing_address VARCHAR(255),
    total_money     DOUBLE       NOT NULL DEFAULT 0,
    generated_at    DATETIME,
    payment_status  VARCHAR(50),
    order_id        BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_transaction_order FOREIGN KEY (order_id) REFERENCES orders (id)
);
