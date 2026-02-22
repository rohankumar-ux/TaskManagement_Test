# -------- Build stage --------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy source code
COPY src ./src

# Compile all Java files
RUN mkdir out && javac -d out $(find src -name "*.java")

# -------- Runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy compiled classes
COPY --from=build /app/out ./out

# Run the main class
CMD ["java", "-cp", "out", "app.TaskManagementApp"]
