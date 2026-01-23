# Spring PetClinic Microservices

This is the microservices version of the Spring PetClinic application, decomposed from the original monolithic architecture.

## Architecture Overview

```
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │     (8080)      │
                                    └────────┬────────┘
                                             │
              ┌──────────────────────────────┼──────────────────────────────┐
              │                              │                              │
              ▼                              ▼                              ▼
    ┌─────────────────┐          ┌─────────────────┐          ┌─────────────────┐
    │  Owner Service  │◄────────►│   Pet Service   │◄────────►│  Visit Service  │
    │    (8081)       │          │     (8082)      │          │     (8083)      │
    └─────────────────┘          └─────────────────┘          └─────────────────┘

              ┌─────────────────┐          ┌─────────────────┐
              │   Vet Service   │          │  Config Server  │
              │     (8084)      │          │     (8888)      │
              └─────────────────┘          └─────────────────┘

                                 ┌─────────────────┐
                                 │Discovery Server │
                                 │  (Eureka 8761)  │
                                 └─────────────────┘
```

## Services

| Service | Port | Description |
|---------|------|-------------|
| Config Server | 8888 | Centralized configuration management |
| Discovery Server | 8761 | Service registry (Netflix Eureka) |
| API Gateway | 8080 | Single entry point, routing, load balancing |
| Owner Service | 8081 | Pet owner management |
| Pet Service | 8082 | Pet and pet type management |
| Visit Service | 8083 | Veterinary visit management |
| Vet Service | 8084 | Veterinarian and specialty management |

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optional)

## Building

Build all services:

```bash
cd microservices
mvn clean package -DskipTests
```

## Running Locally

### Option 1: Run services individually

Start services in this order:

```bash
# 1. Config Server
cd config-server && mvn spring-boot:run

# 2. Discovery Server (new terminal)
cd discovery-server && mvn spring-boot:run

# 3. API Gateway (new terminal)
cd api-gateway && mvn spring-boot:run

# 4. Domain Services (new terminals)
cd vet-service && mvn spring-boot:run
cd owner-service && mvn spring-boot:run
cd pet-service && mvn spring-boot:run
cd visit-service && mvn spring-boot:run
```

### Option 2: Docker Compose

```bash
cd microservices
mvn clean package -DskipTests
docker-compose up --build
```

## API Endpoints

All APIs are accessible through the API Gateway at `http://localhost:8080`.

### Owner Service
```
GET    /api/owners              - List all owners
GET    /api/owners/{id}         - Get owner by ID
POST   /api/owners              - Create owner
PUT    /api/owners/{id}         - Update owner
DELETE /api/owners/{id}         - Delete owner
GET    /api/owners/search?lastName= - Search by last name
```

### Pet Service
```
GET    /api/pets                - List all pets
GET    /api/pets/{id}           - Get pet by ID
POST   /api/pets                - Create pet
PUT    /api/pets/{id}           - Update pet
DELETE /api/pets/{id}           - Delete pet
GET    /api/pets/owner/{ownerId} - Get pets by owner
GET    /api/pet-types           - List all pet types
```

### Visit Service
```
GET    /api/visits              - List all visits
GET    /api/visits/{id}         - Get visit by ID
POST   /api/visits              - Create visit
PUT    /api/visits/{id}         - Update visit
DELETE /api/visits/{id}         - Delete visit
GET    /api/visits/pet/{petId}  - Get visits by pet
```

### Vet Service
```
GET    /api/vets                - List all vets (JSON)
GET    /api/vets/{id}           - Get vet by ID
POST   /api/vets                - Create vet
PUT    /api/vets/{id}           - Update vet
GET    /api/specialties         - List all specialties
```

## Accessing Services

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Config Server**: http://localhost:8888

### Example API Calls

```bash
# Get all owners via API Gateway
curl http://localhost:8080/api/owners

# Get all vets
curl http://localhost:8080/api/vets

# Create a new owner
curl -X POST http://localhost:8080/api/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "address": "123 Main St",
    "city": "Springfield",
    "telephone": "1234567890"
  }'

# Get pets for an owner
curl http://localhost:8080/api/pets/owner/1

# Create a visit
curl -X POST http://localhost:8080/api/visits \
  -H "Content-Type: application/json" \
  -d '{
    "petId": 1,
    "date": "2026-01-23",
    "description": "Annual checkup"
  }'
```

## Database

Each service uses its own H2 in-memory database for development. Data is initialized from `db/data.sql` files in each service.

### H2 Console Access

Each service exposes H2 console (dev only):
- Owner Service: http://localhost:8081/h2-console
- Pet Service: http://localhost:8082/h2-console
- Visit Service: http://localhost:8083/h2-console
- Vet Service: http://localhost:8084/h2-console

## Inter-Service Communication

- **Pet Service** → **Owner Service**: Validates owner exists when creating/updating pets
- **Visit Service** → **Pet Service**: Validates pet exists when creating/updating visits

Communication uses OpenFeign with circuit breakers (Resilience4j) for fault tolerance.

## Project Structure

```
microservices/
├── pom.xml                    # Parent POM
├── docker-compose.yml         # Docker orchestration
├── config-server/             # Centralized configuration
├── discovery-server/          # Eureka service registry
├── api-gateway/               # Spring Cloud Gateway
├── owner-service/             # Owner domain service
├── pet-service/               # Pet domain service
├── visit-service/             # Visit domain service
└── vet-service/               # Vet domain service
```

## Key Changes from Monolith

1. **Database per Service**: Each service has its own database
2. **REST APIs**: All services expose REST endpoints (no server-side rendering)
3. **Decoupled Entities**: 
   - `Owner` no longer has direct relationship to `Pet`
   - `Pet` no longer has direct relationship to `Visit`
   - References stored as IDs and validated via API calls
4. **Service Discovery**: Services register with Eureka
5. **Centralized Config**: Configuration managed by Config Server
6. **API Gateway**: Single entry point for all client requests

## Future Enhancements

- [ ] Add authentication/authorization (OAuth2, JWT)
- [ ] Implement distributed tracing (Zipkin/Jaeger)
- [ ] Add centralized logging (ELK stack)
- [ ] Event-driven communication (Kafka/RabbitMQ)
- [ ] Frontend SPA migration
- [ ] Kubernetes deployment manifests
- [ ] CI/CD pipeline
