-- 1. Restore 'title' columns that were dropped in V7
ALTER TABLE test_series ADD COLUMN title VARCHAR(255);
ALTER TABLE mock_tests ADD COLUMN title VARCHAR(255);
ALTER TABLE sections ADD COLUMN title VARCHAR(255);

-- 2. Add missing feature fields to mock_tests
ALTER TABLE mock_tests ADD COLUMN instructions TEXT;
ALTER TABLE mock_tests ADD COLUMN negative_marking_enabled BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE mock_tests ADD COLUMN is_free BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE mock_tests ADD COLUMN published_at TIMESTAMP NULL;

-- 3. Add missing fields to sections
ALTER TABLE sections ADD COLUMN duration_minutes INT;

-- 4. Add missing fields to questions (Global Bank upgrades)
ALTER TABLE questions ADD COLUMN correct_answer_json JSON;
ALTER TABLE questions ADD COLUMN explanation TEXT;
ALTER TABLE questions ADD COLUMN is_locked BOOLEAN NOT NULL DEFAULT FALSE;