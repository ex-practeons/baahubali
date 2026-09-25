CREATE TABLE mock_tests (
    id VARCHAR(36) PRIMARY KEY,
    series_id VARCHAR(36),
    title VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL,
    total_marks INT NOT NULL,
    difficulty VARCHAR(20),
    is_section_order_strict BOOLEAN DEFAULT FALSE,
    shuffle_sections BOOLEAN DEFAULT FALSE,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (series_id) REFERENCES test_series(id)
);
