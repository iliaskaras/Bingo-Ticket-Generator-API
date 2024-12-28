# Makefile for managing tasks in the Bingo Ticket Generator project

# Default target
.PHONY: all
all: help

# Deployment of the application (build Docker images and start services using Docker Compose)
.PHONY: deploy
deploy:
	@echo "Building the application..."
	./gradlew build -x test --parallel
	@echo "Building and deploying the application using Docker Compose..."
	sudo docker-compose -f deployment/docker-compose.yml up --build

# Bring down the Docker Compose services (stopping and removing containers)
.PHONY: down
down:
	@echo "Stopping and removing Docker Compose services..."
	docker-compose -f deployment/docker-compose.yml down

# Run all tests (unit and stress tests)
.PHONY: test
test:
	@echo "Running all tests including unit and stress tests..."
	./gradlew test

# Help command to display the available Makefile targets
.PHONY: help
help:
	@echo "Makefile commands:"
	@echo "  deploy            - Build and deploy the application using Docker Compose. Note: You may need to explicitly pull openjdk:19-jdk-slim (docker pull openjdk:19-jdk-slim) before executing the command"
	@echo "  down              - Stop and remove Docker Compose services"
	@echo "  test              - Run all tests (unit and stress tests)"