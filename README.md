# Spring Boot Ollama App

This application demonstrates a Spring Boot setup with Docker and Swagger, integrated with the Ollama LLM.

# Spring Boot and Ollama LLM Application

## Prerequisites

### Install Docker and Docker Compose

1. Ensure Docker is installed on your system.
2. Install Docker Compose if it’s not already included in your Docker installation.
3. Verify installation:

   ```bash
   docker --version
   docker-compose --version
   ```

### Install Maven (Optional, for building locally before Docker)

1. If you need to modify and build the application locally before running in Docker, install Maven.
2. Verify installation:

   ```bash
   mvn --version
   ```

### Java Environment (Optional)

If running locally without Docker, ensure JDK 17+ is installed.

---

## Steps to Build and Run

### 1. Navigate to the Project Directory

Unzip the file and move to the project directory:

```bash
unzip spring-boot-ollama-app.zip
cd spring-boot-ollama-app
```

### 2. Build the Spring Boot Application

If you want to build the application locally before running in Docker:

```bash
mvn clean package
```

This will generate a `.jar` file in the `target/` directory.

Alternatively, Docker will handle the build as part of the `docker-compose` process.

### 3. Run Docker Compose

Use Docker Compose to build and run both the Spring Boot application and Ollama service:

```bash
docker-compose up --build
```

This command:

- Builds the images defined in the `Dockerfile.app` and `Dockerfile.ollama`.
- Starts both the Spring Boot app and Ollama LLM container.

### 4. Access the Services

- **Spring Boot App**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
  - Use the Swagger UI to test the `/api/ollama/query` endpoint.
- **Ollama Service**: Runs on port `11434` (used internally by the Spring Boot app).

### 5. Stop the Containers

To stop the running containers, press `Ctrl+C` or use:

```bash
docker-compose down

