# ==============================
# Multi-stage Dockerfile for Spring Boot (Maven) + Angular
# ==============================

# -------- Stage 0: Pre-build backend (spec generation) --------
FROM maven:3.9.9-eclipse-temurin-24-alpine AS spec-builder

WORKDIR /app/backend

# Copy pom.xml first for dependency caching
COPY backend/pom.xml ./
RUN mvn -B -q dependency:go-offline

# Copy backend source
COPY backend/ ./

# Generate spec / artifacts needed by frontend
RUN mvn -B -DskipTests install


# -------- Stage 1: Build Angular frontend --------
FROM node:20-alpine AS frontend-builder

# Install Java for openapi-generator-cli
RUN apk add --no-cache openjdk21-jre

WORKDIR /app/frontend

# Copy generated spec/artifacts from backend build
# (adjust path if your spec is in a different folder)
COPY --from=spec-builder /app/specs ../specs

RUN pwd
RUN ls -la
RUN ls -la ../
RUN ls -la ../specs

# Copy package files first for caching
COPY frontend/package*.json ./
COPY frontend/openapi-typescript-angular-template ./openapi-typescript-angular-template

RUN ls -la ../frontend
RUN ls -la ../frontend/openapi-typescript-angular-template

RUN npm i

RUN npm run generate:all

RUN ls -la

# Copy frontend source
COPY frontend/ ./

# Build production Angular app
RUN npm run build


# -------- Stage 2: Build Spring Boot backend (Maven) --------
FROM maven:3.9.9-eclipse-temurin-24-alpine AS backend-builder


WORKDIR /app/backend

# Copy pom.xml first for dependency caching
COPY backend/pom.xml ./
RUN mvn -B -q dependency:go-offline

# Copy backend source
COPY backend/ ./

# Copy built Angular into Spring static folder
# Adjust path depending on Angular output folder name
COPY --from=frontend-builder /app/frontend/dist/frontend/browser ./src/main/resources/static/


# Build Spring Boot jar
RUN mvn -B clean package -DskipTests


# -------- Stage 4: Runtime image --------
FROM eclipse-temurin:24-jre-alpine


WORKDIR /app

# Copy jar from builder
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# JVM container optimizations
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

# Run app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
