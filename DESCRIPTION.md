# Event-Driven Processing Simulator - Detailed Documentation

An event-driven processing simulator built with Spring Boot that demonstrates how modern backend systems process asynchronous workloads using concurrent worker threads.

The application exposes a REST API that accepts incoming events, places them into a shared processing queue, and executes them asynchronously using a configurable pool of worker threads. Completed events are persisted to a database and can be viewed through the application's API and web interface.

This project demonstrates several core backend engineering concepts, including concurrent programming, RESTful networking, and database persistence.

---

## Features

✅ **Fully Implemented:**

- Submit events through REST API (`POST /api/events`)
- Process events asynchronously using configurable pool of worker threads (default: 3)
- Shared blocking queue implementing the producer-consumer pattern
- Four event types with realistic business names
- Track event states: QUEUED → RUNNING → COMPLETED
- Persist completed events using Spring Data JPA with H2 database
- Query events by status (queued, running, completed)
- Retrieve individual event details by ID
- View processing history of all completed events
- Display real-time system metrics:
  - Queue size (pending events)
  - Active worker count (currently processing events)
  - Completed event count (total finished)
  - Average processing time (across all completed events)
  - Worker utilization percentage (active threads as % of total workers)
- Interactive web dashboard with real-time auto-refresh
- RESTful API with JSON request/response format
- Automatic database schema generation (Hibernate DDL)

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
    Web Browser              REST API                Worker Threads
        │                       │                          │
        ├──────POST /api/events─┤                          │
        │                       ├─────► EventProcessor     │
        │                       │           │               │
        │                       │           ├──► BlockingQueue
        │                       │           │               │
        │                       │           │      ┌────────┼────────┬────────┐
        │                       │           │      ▼        ▼        ▼        ▼
        │                       │           │   Worker1  Worker2  Worker3  (more)
        │                       │           │      │        │        │
        │                  Event saved ◄───┤      └────────┼────────┘
        │                  to database  │ EventRepository  │
        │                       │           │               │
        │◄──────GET /api/events─┤◄──────────┴───────────────┤
        │   (with metrics)      │                          │
        │                       │    H2 Database           │
        │   Dashboard           │    (Persisted Events)    │
        │   with metrics        │                          │
        └───────────────────────┘

Event Lifecycle:
  1. Client submits event via POST /api/events
  2. EventService creates Event entity (status: QUEUED)
  3. Event saved to database
  4. Event placed in BlockingQueue
  5. Worker thread dequeues event (status: RUNNING)
  6. Worker simulates processing via Thread.sleep()
  7. Worker marks event COMPLETED with result
  8. Event updated in database
  9. Client queries via GET /api/events or dashboard
```

---

## Project Structure

```
src/main/java/com/example/
├── MyApplication.java                    # Spring Boot entry point & startup listener
├── controller/
│   └── EventController.java              # REST API endpoints
├── service/
│   ├── EventService.java                 # Business logic for event management
│   └── MetricsService.java               # System metrics calculation
├── model/
│   ├── Event.java                        # JPA entity
│   ├── EventType.java                    # Enum: CREATE_ORDER, RECEIVE_PAYMENT, UPDATE_INVENTORY, SHIP_ORDER
│   └── EventStatus.java                  # Enum: QUEUED, RUNNING, COMPLETED
├── repository/
│   └── EventRepository.java              # Spring Data JPA repository
├── worker/
│   ├── EventProcessor.java               # Orchestrates queue & worker threads
│   └── WorkerThread.java                 # Worker thread implementation
└── config/
    └── ProcessorConfig.java              # Bean configuration (ExecutorService, BlockingQueue)

src/main/resources/
├── application.properties                # Server & application configuration
└── static/
    └── index.html                        # Web dashboard UI
