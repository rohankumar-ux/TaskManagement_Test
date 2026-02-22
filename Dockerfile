# -------- Build stage --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy pom.xml and source code
COPY pom.xml ./
COPY src ./src

# Build with Maven
RUN mvn clean package -DskipTests

# -------- Runtime stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy JAR file from build stage
COPY --from=build /app/target/TaskManagement_Java-*.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
