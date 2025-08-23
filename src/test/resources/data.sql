-- Test data for H2 database

-- Insert test customers
INSERT INTO shopping_cart.customers (id, name, email, loyalty_level, created_at, updated_at) VALUES
(1, 'John Doe', 'john.doe@example.com', 'BRONZE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Jane Smith', 'jane.smith@example.com', 'SILVER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Bob Johnson', 'bob.johnson@example.com', 'GOLD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Alice Brown', 'alice.brown@example.com', 'BRONZE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Charlie Wilson', 'charlie.wilson@example.com', 'SILVER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test products
INSERT INTO shopping_cart.products (id, name, description, category, price, stock_quantity, created_at, updated_at) VALUES
-- Electronics
(1, 'Laptop', 'High-performance laptop', 'Electronics', 1000.00, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Smartphone', 'Latest smartphone model', 'Electronics', 800.00, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tablet', 'Portable tablet device', 'Electronics', 400.00, 75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Headphones', 'Wireless noise-canceling headphones', 'Electronics', 150.00, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Books
(5, 'Java Programming', 'Complete guide to Java programming', 'Books', 45.99, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Spring Boot in Action', 'Learn Spring Boot framework', 'Books', 39.99, 250, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Design Patterns', 'Software design patterns explained', 'Books', 55.00, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Clean Code', 'Writing maintainable code', 'Books', 42.50, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Clothing
(9, 'T-Shirt', 'Cotton casual t-shirt', 'Clothing', 25.00, 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Jeans', 'Blue denim jeans', 'Clothing', 80.00, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Sweater', 'Warm wool sweater', 'Clothing', 60.00, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Shoes', 'Running shoes', 'Clothing', 120.00, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test shopping carts
INSERT INTO shopping_cart.shopping_carts (id, customer_id, status, total_amount, created_at, updated_at) VALUES
(1, 1, 'ACTIVE', 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 'ACTIVE', 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 'COMPLETED', 1200.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 'ACTIVE', 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 5, 'ABANDONED', 450.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test cart items
INSERT INTO shopping_cart.cart_items (id, cart_id, product_id, quantity, unit_price, total_price, created_at, updated_at) VALUES
-- Cart 1 (John Doe - Bronze) - Electronics items for testing quantity discount
(1, 1, 1, 1, 1000.00, 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 4, 3, 150.00, 450.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cart 2 (Jane Smith - Silver) - Mixed items for bulk discount testing
(3, 2, 2, 1, 800.00, 800.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, 5, 2, 45.99, 91.98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, 9, 3, 25.00, 75.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cart 3 (Bob Johnson - Gold) - Completed cart
(6, 3, 1, 1, 1000.00, 1000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 3, 6, 5, 39.99, 199.95, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cart 5 (Charlie Wilson - Silver) - Abandoned cart
(8, 5, 3, 1, 400.00, 400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 5, 10, 1, 80.00, 80.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update cart totals based on items
UPDATE shopping_cart.shopping_carts SET total_amount = 1450.00, updated_at = CURRENT_TIMESTAMP WHERE id = 1;
UPDATE shopping_cart.shopping_carts SET total_amount = 966.98, updated_at = CURRENT_TIMESTAMP WHERE id = 2;
UPDATE shopping_cart.shopping_carts SET total_amount = 480.00, updated_at = CURRENT_TIMESTAMP WHERE id = 5;