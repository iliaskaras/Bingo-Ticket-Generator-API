# Bingo Ticket Generator API

## Overview
The **Bingo Ticket Generator API** is a RESTful service designed to generate Bingo strips. Each strip consists of six Bingo tickets, adhering to traditional Bingo rules. The project follows **Hexagonal Architecture** and **Domain-Driven Design (DDD)** to ensure maintainability, scalability, and testability.

## Bingo Strips creation speed report

### Method Performance Results

The following results taken by running the **StripGeneratorStressSpec**'s test scenarios.
Note: Production code also logs the generated strips, which causing delay on the speed. For the purpose of the speed test, the logging was removed from the **StripGeneratorService**.

| Strip generation | Number of Strips Generated | Speed (ms) |
|------------------|----------------------------|------------|
|                  | 1                          | 8          |
|                  | 1000                       | 613        |
|                  | 2000                       | 815        |
|                  | 4000                       | 1161       |
|                  | 8000                       | 1716       |
|                  | **10000**                  | 2028       |
|                  | 20000                      | 3085       |
|                  | 50000                      | 6608       |
|                  | 100000                     | 13639      |

## Features
- **Strip Generation**: Generate strips containing six Bingo tickets through a REST endpoint.
- **Validation**: Implements all Bingo-specific validation rules for ticket generation.
- **Logging**: Logs generated strips in a structured, human-readable format for debugging and analytics.
- **Stress Testing**: Includes stress tests to evaluate performance under load.
- **Dockerized**: Ready for containerized deployment via Docker and Docker Compose.

## Project Structure
The project is organized into the following layers, following Hexagonal Architecture and DDD principles:

### 1. **Application Layer**
This layer defines the application-specific use cases, coordinating between input ports (REST controllers) and domain services. The main functionality lies in:
- Use case implementations for strip and ticket creation.
- Input and output adapters for REST communication.

### 2. **Domain Layer**
This is the core layer, encapsulating the business logic:
- **Strip Domain**: Handles strip generation, which coordinates the creation of all the 6 individual tickets.
- **Ticket Domain**: Responsible for generating and validating individual tickets by using the validation domain and its column generator service.
- **Validation**: Implements validation logic for Bingo ticket rules, ensuring compliance.

### 3. **Infrastructure Layer**
The infrastructure layer contains implementations for interacting with external systems and frameworks:
- REST controllers to expose the API.
- Configuration classes and exception handlers.

## Code Style
- **Kotlin**: The application follows Kotlin’s standard code conventions.
- **Testing Framework**: Spock and JUnit for unit and integration testing.

## Postman Collection
There is a postman collection in the postman directory under project's root, which you can import and test the 
api after deploying the application locally using the Makefile's make deploy command.

## Makefile Commands
The project includes a `Makefile` to streamline tasks. Below are the available commands:

- **Deploy the Application** (build Docker images and start services using Docker Compose):

```bash
make deploy
```

- **Stop the Application**:

```bash
make down
```

- **Run All Tests** (unit and stress tests):

```bash
make test
```

## Deployment
The application is fully containerized and can be deployed locally using Docker Compose.

### Prerequisites
- Docker
- Docker Compose

### Deployment Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-name>
   ```
2. Build and run the application:
   ```bash
   make deploy
    ```
3. Access the API at `http://localhost:8080`.
4. Stop the containers:
   ```bash
   make down
   ```

## API Usage
The API exposes the following endpoint:

### **POST /api/v1/strips**
Generate one or more Bingo strips, depending on the number request body parameter, up to 15k strips for ensuring no maximum response size exceeded errors.

## API Endpoints

### POST /api/v1/strips
This endpoint generates a specified number of Bingo strips.

**Request Body:**
```json
{
  "number": <integer>
}
```

**Response:**
```json
{
  "strips": [
    {
      "tickets": [
        {
          "rows": [
            {
              "cells": [1, 2, 3]
            },
            {
              "cells": [10]
            },
            {
              "cells": [20, 21, 22]
            },
            {
              "cells": []
            },
            {
              "cells": [40, 41]
            },
            {
              "cells": [50, 51, 52]
            },
            {
              "cells": []
            },
            {
              "cells": []
            },
            {
              "cells": [80, 89, 90]
            }
          ]
        }
      ]
    }
  ]
}
```

- **strips (array)**: List of generated Bingo strips, based on the requested **number**s of tickets to generate.
- **tickets (array)**: List of 6 Bingo strip's tickets.
- **rows (array)**: List of 3 rows in each ticket.
- **cells (array)**: List of numbers in each row's column, starting from 1st till 9th. Blank is represented as empty list.

## Testing
The application is fully unit tested, including stress tests for load testing and performance validation.
For simplicity, stress tests are added under the same unit tests directory.

### Run All Tests
To run all tests (unit tests + stress tests):
```bash
make test
```

## Development

### Running Locally (without Docker)
To run the application locally on your machine without Docker:
1. Build and run the application using Gradle:
   ```bash
   ./gradlew bootRun
   ```
2. Once the application starts, you can access the API at:
    - `http://localhost:8080`
