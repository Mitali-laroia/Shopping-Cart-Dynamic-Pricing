# Multi-stage Dockerfile for Spring Boot application

# Stage 1: Build stage
FROM maven:3.9.5-eclipse-temurin-17 as builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY checkstyle.xml .

# Build the application (skip tests for faster builds in Docker)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Create app user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app/logs

# Copy the built JAR from builder stage
COPY --from=builder /app/target/shopping-cart-dynamic-pricing-*.jar app.jar

# Change ownership of the app directory
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]