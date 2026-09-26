# Test Service

The `test-service` manages the core test structure: categories, test series, mock tests, sections, and questions.

## Immutability Rule
Once a `mock_test` moves out of the `DRAFT` status (e.g. `PUBLISHED`), and if there are attempts associated with it, editing the questions or sections is blocked to prevent data corruption for the students who already started or completed the test. The service checks the `AttemptLockService` for this immutability rule. (Currently mocked, to be wired up with a Kafka consumer listening to `ATTEMPT_STARTED` events).

## Two DTO Rule (Security) & Translations
To ensure the correct answer never leaks to the student UI, we have implemented two separate DTO shapes:
- `QuestionPublicDto`: Exposed by `CatalogController`. Has NO field for `correctAnswer`. All title and question text is resolved to a specific language requested via the `?lang=` query param. If a requested language translation is missing, it returns `404 Not Found` (no silent fallback). If no language is provided, it defaults to the first required language of the category.
- `QuestionInternalDto`: Exposed by `InternalQuestionController`. Includes the `correctAnswer`. Grading only needs `correctAnswer`, which stays language-neutral on the parent `Question` row.

## Math & LaTeX Convention
Math content inside `question_text` and `options_json` values is wrapped in single-dollar delimiters for inline math (e.g. `Solve: $3\frac{1}{2} + 2\frac{2}{3}$`), or double-dollar `$$...$$` for display math.
- The `test-service` stores and returns these strings verbatim. It does **not** render or validate full LaTeX syntax.
- The service *does* perform a lightweight check on save to ensure `$` delimiters are **balanced** (to prevent accidental unclosed delimiters from breaking frontend parsers).
- Rendering (e.g., via KaTeX or MathJax) is strictly the responsibility of the frontend client.

## Internal Authentication
The `InternalQuestionController` requires an `X-Service-Auth` header. The secret is configured via `internal.auth.secret` (defaults to `secret123`). Only internal services (like the Attempt Service) should use the `TestServiceInternalClient` and provide this secret to fetch the answer key.
