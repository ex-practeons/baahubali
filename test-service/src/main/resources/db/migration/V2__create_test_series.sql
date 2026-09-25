CREATE TABLE test_series (
    id VARCHAR(36) PRIMARY KEY,
    category_id VARCHAR(36),
    title VARCHAR(255) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    is_free BOOLEAN DEFAULT FALSE,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
