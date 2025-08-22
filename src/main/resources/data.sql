-- Sample data for Shopping Cart Dynamic Pricing Application

-- Insert sample products
INSERT INTO shopping_cart.products (name, category, price, description, stock_quantity) VALUES
('Laptop Pro 15"', 'ELECTRONICS', 1000.00, 'High-performance laptop with 15-inch display', 50),
('Wireless Mouse', 'ELECTRONICS', 25.00, 'Ergonomic wireless mouse with precision tracking', 200),
('Smartphone X', 'ELECTRONICS', 800.00, 'Latest smartphone with advanced features', 100),
('Bluetooth Headphones', 'ELECTRONICS', 150.00, 'Premium wireless headphones with noise cancellation', 75),
('Programming Book', 'BOOKS', 20.00, 'Complete guide to modern programming practices', 150),
('Design Patterns Book', 'BOOKS', 35.00, 'Essential design patterns for software development', 80),
('Database Systems Book', 'BOOKS', 45.00, 'Comprehensive guide to database design and implementation', 60),
('Cotton T-Shirt', 'CLOTHING', 25.00, '100% cotton comfortable t-shirt', 300),
('Denim Jeans', 'CLOTHING', 60.00, 'Classic fit denim jeans', 150),
('Running Shoes', 'CLOTHING', 90.00, 'Lightweight running shoes for daily exercise', 120);

-- Insert sample customers
INSERT INTO shopping_cart.customers (name, email, loyalty_level) VALUES
('John Doe', 'john.doe@example.com', 'SILVER'),
('Jane Smith', 'jane.smith@example.com', 'GOLD'),
('Bob Wilson', 'bob.wilson@example.com', 'BRONZE'),
('Alice Johnson', 'alice.johnson@example.com', 'SILVER'),
('Charlie Brown', 'charlie.brown@example.com', 'BRONZE');

-- Insert discount rules as per PRD requirements
INSERT INTO shopping_cart.discount_rules (rule_name, rule_type, category, min_quantity, discount_percentage, is_active) VALUES
('Electronics Bulk Discount', 'ITEM_SPECIFIC', 'ELECTRONICS', 3, 15.0, true);

INSERT INTO shopping_cart.discount_rules (rule_name, rule_type, min_cart_value, discount_percentage, is_active) VALUES
('Bulk Cart Discount', 'BULK', 200.00, 10.0, true);

INSERT INTO shopping_cart.discount_rules (rule_name, rule_type, loyalty_level, discount_percentage, is_active) VALUES
('Bronze Loyalty Discount', 'LOYALTY', 'BRONZE', 5.0, true),
('Silver Loyalty Discount', 'LOYALTY', 'SILVER', 10.0, true),
('Gold Loyalty Discount', 'LOYALTY', 'GOLD', 15.0, true);