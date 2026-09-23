CREATE TABLE section_questions (
    id VARCHAR(36) PRIMARY KEY,
    section_id VARCHAR(36) NOT NULL,
    question_id VARCHAR(36) NOT NULL,
    sequence_order INT NOT NULL,
    positive_marks_override DECIMAL(10,2),
    negative_marks_override DECIMAL(10,2),
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (section_id) REFERENCES sections(id),
    FOREIGN KEY (question_id) REFERENCES questions(id),
    UNIQUE KEY uk_section_sequence (section_id, sequence_order),
    UNIQUE KEY uk_section_question (section_id, question_id)
);

CREATE INDEX idx_sq_deleted_at ON section_questions(deleted_at);