# IAM Service API Documentation

The **IAM Service** handles authentication and authorization.

### Register User
Registers a new user account.
**Endpoint:** `POST /api/auth/register`

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "Password123!"
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "status": 201,
  "message": "Account registered successfully",
  "data": {
    "id": "u4f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  },
  "meta": {
    "timestamp": "2026-09-26T10:30:00Z"
  }
}
```
*Note: The response will also include a `Set-Cookie` header with the HTTP-only JWT token.*

### Login
Authenticates an existing user and returns an auth cookie.
**Endpoint:** `POST /api/auth/login`

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "Password123!"
  }'
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Login successful",
  "data": {
    "id": "u4f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  },
  "meta": {
    "timestamp": "2026-09-26T10:35:00Z"
  }
}
```

### Logout
Logs out the user by clearing the auth cookie.
**Endpoint:** `POST /api/auth/logout`

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "x-user-id: u4f9c5d-8b4d-4b8c-8f9f-7b8c8d8f0001"
```

### Get Current User
Retrieves information about the currently authenticated user.
**Endpoint:** `GET /api/users/me`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Cookie: ACCESS_TOKEN=<token>"
```
