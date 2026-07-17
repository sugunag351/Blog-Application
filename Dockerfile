FROM ubuntu:latest
LABEL authors="puneeth"

ENTRYPOINT ["top", "-b"]

# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# -------- RUN STAGE --------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Render provides PORT dynamically
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
