# baahubali

## API Gateway

The API Gateway is the central entry point and security checkpoint for the ed-tech platform. Built with Spring Cloud Gateway and Spring Security Reactive, it routes all frontend traffic to the appropriate backend microservices while shielding them from unauthorized access.

## What It Does
* **Centralized Routing:** Intercepts all incoming HTTP requests and proxies them to the correct downstream microservice based on path rules (e.g., routing `/api/exam/**` to the Exam Engine).
* **Security & Authentication:** Extracts the `jwt_token` from the user's `HttpOnly` cookie and validates its digital signature and expiration.
* **Public Bypass:** Explicitly allows unauthenticated traffic to pass directly to the IAM service for login and registration routes.
* **Rate Limiting:** Protects downstream services from brute-force attacks and sudden traffic spikes.

## What It Passes On
Once a request's JWT is successfully validated, the Gateway acts as a trust boundary. Before forwarding the request downstream, it:
* **Appends Identity Headers:** Extracts the user ID and role from the JWT payload and injects them as trusted HTTP headers (e.g., `X-User-Id`, `X-User-Role`).
* **Strips Spoofed Headers:** Removes any pre-existing identity headers sent by a malicious client trying to fake admin privileges.
* **Forwards the Sanitized Request:** Delivers the clean, trusted request to the internal microservices, allowing them to rely entirely on the `X-User-Id` header for business logic without ever needing to parse a JWT or interact with cookies.
