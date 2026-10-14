-- ============================================================
-- ecommerce-monolith — initial schema
-- Write model (state-stored, one section per bounded context)
-- + read-model tables for the query side (CQRS)
-- ============================================================

-- ------------------------- CATALOG --------------------------
CREATE TABLE categories (
    id   VARCHAR(36)  NOT NULL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT uq_categories_name UNIQUE (name)
);

CREATE TABLE products (
    id          VARCHAR(36)   NOT NULL PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    price       DECIMAL(12, 2) NOT NULL,
    CONSTRAINT ck_products_price_positive CHECK (price > 0)
);

CREATE TABLE product_categories (
    product_id  VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_pc_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_pc_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- ------------------------ INVENTORY -------------------------
CREATE TABLE known_products (
    product_id VARCHAR(36)  NOT NULL PRIMARY KEY,
    title      VARCHAR(255)
);

CREATE TABLE inventory (
    product_id   VARCHAR(36) NOT NULL PRIMARY KEY,
    available_qty INT         NOT NULL,
    CONSTRAINT ck_inventory_available_non_negative CHECK (available_qty >= 0)
);

CREATE TABLE reservations (
    id       VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    status   VARCHAR(16) NOT NULL,
    CONSTRAINT uq_reservations_order UNIQUE (order_id)
);

CREATE TABLE reservation_items (
    id            BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reservation_id VARCHAR(36)  NOT NULL,
    product_id    VARCHAR(36)   NOT NULL,
    quantity      INT           NOT NULL,
    unit_price    DECIMAL(12, 2),
    CONSTRAINT fk_ri_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

-- -------------------------- ORDER ---------------------------
CREATE TABLE orders (
    id           VARCHAR(36)   NOT NULL PRIMARY KEY,
    user_id      VARCHAR(64)   NOT NULL,
    status       VARCHAR(32)   NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    street       VARCHAR(255)  NOT NULL,
    city         VARCHAR(128)  NOT NULL,
    zip          VARCHAR(32)   NOT NULL,
    country      VARCHAR(64)   NOT NULL,
    card_last4   VARCHAR(4),
    created_at   DATETIME(6),
    updated_at   DATETIME(6)
);

CREATE TABLE order_items (
    id         BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id   VARCHAR(36)    NOT NULL,
    product_id VARCHAR(36)    NOT NULL,
    quantity   INT            NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT ck_oi_quantity_positive CHECK (quantity >= 1)
);

-- ------------------------- PAYMENT --------------------------
CREATE TABLE payments (
    id            VARCHAR(36)    NOT NULL PRIMARY KEY,
    order_id      VARCHAR(36)    NOT NULL,
    amount        DECIMAL(12, 2) NOT NULL,
    status        VARCHAR(16)    NOT NULL,
    card_last4    VARCHAR(4),
    token         VARCHAR(128),
    failure_reason VARCHAR(255),
    created_at    DATETIME(6),
    CONSTRAINT uq_payments_order UNIQUE (order_id)
);

-- ------------------------- DELIVERY -------------------------
CREATE TABLE deliveries (
    id         VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id   VARCHAR(36) NOT NULL,
    status     VARCHAR(16) NOT NULL,
    street     VARCHAR(255),
    city       VARCHAR(128),
    zip        VARCHAR(32),
    country    VARCHAR(64),
    created_at DATETIME(6),
    CONSTRAINT uq_deliveries_order UNIQUE (order_id)
);

CREATE TABLE order_address_snapshots (
    order_id VARCHAR(36)  NOT NULL PRIMARY KEY,
    user_id  VARCHAR(64),
    street   VARCHAR(255) NOT NULL,
    city     VARCHAR(128) NOT NULL,
    zip      VARCHAR(32)  NOT NULL,
    country  VARCHAR(64)  NOT NULL
);

-- ================== READ MODELS (query side) =================

CREATE TABLE product_read_model (
    id          VARCHAR(36)   NOT NULL PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    description TEXT,
    price       DECIMAL(12, 2) NOT NULL,
    categories  VARCHAR(1024),
    created_at  DATETIME(6)
);

CREATE TABLE category_read_model (
    id   VARCHAR(36)  NOT NULL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    CONSTRAINT uq_crm_name UNIQUE (name)
);

CREATE TABLE inventory_read_model (
    product_id    VARCHAR(36) NOT NULL PRIMARY KEY,
    available_qty INT         NOT NULL,
    reserved_qty  INT         NOT NULL
);

CREATE TABLE order_read_model (
    id           VARCHAR(36)   NOT NULL PRIMARY KEY,
    user_id      VARCHAR(64)   NOT NULL,
    status       VARCHAR(32)   NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    items        TEXT,
    street       VARCHAR(255),
    city         VARCHAR(128),
    zip          VARCHAR(32),
    country      VARCHAR(64),
    created_at   DATETIME(6)
);

CREATE TABLE payment_read_model (
    id             VARCHAR(36)    NOT NULL PRIMARY KEY,
    order_id       VARCHAR(36)    NOT NULL,
    amount         DECIMAL(12, 2) NOT NULL,
    status         VARCHAR(16)    NOT NULL,
    card_last4     VARCHAR(4),
    failure_reason VARCHAR(255),
    created_at     DATETIME(6),
    CONSTRAINT uq_prm_order UNIQUE (order_id)
);

CREATE TABLE delivery_read_model (
    id         VARCHAR(36) NOT NULL PRIMARY KEY,
    order_id   VARCHAR(36) NOT NULL,
    status     VARCHAR(16) NOT NULL,
    street     VARCHAR(255),
    city       VARCHAR(128),
    zip        VARCHAR(32),
    country    VARCHAR(64),
    created_at DATETIME(6),
    CONSTRAINT uq_drm_order UNIQUE (order_id)
);
