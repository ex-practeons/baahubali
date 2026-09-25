CREATE TABLE questions (
    id VARCHAR(36) PRIMARY KEY,
    section_id VARCHAR(36),
    question_type VARCHAR(20),
    question_text TEXT NOT NULL,
    options_json JSON,
    correct_answer VARCHAR(255) NOT NULL,
    positive_marks DECIMAL(5,2) NOT NULL,
    negative_marks DECIMAL(5,2) DEFAULT 0.00,
    difficulty VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(id)
);
