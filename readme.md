# Event-Driven Processing Simulator

A concurrent event processing system built with Spring Boot and Docker. Submit events via REST API, watch them get processed by a pool of worker threads in real-time through an interactive dashboard.

**For detailed documentation, see [DESCRIPTION.md](docs/documents/DESCRIPTION.md)**

---

## Quick Start

### Clone the Repository

```bash
git clone https://github.com/boubinmj/jobProcessor.git
cd jobProcessor
```

### Build & Run

Choose your platform below:

---

## macOS

**Prerequisites:** Docker Desktop installed

```bash
docker-compose up --build
```

---

## Windows

**Prerequisites:** Docker Desktop installed

```bash
docker-compose up --build
```

---

## Linux

**Prerequisites:** Docker and Docker Compose installed

#### Option 1: Add user to docker group (recommended, one-time setup)

```bash
sudo usermod -aG docker $USER
newgrp docker
docker-compose up --build
```

#### Option 2: Run with sudo (if you skip the group setup)

```bash
sudo docker-compose up --build
```

---

## Alternative: Local Maven Build (All Platforms)

**Prerequisites:** Java 17+, Maven 3.6+

```bash
mvn clean install
mvn spring-boot:run
```

---

## Using Make Commands

```bash
make build              # Build the Docker image
make run-build-server   # Build and start the server
```

---

## Access the Application

- **Dashboard**: http://localhost:8080/index.html
- **REST API**: http://localhost:8080/api/events
- **H2 Console**: http://localhost:8080/h2-console

---

## What's Inside

![Screen recording demo](docs/media/java-final.gif)

- Submit events via REST API
- Watch them get processed by 3 concurrent worker threads
- View real-time metrics: queue size, active workers, completion rate, processing time, worker utilization
- Interactive web dashboard with auto-refreshing stats

---

## Learn More

For comprehensive documentation including:
- Detailed architecture overview
- Complete REST API reference
- Event lifecycle and timing

**See [DESCRIPTION.md](docs/documents/DESCRIPTION.md)**

For a full project report discussing the Java concepts covered in this project including

- Threading and concurrent processing
- Database persistance with H2
- Dependancy Injection
- REST API networking with Java Springboot

**See the [Final Project Report](docs/documents/Event%20Driven%20Processor%20-%20FINAL-1.pdf)**

---