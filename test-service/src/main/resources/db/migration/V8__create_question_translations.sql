CREATE TABLE question_translations (
  id             VARCHAR(36)   PRIMARY KEY,
  question_id    VARCHAR(36)   NOT NULL,
  language       VARCHAR(10)   NOT NULL,
  question_text  TEXT          NOT NULL,
  options_json   JSON          NULL,
  created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_qt_question FOREIGN KEY (question_id) REFERENCES questions(id),
  CONSTRAINT uq_qt_question_lang UNIQUE (question_id, language)
);
