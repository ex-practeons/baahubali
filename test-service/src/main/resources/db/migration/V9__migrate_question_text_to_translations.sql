INSERT INTO question_translations (id, question_id, language, question_text, options_json)
SELECT UUID(), id, 'EN', question_text, options_json FROM questions;

ALTER TABLE questions
  DROP COLUMN question_text,
  DROP COLUMN options_json;
