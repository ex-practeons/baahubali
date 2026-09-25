# Testing Platform (baahubali)

A multi-module ed-tech backend built as a Spring Boot / Spring Cloud
monorepo. A single API Gateway fronts three independently deployable
services, handling JWT-based auth at the edge and forwarding trusted
identity headers downstream.

## Architecture
```
┌─────────────────────────────┐
│            Client           │
└──────────────┬──────────────┘
               │ HTTPS
               ▼
┌─────────────────────────────────────────┐
│              API Gateway                │
│      Spring Cloud Gateway / WebFlux     │
├─────────────────────────────────────────┤
│  JWT Validation                         │
│  Header Injection                       │
│  Request Routing                        │
└──────────────┬──────────────────────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
┌───────────────┐  ┌────────────────┐
│  IAM Service  │  │  Test Service  │
├───────────────┤  ├────────────────┤
│ Authentication│  │ Tests          │
│ Users         │  │ Questions      │
│ Authorization │  │ Categories     │
│               │  │ Catalog        │
│ Spring MVC    │  │ Spring MVC     │
│ JPA           │  │ JPA            │
└───────────────┘  └────────────────┘
```

| Module | Responsibility |
|---|---|
| `api-gateway` | Single entry point for all client traffic. Validates the JWT in the `HttpOnly` cookie, strips spoofed identity headers, and injects trusted `X-User-Id` / `X-User-Role` headers before forwarding requests downstream. |
| `iam-service` | Source of truth for user auth: registration, login, password hashing (`BCrypt`), JWT issuance and cookie delivery. |
| `test-service` | Core domain service: categories, test series, mock tests, question bank, publishing workflow, and student-facing catalog endpoints. Emits Kafka events on publish for downstream consumers (e.g. `attempt-service`). |
| `common` | Shared library all services depend on — currently provides the platform-wide OpenAPI/Swagger auto-configuration (see below). |

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for a deeper breakdown of
each service's internal modules and the request lifecycle.

## Tech stack

- Java 21, Spring Boot 4, Spring Cloud 2025.1.2
- Spring Cloud Gateway (WebFlux) + Spring Security
- Spring Data JPA, MySQL, Flyway migrations
- Redis (rate limiting / caching), Kafka (event publishing)
- springdoc-openapi (OpenAPI 3 / Swagger UI)
- Maven multi-module build

## Getting started

Requires Docker and Docker Compose.

```bash
git clone <repo-url>
cd backend
docker-compose up -d --build
```

This brings up `api-gateway` (host port `8080`), `iam-service`,
`test-service`, MySQL, Redis, and Kafka on a shared bridge network. Only the
gateway is exposed to the host by default — `iam-service` and `test-service`
are reachable only from inside the Docker network, consistent with the
gateway being the platform's only trust boundary.

Environment variables (JWT secret, DB credentials, CORS origins, etc.) are
read from `.env` at the repo root plus a per-service `.env` in each
module's own directory. Copy and adjust as needed for your environment
before starting.

## API documentation

Each service exposes its own OpenAPI docs and Swagger UI, reachable through
the gateway:

| Service | Swagger UI |
|---|---|
| IAM Service | `http://localhost:8080/api/auth/swagger-ui.html` |
| Test Service | `http://localhost:8080/api/tests/swagger-ui.html` |

Raw OpenAPI JSON is available at the same path with `swagger-ui.html`
swapped for `v3/api-docs` (e.g. `http://localhost:8080/api/auth/v3/api-docs`).