```

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.6+

### Start the Application

```bash
cd jobProcessor
mvn clean install
mvn spring-boot:run
```

The server will start on:

```
http://localhost:8080
```

### Access the Application

- **Web Dashboard**: http://localhost:8080/index.html
- **API Base**: http://localhost:8080/api/events
- **H2 Console**: http://localhost:8080/h2-console

---

## REST API Endpoints

| Method | Endpoint | Description | Example Request |
|---------|----------|-------------|-----------------|
| POST | `/api/events` | Submit a new event | `{"eventType": "CREATE_ORDER", "size": 5}` |
| GET | `/api/events` | Get all events (or filter by status) | `/api/events?status=queued` |
| GET | `/api/events?status=queued` | Get queued events | - |
| GET | `/api/events?status=running` | Get running events | - |
| GET | `/api/events?status=completed` | Get completed events | - |
| GET | `/api/events/{id}` | Get specific event by ID | `/api/events/19fba4fc-60b5-4b36-965e-fb24ef45be67` |
| GET | `/api/events/metrics` | Get system metrics | - |

### Request Example

```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"eventType": "CREATE_ORDER", "size": 5}'
```

### Response Example

```json
{
  "id": "19fba4fc-60b5-4b36-965e-fb24ef45be67",
  "eventType": "CREATE_ORDER",
  "status": "QUEUED",
  "size": 5,
  "processingTimeMs": 5000,
  "createdAt": "2026-08-03T21:30:00",
  "startedAt": null,
  "completedAt": null,
  "result": null
}
```

### Metrics Response Example

```json
{
  "queueSize": 0,
  "activeWorkers": 1,
  "completedEventCount": 2,
  "totalEventCount": 2,
  "averageProcessingTimeMs": 4004.0,
  "workerUtilizationPercent": 50.0
}
```

---

## Event Types

The application supports four event types that simulate different business operations:

| Event Type | Display Name | Processing |
|------------|-------------|------------|
| `CREATE_ORDER` | Create Order | Simulates order creation |
| `RECEIVE_PAYMENT` | Receive Payment | Simulates payment processing |
| `UPDATE_INVENTORY` | Update Inventory | Simulates inventory updates |
| `SHIP_ORDER` | Ship Order | Simulates order shipment |

### Processing Time Calculation

Processing time is calculated as:

```
processingTimeMs = baseProcessingTime × eventSize
```

**Default**: `baseProcessingTime = 1000ms`, so a size 5 event takes 5 seconds to process.

### Running with Docker

The easiest way to run this application is with Docker—no Java or Maven installation required.

#### Prerequisites
- Docker and docker-compose installed

#### Quick Start

```bash
cd jobProcessor
docker-compose up --build
```

The application will be available at:
```
http://localhost:8080
```

**First run**: The image build may take a few minutes (Java, Maven, and dependencies). Subsequent runs start instantly.

**Stopping the application**:
```bash
docker-compose down
```

#### Configuration

You can override the default worker thread count when starting the container:

```bash
docker-compose run -e WORKER_THREAD_COUNT=5 jobprocessor
```

Or modify the `docker-compose.yml` environment variable before starting:

```yaml
environment:
  - WORKER_THREAD_COUNT=5  # Change from default of 3
```

---

### Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Number of concurrent worker threads (default: 3)
# Can be overridden via WORKER_THREAD_COUNT environment variable
worker.thread.count=${WORKER_THREAD_COUNT:3}

# Base processing time in milliseconds (default: 1000)
event.base.processing.time.ms=1000

# Server port (default: 8080)
server.port=8080
```

---

## Web Dashboard

The application includes a responsive web dashboard at `http://localhost:8080/index.html` featuring:

- **Event Submission Form**: Submit events with configurable type and size
- **Queued Events Panel**: View events waiting to be processed
- **Running Events Panel**: Monitor actively processing events with progress
- **Completed Events Panel**: View history of processed events with results
- **System Metrics**: Real-time display of:
  - Queue size
  - Active worker threads
  - Completed event count
  - Average processing time
  - Worker utilization percentage
  - Total events processed
- **Auto-Refresh**: Dashboard updates every 1 second

---

## Event Lifecycle

Events flow through the following states:

1. **QUEUED**: Event submitted to REST API → stored in database → placed in BlockingQueue
2. **RUNNING**: Worker thread dequeues event → updates status → begins processing (simulated with Thread.sleep)
3. **COMPLETED**: Processing finishes → event marked complete → stored with result in database

### Timeline Example

For a CREATE_ORDER event with size 5:

```
T=0s:    Event submitted via REST API
T=0.01s: Event stored to database with QUEUED status
T=0.02s: Event placed in BlockingQueue
T=0.05s: Worker thread dequeues event & marks RUNNING
T=5.05s: Worker thread completes processing (5 seconds simulated work)
T=5.06s: Event marked COMPLETED with result
T=5.07s: Event persisted to database
```

---

## Learning Objectives

This project explores:

