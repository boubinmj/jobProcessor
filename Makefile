.PHONY: help build run-server run-tests run-logs clean install test package test-smoke run-build-server verify package all docker-stop docker-clean

# Default target
help:
	@echo "jobProcessor Makefile Commands (Docker-based):"
	@echo "=============================================="
	@echo ""
	@echo "  make build              - Build Docker image (docker-compose build)"
	@echo "  make install            - Install dependencies in Docker (mvn clean install)"
	@echo "  make test               - Run all tests in Docker"
	@echo "  make test-smoke         - Run smoke tests in Docker"
	@echo "  make run-server         - Start Spring Boot server in Docker (detached)"
	@echo "  make run-build-server   - Build image and start server"
	@echo "  make run-logs           - Tail application logs from running container"
	@echo "  make dev                - Build and run server in foreground with logs"
	@echo "  make package            - Package project without running tests"
	@echo "  make verify             - Verify build (compile and test)"
	@echo "  make docker-stop        - Stop running Docker containers"
	@echo "  make clean              - Clean up Docker containers and images"
	@echo "  make all                - Full CI-like workflow (build, install, test, package)"
	@echo ""

# Build the Docker image
build:
	@echo "Building Docker image..."
	docker-compose build

# Install dependencies in Docker
install:
	@echo "Installing dependencies in Docker..."
	docker-compose run --rm jobprocessor mvn clean install

# Run tests in Docker
test:
	@echo "Running tests in Docker..."
	docker-compose run --rm jobprocessor mvn test

# Run smoke tests specifically in Docker
test-smoke:
	@echo "Running smoke tests in Docker..."
	docker-compose run --rm jobprocessor mvn test -Dtest=EventControllerSmokeTestSimple

# Run the Spring Boot server in detached mode
run-server:
	@echo "Starting Spring Boot server in Docker (detached mode)..."
	docker-compose up -d jobprocessor
	@echo "Server started. Use 'make run-logs' to view logs."

# Build and run server
run-build-server: build run-server

# Run logs (tail the latest log output from container)
run-logs:
	@echo "Tailing application logs..."
	docker-compose logs -f jobprocessor

# Development mode: build and run server in foreground with logs
dev:
	@echo "Starting development environment (foreground with logs)..."
	docker-compose up jobprocessor

# Package without running tests
package:
	@echo "Packaging project in Docker (skipping tests)..."
	docker-compose run --rm jobprocessor mvn clean package -DskipTests

# Verify build (compile and test)
verify:
	@echo "Verifying build in Docker..."
	docker-compose run --rm jobprocessor mvn verify

# Stop running Docker containers
docker-stop:
	@echo "Stopping Docker containers..."
	docker-compose down

# Clean up Docker containers and images
clean:
	@echo "Cleaning up Docker containers and locally built images..."
	docker-compose down --rmi local
	@echo "Cleanup completed."

# Full CI-like workflow
all: build install test package
	@echo "Full build pipeline completed successfully!"
