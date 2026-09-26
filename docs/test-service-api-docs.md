# Test Service Detailed API Documentation

This document outlines the detailed REST API endpoints available in the `test-service`, including input parameters, headers, request bodies, and response structures.

## Standard API Response Format

All successful API responses are wrapped in a standard `ApiResponse` format:

```json
{
  "success": true,
  "status": 200,
  "message": "Resource retrieved successfully",
  "data": { ... },
  "meta": {
    "timestamp": "2024-03-10T12:00:00Z",
    "version": "1.2.0",
    "pagination": { // Only present for paginated list endpoints
      "total_records": 100,
      "current_page": 1,
      "total_pages": 5,
      "per_page": 20,
      "has_next": true,
      "has_previous": false
    }
  }
}
```

Errors are wrapped in `ApiErrorResponse`:
```json
{
  "success": false,
  "status": 400,
  "message": "Validation failed",
  "error": {
    "code": "BAD_REQUEST",
    "details": [...]
  },
  "meta": {
    "timestamp": "2024-03-10T12:00:00Z",
    "trace_id": "err-uuid"
  }
}
```

---

## 1. Admin Endpoints

Admin endpoints require administrative authorization, specifically enforced via the `x-user-id` header passed from the API gateway.

### 1.1 Category Management

#### Create Category
*   **Endpoint**: `POST /admin/categories`
*   **Headers**: 
    *   `x-user-id` (String, Required) - ID of the admin creating the category.
*   **Request Body** (`CategoryDto`):
    ```json
    {
      "id": "string (optional)",
      "name": "string",
      "description": "string",
      "requiredLanguages": ["string"]
    }
    ```
*   **Response** (201 Created):
    *   **Data**: `CategoryDto`

#### Update Category
*   **Endpoint**: `PUT /admin/categories/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Headers**: 
    *   `x-user-id` (String, Required)
*   **Request Body** (`CategoryDto`): Same as Create Category.
*   **Response** (200 OK):
    *   **Data**: `CategoryDto`

#### Get All Categories
*   **Endpoint**: `GET /admin/categories`
*   **Response** (200 OK):
    *   **Data**: `List<CategoryDto>`

---

### 1.2 Test Series Management

#### Create Test Series
*   **Endpoint**: `POST /admin/series`
*   **Headers**: 
    *   `x-user-id` (String, Required)
*   **Request Body** (`TestSeriesCreateDto`):
    ```json
    {
      "title": "string",
      "basePrice": 0.0,
      "categoryId": "UUID"
    }
    ```
*   **Response** (201 Created):
    *   **Data**: `TestSeriesDetailDto`

#### Get Test Series List (Paginated)
*   **Endpoint**: `GET /admin/series`
*   **Query Parameters**:
    *   `search` (String, Optional)
    *   `status` (String, Optional)
    *   `categoryId` (UUID, Optional)
    *   `page` (int, Default: 1)
    *   `limit` (int, Default: 20)
*   **Response** (200 OK):
    *   **Data**: `List<TestSeriesListDto>`
    *   **Meta**: Includes pagination details.

#### Get Test Series by ID
*   **Endpoint**: `GET /admin/series/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `TestSeriesDetailDto`

