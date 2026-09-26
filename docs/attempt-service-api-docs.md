# Attempt Service API Documentation

The **Attempt Service** handles the test-taking sessions. 

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
  "attemptId": "a5f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001",
  "status": "IN_PROGRESS",
  "startedAt": "2026-09-26T10:40:00Z"
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