- Concurrent programming with `ExecutorService` and custom `Runnable` threads
- Thread-safe communication using `BlockingQueue` (producer-consumer pattern)
- REST API development with Spring Boot and Spring Web MVC
- Database persistence using Spring Data JPA and Hibernate ORM
- Entity lifecycle management and relationship mapping
- Spring Bean configuration and dependency injection
- Event-driven asynchronous processing
- System design and architecture for scalable backend systems
- Real-time metrics calculation and monitoring
- Frontend-backend integration with JavaScript and REST APIs

---

## Testing & Verification

### Manual Testing with cURL

**1. Submit an event:**
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"eventType":"CREATE_ORDER","size":3}'
```

**2. Get event status:**
```bash
curl http://localhost:8080/api/events/{event-id}
```

**3. Get all completed events:**
```bash
curl http://localhost:8080/api/events?status=completed
```

**4. Get current metrics:**
```bash
curl http://localhost:8080/api/events/metrics
```

### Dashboard Testing

1. Open http://localhost:8080/index.html
2. Select an event type from the dropdown
3. Adjust event size (1-100)
4. Click "Submit Event"
5. Observe:
   - Event appears in "Queued Events" (briefly)
   - Event moves to "Running Events" (for configured duration)
   - Event appears in "Completed Events" with result
   - Metrics update in real-time
   - Average processing time and worker utilization calculate correctly

### Load Testing

Submit multiple events quickly to observe:

```bash
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/events \
    -H "Content-Type: application/json" \
    -d '{"eventType":"SHIP_ORDER","size":2}' \
    -s &
done
```

Observe in the dashboard:
- Events queue up
- Worker threads process 3 in parallel
- Queue processes FIFO order
- All events eventually complete
- Metrics accurately reflect concurrent processing

---

## Design Patterns & Architecture

### Patterns Implemented

1. **Producer-Consumer Pattern**
   - `EventController` acts as producer (submits events to queue)
   - `WorkerThread` instances act as consumers (dequeue and process)
   - `BlockingQueue` ensures thread-safe handoff between producer and consumers

2. **Thread Pool Pattern (ExecutorService)**
   - Fixed pool of 3 worker threads (configurable)
   - Reusable thread pool avoids thread creation overhead
   - Graceful shutdown on application termination

3. **Repository Pattern**
   - `EventRepository` abstracts database access
   - Spring Data JPA provides CRUD operations automatically
   - Decouples business logic from persistence details

4. **Dependency Injection**
   - All dependencies injected via Spring constructor injection
   - No hard-coded dependencies or service locators
   - Easy to mock and test

5. **Service Layer Pattern**
   - `EventService` handles business logic
   - `MetricsService` encapsulates metrics calculations
   - Controllers delegate to services

### Key Design Decisions

- **In-Memory Database**: H2 provides a lightweight, portable database suitable for course projects
- **Event UUID**: String-based UUIDs ensure globally unique event IDs
- **Timestamps**: All state transitions recorded for audit trail and metrics
- **Lazy Status Updates**: Event status updated in database immediately to ensure consistency
- **Auto-Refresh Dashboard**: 1-second polling provides real-time visibility without WebSocket complexity
- **Vanilla JavaScript**: No framework dependencies for maximum portability

---

## Troubleshooting

**Application won't start:**
- Ensure Java 17+ is installed: `java -version`
- Check port 8080 is not in use: `lsof -i :8080`
- Check Maven installation: `mvn -v`

**Database errors:**
- H2 database is in-memory; data resets on app restart
- To persist data, modify `application.properties`:
  ```properties
  spring.datasource.url=jdbc:h2:file:./eventdb
  spring.jpa.hibernate.ddl-auto=update
  ```

**Dashboard not loading:**
- Clear browser cache (Ctrl+Shift+Delete or Cmd+Shift+Delete)
- Check browser console for JavaScript errors (F12)
- Verify API is accessible: curl http://localhost:8080/api/events/metrics

**Events not processing:**
- Check worker threads started (see console output)
- Verify database connectivity
- Check application.properties for worker thread count setting

---

## Database Schema

The application creates a single `EVENTS` table:

```sql
CREATE TABLE events (
  id VARCHAR(36) PRIMARY KEY,
  event_type VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  size INTEGER NOT NULL,
  processing_time_ms BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  started_at TIMESTAMP,
  completed_at TIMESTAMP,
  result VARCHAR(1000)
);
```

View the H2 console at: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave blank)
