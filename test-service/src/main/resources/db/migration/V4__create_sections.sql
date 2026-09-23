CREATE TABLE sections (
    id VARCHAR(36) PRIMARY KEY,
    test_id VARCHAR(36),
    title VARCHAR(255) NOT NULL,
    sequence_order INT NOT NULL,
    shuffle_questions BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (test_id) REFERENCES mock_tests(id)
);