#### Update Test Series
*   **Endpoint**: `PUT /admin/series/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`TestSeriesUpdateDto`):
    ```json
    {
      "title": "string",
      "basePrice": 0.0,
      "categoryId": "UUID"
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `TestSeriesDetailDto`

#### Delete Test Series
*   **Endpoint**: `DELETE /admin/series/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `TestSeriesDetailDto`

---

### 1.3 Mock Test Management

#### Create Mock Test
*   **Endpoint**: `POST /admin/mock-tests/series/{seriesId}/mock-tests`
*   **Path Parameters**: `seriesId` (UUID)
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`MockTestCreateDto`):
    ```json
    {
      "title": "string",
      "durationMinutes": 120,
      "isSectionOrderStrict": true,
      "shuffleSections": false,
      "negativeMarkingEnabled": true,
      "instructions": "string",
      "isFree": false
    }
    ```
*   **Response** (201 Created):
    *   **Data**: `AdminMockTestDetailDto`

#### Update Mock Test
*   **Endpoint**: `PUT /admin/mock-tests/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Request Body** (`MockTestCreateDto`): Same as Create Mock Test.
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto`

#### Get Mock Test Summary
*   **Endpoint**: `GET /admin/mock-tests/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto` (Includes basic details and `List<SectionSummaryDto>`)

#### Get Mock Test Answer Key
*   **Endpoint**: `GET /admin/mock-tests/{id}/answer-key`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `TestBlueprintDto` (Detailed tree of Sections and their Questions with Answers).

#### Publish Test
*   **Endpoint**: `POST /admin/mock-tests/{id}/publish`
*   **Path Parameters**: `id` (UUID)
*   **Headers**: 
    *   `If-Match` (Instant ISO-8601 String, Required) - For optimistic locking.
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto`

#### Archive Test
*   **Endpoint**: `POST /admin/mock-tests/{id}/archive`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto`

#### Clone Test
*   **Endpoint**: `POST /admin/mock-tests/{id}/clone`
*   **Path Parameters**: `id` (UUID)
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body**:
    ```json
    {
      "newTitle": "string"
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto`

#### Revert to Draft
*   **Endpoint**: `PATCH /admin/mock-tests/{id}/revert-to-draft`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `AdminMockTestDetailDto`

---

### 1.4 Section Management

#### Create Section
*   **Endpoint**: `POST /admin/mock-tests/{testId}/sections`
*   **Path Parameters**: `testId` (UUID)
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`SectionCreateDto`):
    ```json
    {
      "title": "string",
      "durationMinutes": 60,
      "shuffleQuestions": true
    }
    ```
*   **Response** (201 Created):
    *   **Data**: `AdminSectionDetailDto`

#### Reorder Sections
*   **Endpoint**: `PUT /admin/mock-tests/{testId}/sections/reorder`
*   **Path Parameters**: `testId` (UUID)
*   **Request Body**:
    ```json
    {
      "orderedSectionIds": ["UUID1", "UUID2"]
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `List<AdminSectionDetailDto>`

#### Attach Questions to Section
*   **Endpoint**: `POST /admin/sections/{id}/questions`
*   **Path Parameters**: `id` (UUID) - Section ID
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`BulkAttachQuestionsDto`):
    ```json
    {
      "questionIds": ["UUID1", "UUID2"],
      "positiveMarksOverride": 2.0,
      "negativeMarksOverride": 0.5
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `AdminSectionDetailDto`

#### Remove Question from Section
*   **Endpoint**: `DELETE /admin/sections/{sectionId}/questions/{questionId}`
*   **Path Parameters**: `sectionId` (UUID), `questionId` (UUID)
*   **Response** (200 OK):
    *   **Data**: `AdminSectionDetailDto`

#### Reorder Questions in Section
*   **Endpoint**: `PUT /admin/sections/{sectionId}/questions/reorder`
*   **Path Parameters**: `sectionId` (UUID)
*   **Request Body**:
    ```json
    {
      "orderedQuestionIds": ["UUID1", "UUID2"]
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `AdminSectionDetailDto`

#### Update Question Marks (Override in Section)
*   **Endpoint**: `PATCH /admin/sections/{sectionId}/questions/{questionId}`
*   **Path Parameters**: `sectionId` (UUID), `questionId` (UUID)
*   **Request Body**:
    ```json
    {
      "positiveMarks": 2.0,
      "negativeMarks": 0.5
    }
    ```
*   **Response** (200 OK):
    *   **Data**: `AdminSectionDetailDto`

---

### 1.5 Question Management

#### Create Question
*   **Endpoint**: `POST /admin/questions`
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`QuestionCreateDto`):
    ```json
    {
      "questionType": "SINGLE_CHOICE", // Enum
      "translations": [
        {
          "language": "en",
          "questionText": "string",
          "optionsJson": "string (JSON representation of options)"
        }
      ],
      "correctAnswerJson": { "key": "value" },
      "positiveMarks": 1.0,
      "negativeMarks": 0.25,
      "explanation": "string",
      "difficulty": "EASY" // Enum
    }
    ```
*   **Response** (201 Created):
    *   **Data**: `QuestionDetailDto`

#### Get Questions List (Paginated)
*   **Endpoint**: `GET /admin/questions`
*   **Query Parameters**:
    *   `search` (String, Optional)
    *   `type` (String, Optional) - Question Type Enum
    *   `difficulty` (String, Optional) - Difficulty Enum
    *   `isLocked` (Boolean, Optional)
    *   `unused` (Boolean, Optional)
    *   `page` (int, Default: 1)
    *   `limit` (int, Default: 20)
*   **Response** (200 OK):
    *   **Data**: `List<QuestionListDto>`
    *   **Meta**: Includes pagination details.

#### Get Question by ID
*   **Endpoint**: `GET /admin/questions/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `QuestionDetailDto`

#### Update Question
*   **Endpoint**: `PUT /admin/questions/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Headers**: `x-user-id` (String, Required)
*   **Request Body** (`QuestionUpdateDto`): Same as QuestionCreateDto.
*   **Response** (200 OK):
    *   **Data**: `QuestionDetailDto` (Includes a `warning` field if the update affects active/published tests).

#### Delete Question
*   **Endpoint**: `DELETE /admin/questions/{id}`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `QuestionDetailDto`

---

## 2. Catalog Endpoints (Public)

Public endpoints used to display test structures for candidates.

#### Get Test Structure
*   **Endpoint**: `GET /catalog/mock-tests/{id}/structure`
*   **Path Parameters**: `id` (UUID)
*   *Note: Gateway enforces entitlement/payment headers if this test is not marked as free.*
*   **Response** (200 OK):
    *   **Data**: `PublicMockTestStructureDto`
    ```json
    {
      "testId": "UUID",
      "title": "string",
      "durationMinutes": 120,
      "instructions": "string",
      "totalMarks": 100.0,
      "isFree": false,
      "sections": [
        {
          "sectionId": "UUID",
          "title": "string",
          "sequenceOrder": 1,
          "questions": [
             {
               "questionId": "UUID",
               "sequenceOrder": 1,
               "questionType": "string",
               "questionText": "string",
               "optionsJson": { ... },
               "positiveMarks": 1.0,
               "negativeMarks": 0.25
             }
          ]
        }
      ]
    }
    ```

---

## 3. Internal Endpoints (Service-to-Service)

Endpoints restricted to internal microservice communication (e.g., accessed by the execution-service or grading-service).

#### Get Test Blueprint
*   **Endpoint**: `GET /internal/mock-tests/{id}/blueprint`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `TestBlueprintDto` (Complete hierarchical structure of the test, including answers for grading).

#### Get Test Status
*   **Endpoint**: `GET /internal/mock-tests/{id}/status`
*   **Path Parameters**: `id` (UUID)
*   **Response** (200 OK):
    *   **Data**: `TestStatusDto`
    ```json
    {
      "testId": "UUID",
      "status": "PUBLISHED",
      "totalMarks": 100.0,
      "durationMinutes": 120
    }
    ```
