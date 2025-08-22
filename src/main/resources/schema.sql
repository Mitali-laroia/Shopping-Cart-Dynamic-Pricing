-- H2 Database Schema for Shopping Cart Dynamic Pricing Application

-- Create schemas
CREATE SCHEMA IF NOT EXISTS shopping_cart;

-- Create products table
CREATE TABLE IF NOT EXISTS shopping_cart.products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('ELECTRONICS', 'BOOKS', 'CLOTHING')),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    description TEXT,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create customers table
CREATE TABLE IF NOT EXISTS shopping_cart.customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    loyalty_level VARCHAR(50) NOT NULL DEFAULT 'BRONZE' CHECK (loyalty_level IN ('BRONZE', 'SILVER', 'GOLD')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create shopping_carts table
CREATE TABLE IF NOT EXISTS shopping_cart.shopping_carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT REFERENCES shopping_cart.customers(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS shopping_cart.cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES shopping_cart.shopping_carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES shopping_cart.products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cart_id, product_id)
);

-- Create discount_rules table for configuration
CREATE TABLE IF NOT EXISTS shopping_cart.discount_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- 'ITEM_SPECIFIC', 'BULK', 'LOYALTY'
    category VARCHAR(50) CHECK (category IN ('ELECTRONICS', 'BOOKS', 'CLOTHING')),
    min_quantity INTEGER,
    min_cart_value DECIMAL(10,2),
    loyalty_level VARCHAR(50) CHECK (loyalty_level IN ('BRONZE', 'SILVER', 'GOLD')),
    discount_percentage DECIMAL(5,2) NOT NULL CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_products_category ON shopping_cart.products(category);
CREATE INDEX IF NOT EXISTS idx_customers_email ON shopping_cart.customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_loyalty_level ON shopping_cart.customers(loyalty_level);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_customer_id ON shopping_cart.shopping_carts(customer_id);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_status ON shopping_cart.shopping_carts(status);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON shopping_cart.cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_id ON shopping_cart.cart_items(product_id);
CREATE INDEX IF NOT EXISTS idx_discount_rules_type ON shopping_cart.discount_rules(rule_type);
CREATE INDEX IF NOT EXISTS idx_discount_rules_active ON shopping_cart.discount_rules(is_active);