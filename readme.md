# Event-Driven Processing Simulator

A concurrent event processing system built with Spring Boot and Docker. Submit events via REST API, watch them get processed by a pool of worker threads in real-time through an interactive dashboard.

**For detailed documentation, see [DESCRIPTION.md](DESCRIPTION.md)**

---

## Quick Start

### Option 1: Docker (Recommended - No Java/Maven Required)

```bash
docker-compose up --build
```

Application will be available at: **http://localhost:8080**

### Option 2: Local Maven Build

```bash
mvn clean install
mvn spring-boot:run
```

Application will be available at: **http://localhost:8080**

---

## Access the Application

- **Dashboard**: http://localhost:8080/index.html
- **REST API**: http://localhost:8080/api/events
- **H2 Console**: http://localhost:8080/h2-console

---

## What's Inside

- Submit events via REST API
- Watch them get processed by 3 concurrent worker threads
- View real-time metrics: queue size, active workers, completion rate, processing time, worker utilization
- Interactive web dashboard with auto-refreshing stats
- 100% containerized with Docker

---

## Learn More

For comprehensive documentation including:
- Detailed architecture overview
- Complete REST API reference
- Event lifecycle and timing
- Configuration options
- Design patterns and learning objectives
- Troubleshooting guide

**See [DESCRIPTION.md](DESCRIPTION.md)**

---