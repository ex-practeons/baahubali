# Test Service API Documentation

This document outlines the REST API endpoints available in the `test-service`. The API is organized into several domains, primarily focused on administration, catalog access, and internal service-to-service communication.

---

## Admin Endpoints

Admin endpoints typically require the `x-user-id` header to identify the administrative user performing the action.

### Category Management
Manage categories for test series and mock tests.

*   **Create Category**
    *   `POST /admin/categories`
    *   **Body:** `CategoryDto`
    *   **Headers:** `x-user-id`

*   **Update Category**
    *   `PUT /admin/categories/{id}`
    *   **Body:** `CategoryDto`
    *   **Headers:** `x-user-id`

*   **Get All Categories**
    *   `GET /admin/categories`

### Test Series Management
Manage collections of mock tests organized into series.

*   **Create Test Series**
    *   `POST /admin/series`
    *   **Body:** `TestSeriesCreateDto`
    *   **Headers:** `x-user-id`

*   **Get Test Series List**
    *   `GET /admin/series`
    *   **Query Parameters:** `search`, `status`, `categoryId`, `page` (default: 1), `limit` (default: 20)

*   **Get Test Series by ID**
    *   `GET /admin/series/{id}`

*   **Update Test Series**
    *   `PUT /admin/series/{id}`
    *   **Body:** `TestSeriesUpdateDto`
    *   **Headers:** `x-user-id`

*   **Delete Test Series**
    *   `DELETE /admin/series/{id}`

### Mock Test Management
Manage individual mock tests.

*   **Create Mock Test**
    *   `POST /admin/mock-tests/series/{seriesId}/mock-tests`
    *   **Body:** `MockTestCreateDto`
    *   **Headers:** `x-user-id`

*   **Update Mock Test**
    *   `PUT /admin/mock-tests/{id}`
    *   **Body:** `MockTestCreateDto`

*   **Get Mock Test Summary**
    *   `GET /admin/mock-tests/{id}`

*   **Get Mock Test Answer Key**
    *   `GET /admin/mock-tests/{id}/answer-key`

*   **Publish Test**
    *   `POST /admin/mock-tests/{id}/publish`
    *   **Headers:** `If-Match` (Expected `updatedAt` instant for optimistic locking)

*   **Archive Test**
    *   `POST /admin/mock-tests/{id}/archive`

*   **Clone Test**
    *   `POST /admin/mock-tests/{id}/clone`
    *   **Body:** `{ "newTitle": "..." }`
    *   **Headers:** `x-user-id`

*   **Revert to Draft**
    *   `PATCH /admin/mock-tests/{id}/revert-to-draft`

### Section Management
Manage sections within a mock test.

*   **Create Section**
    *   `POST /admin/mock-tests/{testId}/sections`
    *   **Body:** `SectionCreateDto`
    *   **Headers:** `x-user-id`

*   **Reorder Sections**
    *   `PUT /admin/mock-tests/{testId}/sections/reorder`
    *   **Body:** `{ "orderedSectionIds": [...] }`

### Question Management
Manage questions in the central question bank.

*   **Create Question**
    *   `POST /admin/questions`
    *   **Body:** `QuestionCreateDto`
    *   **Headers:** `x-user-id`

*   **Get Questions List**
    *   `GET /admin/questions`
    *   **Query Parameters:** `search`, `type`, `difficulty`, `isLocked`, `unused`, `page` (default: 1), `limit` (default: 20)

*   **Get Question by ID**
    *   `GET /admin/questions/{id}`

*   **Update Question**
    *   `PUT /admin/questions/{id}`
    *   **Body:** `QuestionUpdateDto`
    *   **Headers:** `x-user-id`

*   **Delete Question**
    *   `DELETE /admin/questions/{id}`

### Section-Question Mapping
Manage the assignment of questions to specific sections within a mock test.

*   **Attach Questions to Section**
    *   `POST /admin/sections/{id}/questions`
    *   **Body:** `BulkAttachQuestionsDto`
    *   **Headers:** `x-user-id`

*   **Remove Question from Section**
    *   `DELETE /admin/sections/{sectionId}/questions/{questionId}`

*   **Reorder Questions in Section**
    *   `PUT /admin/sections/{sectionId}/questions/reorder`
    *   **Body:** `{ "orderedQuestionIds": [...] }`

*   **Update Question Marks (Override)**
    *   `PATCH /admin/sections/{sectionId}/questions/{questionId}`
    *   **Body:** `{ "positiveMarks": ..., "negativeMarks": ... }`

---

## Catalog Endpoints

Public-facing endpoints for accessing test catalog information.

*   **Get Test Structure**
    *   `GET /catalog/mock-tests/{id}/structure`
    *   *Note: Gateway enforces entitlement headers if this is a paid test.*

---

## Internal Endpoints

Endpoints used for internal communication between microservices.

*   **Get Test Blueprint**
    *   `GET /internal/mock-tests/{id}/blueprint`

*   **Get Test Status**
    *   `GET /internal/mock-tests/{id}/status`
