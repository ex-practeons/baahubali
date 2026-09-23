ALTER TABLE categories
  ADD COLUMN required_languages JSON;

-- Backfill existing rows with EN
UPDATE categories SET required_languages = JSON_ARRAY('EN');

ALTER TABLE categories
  MODIFY COLUMN required_languages JSON NOT NULL;
