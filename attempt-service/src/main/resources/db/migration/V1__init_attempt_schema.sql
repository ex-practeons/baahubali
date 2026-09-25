CREATE TABLE attempts (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    test_id VARCHAR(64) NOT NULL,
    started_at DATETIME(6) NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    final_score DOUBLE,
    PRIMARY KEY (id)
);

CREATE TABLE attempt_answers (
    id BIGINT AUTO_INCREMENT NOT NULL,
    attempt_id VARCHAR(36) NOT NULL,
    question_id VARCHAR(64) NOT NULL,
    selected_option VARCHAR(16),
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_attempt_question UNIQUE (attempt_id, question_id)
);