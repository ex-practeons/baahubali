# Attempt Service API Documentation

The **Attempt Service** handles test-taking sessions. Every JSON endpoint uses a consistent envelope:

```json
{
  "success": true,
  "status": 200,
  "message": "Resource retrieved successfully",
  "data": {},
  "meta": { "timestamp": "2026-09-23T17:00:00Z", "version": "1.2.0" }
}
```

Errors use `success: false`, the matching HTTP status and message, an `error` object with `code` and `details`, and `meta` containing `timestamp`, `version`, and `trace_id`. Validation details are a list of `{ "field", "issue" }` objects.

### Start Attempt
Starts a new attempt for a mock test.
**Endpoint:** `POST /attempts-api`

**cURL Example:**
```bash
curl -X POST http://localhost:8080/attempts-api \
  -H "Content-Type: application/json" \
  -H "Cookie: ACCESS_TOKEN=<token>" \
  -d '{
    "userId": "u4f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001",
    "testId": "1c3f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0002",
    "durationMinutes": 180
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "status": 201,
  "message": "Attempt started successfully",
  "data": {
    "attemptId": "a5f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001",
    "deadline": "2026-09-26T13:40:00Z",
    "testPayload": {}
  },
  "meta": { "timestamp": "2026-09-26T10:40:00Z", "version": "1.2.0" }
}
```

### Attempt History
Returns the user's attempts as a top-level array in `data`, with pagination metadata. `page` is 1-based and `perPage` must be between 1 and 100.
**Endpoint:** `GET /attempts-api/history?page=1&perPage=10`

```json
{
  "success": true,
  "status": 200,
  "message": "Attempt history retrieved successfully",
  "data": [],
  "meta": {
    "timestamp": "2026-09-26T10:40:00Z",
    "version": "1.2.0",
    "pagination": {
      "total_records": 0,
      "current_page": 1,
      "total_pages": 0,
      "per_page": 10,
      "has_next": false,
      "has_previous": false
    }
  }
}
```

### Submit Attempt
Finalizes and submits an ongoing attempt.
**Endpoint:** `POST /attempts-api/{id}/submit`

**cURL Example:**
```bash
curl -X POST http://localhost:8080/attempts-api/a5f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001/submit \
  -H "Cookie: ACCESS_TOKEN=<token>"
```

### Stream Attempt (SSE)
Subscribes to Server-Sent Events (SSE) for real-time updates regarding the test attempt (like time remaining, background flush status, etc).
**Endpoint:** `GET /attempts-api/{id}/stream`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/attempts-api/a5f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001/stream \
  -H "Accept: text/event-stream" \
  -H "Cookie: ACCESS_TOKEN=<token>"
```
SSE responses use `text/event-stream` for live events rather than the JSON envelope.

### Patch Attempt
Saves answers dynamically while the attempt is ongoing.
**Endpoint:** `PATCH /attempts-api/{id}`

**cURL Example:**
```bash
curl -X PATCH http://localhost:8080/attempts-api/a5f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001 \
  -H "Content-Type: application/json" \
  -H "Cookie: ACCESS_TOKEN=<token>" \
  -d '{
    "questionId": "b1f7c3c5-1b4d-4b8c-8f9f-7b8c8d8f0004",
    "selectedOptions": ["B"],
    "timeSpentSeconds": 45,
    "status": "ANSWERED"
  }'
```
