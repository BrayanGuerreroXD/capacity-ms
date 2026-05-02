# Capacity Microservice

Spring WebFlux API for managing capacities, consuming and publishing events via Apache Kafka, and exposing reactive REST endpoints for bootcamp capacity management.

## Technologies
- Spring WebFlux
- Spring Security
- Spring Data R2DBC (Reactive MySQL)
- Apache Kafka
- Java 25
- Gradle
- MySQL
- MapStruct
- OpenApi Swagger
- Flyway
- Lombok

## Architecture

This project follows **Clean Architecture** principles as implemented in the Bancolombia scaffold. The architecture is organized into independent layers that facilitate maintenance and scalability.

### Project Structure (Based on Bancolombia Scaffold)

```
capacity-ms/
├── applications/                 # Application layer (entry points)
│   └── app-service/             # Main application service
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/        # Main source code
│       │   │   └── resources/   # Configuration resources
│       │   └── test/            # Tests
│       └── build.gradle         # Gradle configuration for the service
├── domain/                      # Domain layer (pure business)
│   ├── model/                   # Entities and domain models
│   └── usecase/                 # Application use cases
├── infrastructure/              # Infrastructure layer (technical details)
│   ├── driven-adapters/         # Adapters to external systems
│   │   ├── r2dbc-repository/    # Reactive MySQL repository adapter
│   │   └── kafka/               # Kafka producer/consumer adapters
│   └── entry-points/            # Application entry points
│       ├── reactive-web/        # Reactive web adapter (WebFlux)
│       └── kafka-consumer/      # Kafka consumer adapter
├── deployment/                  # Deployment configurations
├── build.gradle                 # Root Gradle configuration
├── settings.gradle              # Multi-project configuration
└── README.md                    # This file
```

### Layer Details

1. **Domain**: Contains pure business logic, independent of frameworks and technologies.
   - `model`: Entities representing business concepts (Capacity, TechnologyCatalog, CapacityTechnology, CapacityBootcamp, Auth)
   - `usecase`: Implementation of use cases that orchestrate application logic

2. **Infrastructure**: Technical implementation details.
   - `driven-adapters`: Adapters that allow the domain to communicate with the outside world (databases, message queues)
   - `entry-points`: System entry points (REST APIs, message consumers)

3. **Applications**: Specific configuration for each service/application.
   - Contains the main class with the `main` method
   - Configures beans and dependencies specific to the service

## Configuration

Example configuration in `applications/app-service/src/main/resources/application.yml`:

```yaml
server:
  port: 7700
spring:
  application:
    name: capacity-ms
  r2dbc:
    url: r2dbc:mysql://localhost:3306/db_capacity
    username: root
    password: 1234
  flyway:
    url: jdbc:mysql://localhost:3306/db_capacity
    user: root
    password: 1234
  kafka:
    bootstrap-servers: localhost:9092

cors:
  allowed-origins: "http://localhost:4200,http://localhost:7700"

kafka:
  topics:
    sync-technology-catalog: sync.technologies.catalog
    sync-technology-deleted: sync.technologies.deleted
    sync-capacity-catalog: sync.capacity.catalog
    sync-capacity-deleted: sync.capacity.deleted
    sync-capacity-technology-catalog: sync.capacity.technology.catalog
    sync-capacity-technology-deleted: sync.capacity.technology.deleted
    sync-technologies-capacities-match: sync.technologies.capacities.match
    sync-capacities-bootcamps-match: sync.capacities.bootcamps.match
    auth-login-admin: auth.login.admin
    generic-auth-logout: generic.auth.logout
```

## Main Endpoints

### Capacities
- `POST /api/v1/capacities` - Create a new capacity
- `GET /api/v1/capacities` - Get all capacities (paginated: `?page=0&size=10`)
- `GET /api/v1/capacities/{id}` - Get a capacity by ID
- `PUT /api/v1/capacities/{id}` - Update an existing capacity
- `DELETE /api/v1/capacities/{id}` - Delete a capacity

### Technology Catalogs
- `GET /api/v1/technology-catalogs` - Get all technology catalogs

### Security
- All endpoints except `/actuator/**` and Swagger documentation require authentication
- Unauthorized requests return HTTP 401
- Access denied returns HTTP 403

## Kafka Events

### Consumed Events

| Topic | Consumer | Purpose |
|-------|----------|---------|
| `sync.technologies.catalog` | `TechnologyCatalogConsumer` | Syncs technology catalog entries |
| `sync.technologies.deleted` | `TechnologyDeletedConsumer` | Deletes technology catalog entries |
| `auth.login.admin` | `AuthLoginConsumer` | Saves auth login events |
| `generic.auth.logout` | `AuthLogoutConsumer` | Deletes auth on logout |
| `sync.capacities.bootcamps.match` | `CapacityBootcampSyncConsumer` | Syncs bootcamp-capacity relationships |

### Produced Events

| Topic | Publisher | Purpose |
|-------|-----------|---------|
| `sync.capacity.catalog` | `CapacityEventPublisherAdapter` | Publishes capacity creation/update events |
| `sync.capacity.deleted` | `CapacityEventPublisherAdapter` | Publishes capacity deletion events |
| `sync.capacity.technology.catalog` | `CapacityTechnologyEventPublisherAdapter` | Publishes capacity-technology associations |
| `sync.capacity.technology.deleted` | `CapacityTechnologyEventPublisherAdapter` | Publishes capacity-technology deletions |
| `sync.technologies.capacities.match` | `SyncTechnologyCapacityEventPublisherAdapter` | Publishes technology-capacity matching events |

## Development Commands

### Start the API

```bash
# From the project root
./gradlew applications:app-service:bootRun
```

The API will be available at `http://localhost:7700`

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew applications:app-service:test
./gradlew domain:model:test
./gradlew domain:usecase:test
./gradlew infrastructure:driven-adapters:r2dbc-repository:test
./gradlew infrastructure:entry-points:reactive-web:test
```

### Generate OpenApi Documentation (Swagger)

Once the application is running, access:
- Swagger UI: `http://localhost:7700/swagger-ui.html`
- OpenApi JSON: `http://localhost:7700/v3/api-docs`

## Implemented Features

- Clean Architecture following Bancolombia principles
- Reactive programming with Spring WebFlux and R2DBC
- Authentication and authorization with Spring Security
- Kafka consumer for event-driven capacity synchronization
- Kafka producer for publishing capacity and technology events
- MapStruct for entity/DTO mapping
- Global exception handling
- CORS configuration
- Health checks and metrics with Actuator
- Database migrations with Flyway
- Lombok for boilerplate reduction

## Prerequisites

- Java 25
- Gradle 8.x
- MySQL 8.x
- Apache Kafka
- Docker (optional, for development with containers)