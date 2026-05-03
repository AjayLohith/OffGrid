FROM node:20-slim AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-21 AS java-builder
WORKDIR /app/backend/OffGrid/OffGrid
COPY backend/OffGrid/OffGrid/pom.xml ./
RUN mvn dependency:go-offline -q
COPY backend/OffGrid/OffGrid/src/ ./src/
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static
RUN mvn package -DskipTests -q

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=java-builder /app/backend/OffGrid/OffGrid/target/offgrid-1.0.0.jar ./app.jar
EXPOSE 8080 9090
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
