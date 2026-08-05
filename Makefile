.PHONY: help build run-server run-tests run-logs clean install test package

# Default target
help:
	@echo "jobProcessor Makefile Commands:"
	@echo "================================"
	@echo ""
	@echo "  make build          - Build the project (mvn clean package)"
	@echo "  make install        - Install dependencies (mvn clean install)"
	@echo "  make test           - Run all tests"
	@echo "  make run-server     - Start the Spring Boot server"
	@echo "  make run-logs       - Tail application logs (requires server running)"
	@echo "  make dev            - Build, start server, and show logs (combined)"
	@echo "  make clean          - Clean build artifacts"
	@echo ""

# Build the project
build:
	@echo "Building project..."
	mvn clean package

# Install dependencies
install:
	@echo "Installing dependencies..."
	mvn clean install

# Run tests
test:
	@echo "Running tests..."
	mvn test

# Run smoke tests specifically
test-smoke:
	@echo "Running smoke tests..."
	mvn test -Dtest=EventControllerSmokeTestSimple

# Run the Spring Boot server
run-server:
	@echo "Starting Spring Boot server..."
	mvn spring-boot:run

# Build and run server
run-build-server: build run-server

# Run logs (tail the latest log file)
run-logs:
	@echo "Tailing application logs..."
	@if [ -f "application.log" ]; then \
		tail -f application.log; \
	else \
		echo "No log file found. Starting server to generate logs..."; \
		mvn spring-boot:run; \
	fi

# Development mode: build, then run server with logs
dev:
	@echo "Starting development environment..."
	@echo "Building project..."
	mvn clean install
	@echo "Starting server..."
	mvn spring-boot:run

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	rm -rf target/
	rm -f application.log

# Package without running tests
package:
	@echo "Packaging project (skipping tests)..."
	mvn clean package -DskipTests

# Verify build (compile and test)
verify:
	@echo "Verifying build..."
	mvn verify

# Full CI-like workflow
all: clean install test build
	@echo "Full build pipeline completed successfully!"
