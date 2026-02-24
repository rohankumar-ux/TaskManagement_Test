# Task Management System

A Java-based task management application with user authentication, task assignment, and activity tracking.

## Features

- User management with role-based access
- Task creation, assignment, and status tracking
- Activity logging and monitoring
- Search and filter capabilities
- Command-line interface

## Tech Stack

- Java 21
- Maven
- JUnit 5 for testing
- Mockito for mocking
- JaCoCo for code coverage
- SonarCloud for code analysis

## Quick Start

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="org.example.app.TaskManagementApp"
```

## CI/CD

Automated pipeline includes:
- SonarCloud code analysis
- Unit testing with JUnit
- Code coverage reporting
- Artifact packaging and upload
