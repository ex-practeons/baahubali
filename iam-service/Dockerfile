# ---------- Stage 1: build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy the entire monorepo to bypass Maven Reactor module issues
COPY . .

# Fix line endings for Windows users and make the wrapper executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Build only the IAM Service and its required internal dependencies (-am)
RUN --mount=type=cache,target=/root/.m2 ./mvnw -pl iam-service clean package -DskipTests -am -B -q

# Extract layers using the Spring Boot 3.x layertools syntax
RUN java -Djarmode=layertools -jar iam-service/target/*.jar extract --destination /workspace/extracted

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /application

# Create a non-root user for security
RUN groupadd --system iamgroup && useradd --system --gid iamgroup --no-create-home iamuser

# Copy the extracted layers in order of change frequency
COPY --from=build --chown=iamuser:iamgroup /workspace/extracted/dependencies/ ./
COPY --from=build --chown=iamuser:iamgroup /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=iamuser:iamgroup /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=iamuser:iamgroup /workspace/extracted/application/ ./

USER iamuser

# Optimize JVM memory for containerized environments
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

# Expose the default Spring Boot port (since server.port: 8081 was removed)
EXPOSE 8080

# Execute using the Spring Boot Loader
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]