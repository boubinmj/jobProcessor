# Event-Driven Processing Simulator

An event-driven processing simulator built with Spring Boot that demonstrates how modern backend systems process asynchronous workloads using concurrent worker threads.

The application exposes a REST API that accepts incoming events, places them into a shared processing queue, and executes them asynchronously using a configurable pool of worker threads. Completed events are persisted to a database and can be viewed through the application's API and web interface.

This project demonstrates several core backend engineering concepts, including concurrent programming, RESTful networking, and database persistence.

---

## Features

- Submit events through a REST API
- Process events asynchronously using multiple worker threads
- Shared blocking queue implementing the producer-consumer pattern
- Track queued, running, and completed events
- Persist completed events using Spring Data JPA
- View processing history
- Display system metrics such as:
  - Queue size
  - Worker utilization
  - Average processing time
  - Completed event count

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

---

## Architecture

```
                POST /events
                      │
                      ▼
             Spring REST Controller
                      │
                      ▼
              BlockingQueue<Event>
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
      Worker Thread          Worker Thread
          │                       │
          └───────────┬───────────┘
                      ▼
              Event Processing
                      │
                      ▼
                 H2 Database
                      │
                      ▼
          REST API / Dashboard
```

---

## Project Structure

```
src/
├── controller/
├── service/
├── model/
├── repository/
├── worker/
├── config/
└── Application.java
```

---

## Running the Application

Clone the repository.

```bash
git clone <repo-url>
cd event-processing-simulator
```

Run the application.

```bash
./mvnw spring-boot:run
```

The server will start on:

```
http://localhost:8080
```

---

## Planned API

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/events` | Submit a new event |
| GET | `/events` | View all events |
| GET | `/events/{id}` | View a specific event |
| GET | `/stats` | View system statistics |

---

## Example Event

```json
{
  "type": "CREATE_ORDER",
  "size": 5
}
```

The simulator does not perform real business logic. Instead, each event type simulates work by sleeping for a configurable amount of time before completing.

---

## Learning Objectives

This project explores:

- Concurrent programming with `ExecutorService`
- Thread-safe communication using `BlockingQueue`
- Producer-consumer architecture
- REST API development with Spring Boot
- Database persistence using Spring Data JPA
- Backend system design and asynchronous processing