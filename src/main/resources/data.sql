INSERT INTO product (id, product_name, description, price, stock_quantity, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'iPhone 16 Pro', 'Latest Apple iPhone', 1099, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'AirPod Pro', 'Noise cancelling wireless headphones', 299, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO discount (id, discount_name, discount_type, discount_value, start_date, end_date, product_id, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Buy 1 Get 50% Off Second', 'PERCENTAGE', 50.00,
     CURRENT_TIMESTAMP, DATEADD('MONTH', 1, CURRENT_TIMESTAMP), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart (id, user_id, status, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', '00000000-0000-0000-0000-000000001001', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO cart_item (id, cart_id, product_id, quantity, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);