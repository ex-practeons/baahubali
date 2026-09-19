# testing-platform

Multi-module Maven project built on **Java 21** and **Spring Boot 4.0.7** (Spring Cloud 2025.1.2).

## Modules

| Module            | Description                                   | Stack                                | Port |
|-------------------|-----------------------------------------------|--------------------------------------|------|
| `api-gateway`     | Single entry point that routes to services    | Spring Cloud Gateway (WebFlux), Actuator | 8080 |
| `payment-service` | Payment processing service                    | Spring MVC, Actuator                 | 8081 |

## Layout

```
payment-platform/
├── pom.xml                  # parent POM (packaging=pom, inherits spring-boot-starter-parent)
├── mvnw / mvnw.cmd          # Maven Wrapper
├── .mvn/wrapper/
├── api-gateway/
│   ├── pom.xml
│   └── src/{main,test}/...
└── payment-service/
    ├── pom.xml
    └── src/{main,test}/...
```

## Prerequisites

- JDK 21
- No local Maven install needed (use the wrapper)

## Build

```bash
./mvnw clean verify          # Windows: mvnw.cmd clean verify
```

## Run

From the root, start both services together:

```bash
./mvnw spring-boot:run
```

This works because `.mvn/maven.config` enables a parallel build (`-T4`), so the two
blocking `spring-boot:run` goals execute at the same time, and the parent POM skips the plugin
(it has no main class). Press `Ctrl+C` to stop both. Log lines are interleaved; each carries the
application name.

`-T4` allows four modules to run at once. If you add more runnable modules, raise that number
(it must be at least the number of runnable modules).

Run a single module:

```bash
./mvnw -pl payment-service spring-boot:run
./mvnw -pl api-gateway spring-boot:run
```

Health checks: <http://localhost:8080/actuator/health> and <http://localhost:8081/actuator/health>

## Adding a new module

1. Create the module directory with its own `pom.xml` (parent = `payment-platform`).
2. Register it in the parent `pom.xml` under `<modules>`.
