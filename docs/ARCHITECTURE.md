# Backend Architecture Documentation

This document outlines the architecture, responsibilities, and network flow of the core infrastructure modules within the monorepo.

## api-gateway
The API Gateway serves as the single entry point for all frontend client requests (Next.js) and acts as the central security checkpoint for the platform. Built on Spring Cloud Gateway and Spring Security Reactive, it operates completely statelessly to allow horizontal scaling.

**Core Responsibilities:**
*   **Reverse Proxy & Routing:** Intercepts all incoming HTTP traffic and routes it to the appropriate downstream microservice based on the URL path.
*   **JWT Extraction & Validation:** Reads the `jwt_token` from the `HttpOnly` cookie on incoming requests. It validates the digital signature and expiration of the token using the shared secret key.
*   **Header Mutation (The Trust Boundary):** Upon successful JWT validation, the Gateway extracts the user's ID and Role from the token payload, appends them as trusted HTTP headers (e.g., `X-User-Id`, `X-User-Role`), and strips any externally spoofed headers before forwarding the request to downstream services.
*   **Auth Bypass:** Explicitly allows unauthenticated traffic to pass directly to the `iam-service` for specific routes (`/api/auth/login`, `/api/auth/register`).

## iam-service
The Identity & Access Management (IAM) service is the single source of truth for user authentication and authorization data. Built with Spring Web and Spring Data JPA, it is the only service permitted to directly access the user credential tables in the MySQL database.

**Core Responsibilities:**
*   **User Provisioning:** Handles new student registrations, ensuring unique email constraints and persisting user profiles to MySQL.
*   **Credential Verification:** Uses `BCryptPasswordEncoder` to securely hash incoming passwords and verify them against the stored database hashes.
*   **Token Generation:** Upon successful authentication, constructs a JSON Web Token (JWT) encapsulating the user's identity and system role. 
*   **Secure Cookie Delivery:** Responsible for attaching the generated JWT to the HTTP response as an `HttpOnly`, `Secure` cookie, ensuring the Next.js frontend cannot access the token via JavaScript, thereby neutralizing Cross-Site Scripting (XSS) attack vectors.