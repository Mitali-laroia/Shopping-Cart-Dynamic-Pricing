-- Shopping Cart Dynamic Pricing Database Initialization Script
-- This script sets up the initial database schema and sample data

-- Create database if not exists (already handled by docker-compose environment)
-- CREATE DATABASE shopping_cart_db;

-- Connect to the database
\c shopping_cart_db;

-- Create schemas
CREATE SCHEMA IF NOT EXISTS shopping_cart;

-- Set default schema
SET search_path TO shopping_cart, public;

-- Create product categories enum
CREATE TYPE product_category AS ENUM ('Electronics', 'Books', 'Clothing');

-- Create loyalty levels enum  
CREATE TYPE loyalty_level AS ENUM ('Bronze', 'Silver', 'Gold');

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category product_category NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    description TEXT,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    loyalty_level loyalty_level NOT NULL DEFAULT 'Bronze',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create shopping_carts table
CREATE TABLE IF NOT EXISTS shopping_carts (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create cart_items table
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT NOT NULL REFERENCES shopping_carts(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cart_id, product_id)
);

-- Create discount_rules table for configuration
CREATE TABLE IF NOT EXISTS discount_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- 'ITEM_SPECIFIC', 'BULK', 'LOYALTY'
    category product_category,
    min_quantity INTEGER,
    min_cart_value DECIMAL(10,2),
    loyalty_level loyalty_level,
    discount_percentage DECIMAL(5,2) NOT NULL CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_loyalty_level ON customers(loyalty_level);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_customer_id ON shopping_carts(customer_id);
CREATE INDEX IF NOT EXISTS idx_shopping_carts_status ON shopping_carts(status);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id ON cart_items(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX IF NOT EXISTS idx_discount_rules_type ON discount_rules(rule_type);
CREATE INDEX IF NOT EXISTS idx_discount_rules_active ON discount_rules(is_active);

-- Insert sample products
INSERT INTO products (name, category, price, description, stock_quantity) VALUES
('Laptop Pro 15"', 'Electronics', 1000.00, 'High-performance laptop with 15-inch display', 50),
('Wireless Mouse', 'Electronics', 25.00, 'Ergonomic wireless mouse with precision tracking', 200),
('Smartphone X', 'Electronics', 800.00, 'Latest smartphone with advanced features', 100),
('Bluetooth Headphones', 'Electronics', 150.00, 'Premium wireless headphones with noise cancellation', 75),
('Programming Book', 'Books', 20.00, 'Complete guide to modern programming practices', 150),
('Design Patterns Book', 'Books', 35.00, 'Essential design patterns for software development', 80),
('Database Systems Book', 'Books', 45.00, 'Comprehensive guide to database design and implementation', 60),
('Cotton T-Shirt', 'Clothing', 25.00, '100% cotton comfortable t-shirt', 300),
('Denim Jeans', 'Clothing', 60.00, 'Classic fit denim jeans', 150),
('Running Shoes', 'Clothing', 90.00, 'Lightweight running shoes for daily exercise', 120);

-- Insert sample customers
INSERT INTO customers (name, email, loyalty_level) VALUES
('John Doe', 'john.doe@example.com', 'Silver'),
('Jane Smith', 'jane.smith@example.com', 'Gold'),
('Bob Wilson', 'bob.wilson@example.com', 'Bronze'),
('Alice Johnson', 'alice.johnson@example.com', 'Silver'),
('Charlie Brown', 'charlie.brown@example.com', 'Bronze');

-- Insert discount rules as per PRD requirements
INSERT INTO discount_rules (rule_name, rule_type, category, min_quantity, discount_percentage, is_active) VALUES
('Electronics Bulk Discount', 'ITEM_SPECIFIC', 'Electronics', 3, 15.0, true);

INSERT INTO discount_rules (rule_name, rule_type, min_cart_value, discount_percentage, is_active) VALUES
('Bulk Cart Discount', 'BULK', 200.00, 10.0, true);

INSERT INTO discount_rules (rule_name, rule_type, loyalty_level, discount_percentage, is_active) VALUES
('Bronze Loyalty Discount', 'LOYALTY', 'Bronze', 5.0, true),
('Silver Loyalty Discount', 'LOYALTY', 'Silver', 10.0, true),
('Gold Loyalty Discount', 'LOYALTY', 'Gold', 15.0, true);

-- Create a function to update timestamp columns
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update the updated_at column
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_shopping_carts_updated_at BEFORE UPDATE ON shopping_carts 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cart_items_updated_at BEFORE UPDATE ON cart_items 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Grant permissions
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA shopping_cart TO shopping_cart_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA shopping_cart TO shopping_cart_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA shopping_cart TO shopping_cart_user;