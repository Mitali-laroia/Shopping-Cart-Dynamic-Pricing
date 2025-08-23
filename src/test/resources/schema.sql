-- Drop existing tables and schema if they exist
DROP SCHEMA IF EXISTS shopping_cart CASCADE;

-- Create schema for H2 test database
CREATE SCHEMA IF NOT EXISTS shopping_cart;

-- Customers table
CREATE TABLE shopping_cart.customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    loyalty_level VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE shopping_cart.products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shopping carts table
CREATE TABLE shopping_cart.shopping_carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES shopping_cart.customers(id)
);

-- Cart items table
CREATE TABLE shopping_cart.cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES shopping_cart.shopping_carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES shopping_cart.products(id),
    UNIQUE(cart_id, product_id)
);

-- Create indexes for better performance
CREATE INDEX idx_customers_email ON shopping_cart.customers(email);
CREATE INDEX idx_customers_loyalty ON shopping_cart.customers(loyalty_level);
CREATE INDEX idx_products_category ON shopping_cart.products(category);
CREATE INDEX idx_products_name ON shopping_cart.products(name);
CREATE INDEX idx_carts_customer ON shopping_cart.shopping_carts(customer_id);
CREATE INDEX idx_carts_status ON shopping_cart.shopping_carts(status);
CREATE INDEX idx_cart_items_cart ON shopping_cart.cart_items(cart_id);
CREATE INDEX idx_cart_items_product ON shopping_cart.cart_items(product_id);