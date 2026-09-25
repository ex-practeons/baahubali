ALTER TABLE test_series ADD COLUMN title_translations JSON;
UPDATE test_series SET title_translations = JSON_OBJECT('EN', title);
ALTER TABLE test_series MODIFY COLUMN title_translations JSON NOT NULL;
ALTER TABLE test_series DROP COLUMN title;

ALTER TABLE mock_tests ADD COLUMN title_translations JSON;
UPDATE mock_tests SET title_translations = JSON_OBJECT('EN', title);
ALTER TABLE mock_tests MODIFY COLUMN title_translations JSON NOT NULL;
ALTER TABLE mock_tests DROP COLUMN title;

ALTER TABLE sections ADD COLUMN title_translations JSON;
UPDATE sections SET title_translations = JSON_OBJECT('EN', title);
ALTER TABLE sections MODIFY COLUMN title_translations JSON NOT NULL;
ALTER TABLE sections DROP COLUMN title;
