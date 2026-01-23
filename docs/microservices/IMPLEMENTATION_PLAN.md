# PetClinic Microservices Implementation Plan

## Document Information
- **Version**: 1.0
- **Date**: January 23, 2026
- **Reference**: [SPECIFICATION.md](SPECIFICATION.md)

---

## 1. Project Structure Overview

```
spring-petclinic-microservices/
├── pom.xml                           # Parent POM (multi-module)
├── docker-compose.yml                # Local development orchestration
├── config-server/                    # Centralized configuration
│   ├── pom.xml
│   └── src/main/...
├── discovery-server/                 # Eureka service registry
│   ├── pom.xml
│   └── src/main/...
├── api-gateway/                      # Spring Cloud Gateway
│   ├── pom.xml
│   └── src/main/...
├── owner-service/                    # Owner domain service
│   ├── pom.xml
│   └── src/main/...
├── pet-service/                      # Pet domain service
│   ├── pom.xml
│   └── src/main/...
├── visit-service/                    # Visit domain service
│   ├── pom.xml
│   └── src/main/...
├── vet-service/                      # Vet domain service
│   ├── pom.xml
│   └── src/main/...
└── config-repo/                      # Git-based config repository
    ├── application.yml
    ├── owner-service.yml
    ├── pet-service.yml
    ├── visit-service.yml
    └── vet-service.yml
```

---

## 2. Implementation Phases

### Phase 1: Project Foundation & Infrastructure Services

#### 1.1 Create Multi-Module Parent POM
**Duration**: 1 hour

**Tasks**:
- Create parent `pom.xml` with shared dependencies
- Define Spring Boot and Spring Cloud versions
- Configure common plugins

**Dependencies**:
```xml
- spring-boot-starter-parent: 4.0.1
- spring-cloud-dependencies: 2024.0.x
- spring-cloud-starter-config
- spring-cloud-starter-netflix-eureka-client
- spring-cloud-starter-netflix-eureka-server
- spring-cloud-starter-gateway
- spring-cloud-starter-openfeign
```

#### 1.2 Implement Config Server
**Duration**: 1 hour

**Structure**:
```
config-server/
├── pom.xml
├── Dockerfile
└── src/main/
    ├── java/.../ConfigServerApplication.java
    └── resources/
        └── application.yml
```

**Key Configuration**:
- Port: 8888
- Native profile for local file-based config
- Git profile for production

#### 1.3 Create Configuration Repository
**Duration**: 30 minutes

**Structure**:
```
config-repo/
├── application.yml           # Shared config
├── owner-service.yml
├── pet-service.yml
├── visit-service.yml
└── vet-service.yml
```

#### 1.4 Implement Discovery Server (Eureka)
**Duration**: 1 hour

**Structure**:
```
discovery-server/
├── pom.xml
├── Dockerfile
└── src/main/
    ├── java/.../DiscoveryServerApplication.java
    └── resources/
        └── application.yml
```

**Key Configuration**:
- Port: 8761
- Standalone mode for development
- Dashboard enabled

#### 1.5 Implement API Gateway
**Duration**: 2 hours

**Structure**:
```
api-gateway/
├── pom.xml
├── Dockerfile
└── src/main/
    ├── java/.../ApiGatewayApplication.java
    └── resources/
        └── application.yml
```

**Key Configuration**:
- Port: 8080
- Route definitions for all services
- Eureka integration for service discovery
- CORS configuration

**Routes**:
| Path | Service |
|------|---------|
| `/api/owners/**` | owner-service |
| `/api/pets/**` | pet-service |
| `/api/pet-types/**` | pet-service |
| `/api/visits/**` | visit-service |
| `/api/vets/**` | vet-service |
| `/api/specialties/**` | vet-service |

---

### Phase 2: Domain Services Implementation

#### 2.1 Implement Vet Service (Least Coupled)
**Duration**: 3 hours

**Rationale**: Start with vet-service as it has no dependencies on other services.

**Structure**:
```
vet-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/org/springframework/samples/petclinic/vet/
    │   │   ├── VetServiceApplication.java
    │   │   ├── model/
    │   │   │   ├── Vet.java
    │   │   │   ├── Specialty.java
    │   │   │   └── Vets.java
    │   │   ├── repository/
    │   │   │   ├── VetRepository.java
    │   │   │   └── SpecialtyRepository.java
    │   │   ├── controller/
    │   │   │   └── VetController.java
    │   │   └── service/
    │   │       └── VetService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── bootstrap.yml
    │       └── db/
    │           ├── schema.sql
    │           └── data.sql
    └── test/
        └── java/.../
```

**API Endpoints**:
```
GET  /api/vets           - List all vets (paginated, JSON)
GET  /api/vets/{id}      - Get vet by ID
POST /api/vets           - Create vet
PUT  /api/vets/{id}      - Update vet
GET  /api/specialties    - List all specialties
```

**Database Schema** (vet_db):
```sql
CREATE TABLE vets (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(30),
    last_name VARCHAR(30)
);

CREATE TABLE specialties (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80)
);

CREATE TABLE vet_specialties (
    vet_id INTEGER NOT NULL,
    specialty_id INTEGER NOT NULL,
    FOREIGN KEY (vet_id) REFERENCES vets(id),
    FOREIGN KEY (specialty_id) REFERENCES specialties(id),
    UNIQUE (vet_id, specialty_id)
);
```

#### 2.2 Implement Owner Service
**Duration**: 3 hours

**Structure**:
```
owner-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/org/springframework/samples/petclinic/owner/
    │   │   ├── OwnerServiceApplication.java
    │   │   ├── model/
    │   │   │   └── Owner.java
    │   │   ├── repository/
    │   │   │   └── OwnerRepository.java
    │   │   ├── controller/
    │   │   │   └── OwnerController.java
    │   │   └── service/
    │   │       └── OwnerService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── bootstrap.yml
    │       └── db/
    │           ├── schema.sql
    │           └── data.sql
    └── test/
```

**API Endpoints**:
```
GET    /api/owners              - List owners (paginated)
GET    /api/owners/{id}         - Get owner by ID
POST   /api/owners              - Create owner
PUT    /api/owners/{id}         - Update owner
DELETE /api/owners/{id}         - Delete owner
GET    /api/owners/search?lastName={name} - Search by last name
```

**Key Changes from Monolith**:
- Remove `List<Pet> pets` relationship (decoupled)
- Owner entity is self-contained
- No cascade operations to pets

**Database Schema** (owner_db):
```sql
CREATE TABLE owners (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(30),
    last_name VARCHAR(30),
    address VARCHAR(255),
    city VARCHAR(80),
    telephone VARCHAR(20)
);
```

#### 2.3 Implement Pet Service
**Duration**: 4 hours

**Structure**:
```
pet-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/org/springframework/samples/petclinic/pet/
    │   │   ├── PetServiceApplication.java
    │   │   ├── model/
    │   │   │   ├── Pet.java
    │   │   │   └── PetType.java
    │   │   ├── dto/
    │   │   │   ├── PetRequest.java
    │   │   │   └── PetResponse.java
    │   │   ├── repository/
    │   │   │   ├── PetRepository.java
    │   │   │   └── PetTypeRepository.java
    │   │   ├── controller/
    │   │   │   ├── PetController.java
    │   │   │   └── PetTypeController.java
    │   │   ├── service/
    │   │   │   └── PetService.java
    │   │   └── client/
    │   │       └── OwnerServiceClient.java  # Feign client
    │   └── resources/
    │       ├── application.yml
    │       ├── bootstrap.yml
    │       └── db/
    │           ├── schema.sql
    │           └── data.sql
    └── test/
```

**API Endpoints**:
```
GET    /api/pets                    - List all pets
GET    /api/pets/{id}               - Get pet by ID
POST   /api/pets                    - Create pet (validates ownerId)
PUT    /api/pets/{id}               - Update pet
DELETE /api/pets/{id}               - Delete pet
GET    /api/pets/owner/{ownerId}    - Get pets by owner
GET    /api/pet-types               - List all pet types
GET    /api/pet-types/{id}          - Get pet type by ID
```

**Key Changes from Monolith**:
- `ownerId` stored as reference (not JPA relationship)
- Feign client to validate owner exists
- Remove `List<Visit> visits` relationship

**Database Schema** (pet_db):
```sql
CREATE TABLE pet_types (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80)
);

CREATE TABLE pets (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30),
    birth_date DATE,
    type_id INTEGER NOT NULL,
    owner_id INTEGER NOT NULL,  -- Reference to owner-service
    FOREIGN KEY (type_id) REFERENCES pet_types(id)
);
```

**Feign Client**:
```java
@FeignClient(name = "owner-service")
public interface OwnerServiceClient {
    @GetMapping("/api/owners/{ownerId}")
    OwnerDto getOwner(@PathVariable Integer ownerId);
}
```

#### 2.4 Implement Visit Service
**Duration**: 3 hours

**Structure**:
```
visit-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/org/springframework/samples/petclinic/visit/
    │   │   ├── VisitServiceApplication.java
    │   │   ├── model/
    │   │   │   └── Visit.java
    │   │   ├── dto/
    │   │   │   ├── VisitRequest.java
    │   │   │   └── VisitResponse.java
    │   │   ├── repository/
    │   │   │   └── VisitRepository.java
    │   │   ├── controller/
    │   │   │   └── VisitController.java
    │   │   ├── service/
    │   │   │   └── VisitService.java
    │   │   └── client/
    │   │       └── PetServiceClient.java  # Feign client
    │   └── resources/
    │       ├── application.yml
    │       ├── bootstrap.yml
    │       └── db/
    │           ├── schema.sql
    │           └── data.sql
    └── test/
```

**API Endpoints**:
```
GET    /api/visits                  - List all visits
GET    /api/visits/{id}             - Get visit by ID
POST   /api/visits                  - Create visit (validates petId)
PUT    /api/visits/{id}             - Update visit
DELETE /api/visits/{id}             - Delete visit
GET    /api/visits/pet/{petId}      - Get visits by pet
```

**Database Schema** (visit_db):
```sql
CREATE TABLE visits (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    pet_id INTEGER NOT NULL,  -- Reference to pet-service
    visit_date DATE,
    description VARCHAR(255)
);
```

---

### Phase 3: Integration & Cross-Cutting Concerns

#### 3.1 Configure Feign Clients
**Duration**: 2 hours

**Tasks**:
- Add `@EnableFeignClients` to service applications
- Implement circuit breakers with Resilience4j
- Add fallback methods for service unavailability

**Example Circuit Breaker**:
```java
@FeignClient(name = "owner-service", fallback = OwnerServiceFallback.class)
public interface OwnerServiceClient {
    @GetMapping("/api/owners/{ownerId}")
    OwnerDto getOwner(@PathVariable Integer ownerId);
}

@Component
public class OwnerServiceFallback implements OwnerServiceClient {
    @Override
    public OwnerDto getOwner(Integer ownerId) {
        throw new ServiceUnavailableException("Owner service unavailable");
    }
}
```

#### 3.2 API Gateway Route Configuration
**Duration**: 1 hour

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: owner-service
          uri: lb://owner-service
          predicates:
            - Path=/api/owners/**
        - id: pet-service
          uri: lb://pet-service
          predicates:
            - Path=/api/pets/**, /api/pet-types/**
        - id: visit-service
          uri: lb://visit-service
          predicates:
            - Path=/api/visits/**
        - id: vet-service
          uri: lb://vet-service
          predicates:
            - Path=/api/vets/**, /api/specialties/**
```

#### 3.3 Error Handling & Validation
**Duration**: 2 hours

**Tasks**:
- Create shared exception classes
- Implement `@ControllerAdvice` for each service
- Standardize error response format

**Standard Error Response**:
```json
{
  "timestamp": "2026-01-23T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Owner not found with id: 999",
  "path": "/api/owners/999"
}
```

#### 3.4 Distributed Tracing Setup
**Duration**: 1 hour

**Tasks**:
- Add `spring-cloud-starter-sleuth` to all services
- Configure correlation ID propagation
- Add tracing headers to Feign clients

---

### Phase 4: Containerization & Orchestration

#### 4.1 Create Dockerfiles
**Duration**: 2 hours

**Standard Dockerfile** (per service):
```dockerfile
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
```

#### 4.2 Create Docker Compose
**Duration**: 2 hours

```yaml
version: '3.8'
services:
  config-server:
    build: ./config-server
    ports:
      - "8888:8888"
    
  discovery-server:
    build: ./discovery-server
    ports:
      - "8761:8761"
    depends_on:
      - config-server
    
  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - discovery-server
    
  owner-service:
    build: ./owner-service
    ports:
      - "8081:8081"
    depends_on:
      - discovery-server
      - config-server
    
  pet-service:
    build: ./pet-service
    ports:
      - "8082:8082"
    depends_on:
      - discovery-server
      - config-server
      - owner-service
    
  visit-service:
    build: ./visit-service
    ports:
      - "8083:8083"
    depends_on:
      - discovery-server
      - config-server
      - pet-service
    
  vet-service:
    build: ./vet-service
    ports:
      - "8084:8084"
    depends_on:
      - discovery-server
      - config-server
```

#### 4.3 Update Kubernetes Manifests
**Duration**: 2 hours

**Tasks**:
- Create deployment YAML for each service
- Create service YAML for internal communication
- Create ingress for API Gateway
- Create ConfigMaps for configuration

---

### Phase 5: Testing

#### 5.1 Unit Tests
**Duration**: 4 hours

**Per Service**:
- Repository tests with `@DataJpaTest`
- Service layer tests with mocked dependencies
- Controller tests with `@WebMvcTest`

#### 5.2 Integration Tests
**Duration**: 4 hours

**Tasks**:
- Testcontainers for database testing
- `@SpringBootTest` for full context tests
- Feign client tests with WireMock

#### 5.3 End-to-End Tests
**Duration**: 2 hours

**Tasks**:
- Docker Compose based E2E tests
- API contract testing
- Smoke tests for critical paths

---

## 3. Implementation Timeline

| Phase | Task | Duration | Dependencies |
|-------|------|----------|--------------|
| 1.1 | Parent POM | 1h | None |
| 1.2 | Config Server | 1h | 1.1 |
| 1.3 | Config Repository | 30m | 1.2 |
| 1.4 | Discovery Server | 1h | 1.1 |
| 1.5 | API Gateway | 2h | 1.4 |
| 2.1 | Vet Service | 3h | 1.3, 1.4 |
| 2.2 | Owner Service | 3h | 1.3, 1.4 |
| 2.3 | Pet Service | 4h | 2.2 |
| 2.4 | Visit Service | 3h | 2.3 |
| 3.1 | Feign Clients | 2h | 2.1-2.4 |
| 3.2 | Gateway Routes | 1h | 1.5, 2.1-2.4 |
| 3.3 | Error Handling | 2h | 2.1-2.4 |
| 3.4 | Distributed Tracing | 1h | 2.1-2.4 |
| 4.1 | Dockerfiles | 2h | 2.1-2.4 |
| 4.2 | Docker Compose | 2h | 4.1 |
| 4.3 | Kubernetes | 2h | 4.1 |
| 5.1 | Unit Tests | 4h | 2.1-2.4 |
| 5.2 | Integration Tests | 4h | 5.1 |
| 5.3 | E2E Tests | 2h | 4.2 |

**Total Estimated Duration**: ~40 hours

---

## 4. Service Startup Order

```
1. config-server     (standalone, no dependencies)
2. discovery-server  (depends on config-server)
3. api-gateway       (depends on discovery-server)
4. vet-service       (depends on config + discovery)
5. owner-service     (depends on config + discovery)
6. pet-service       (depends on config + discovery + owner-service)
7. visit-service     (depends on config + discovery + pet-service)
```

---

## 5. Verification Checklist

### Infrastructure Services
- [ ] Config Server starts and serves configuration
- [ ] Discovery Server starts and dashboard accessible at :8761
- [ ] API Gateway starts and routes requests

### Domain Services
- [ ] Vet Service: CRUD operations work via API
- [ ] Owner Service: CRUD operations work via API
- [ ] Pet Service: CRUD with owner validation works
- [ ] Visit Service: CRUD with pet validation works

### Integration
- [ ] Services register with Eureka
- [ ] Feign clients can call other services
- [ ] API Gateway routes to all services
- [ ] Circuit breakers trigger on failures

### Deployment
- [ ] All Dockerfiles build successfully
- [ ] Docker Compose starts entire stack
- [ ] Services are accessible via Gateway

---

## 6. Rollback Plan

If migration fails:
1. Keep original monolith codebase intact (in separate branch)
2. Database schemas are separate, original DB unchanged
3. Can revert by switching back to monolith deployment
4. No data migration required (fresh databases for microservices)

---

## 7. Post-Migration Considerations

### Immediate Follow-ups
- Monitor service health and latency
- Tune connection pool sizes
- Adjust timeout configurations

### Future Enhancements
- Add distributed caching (Redis)
- Implement event-driven communication
- Add API versioning
- Implement rate limiting
- Add authentication/authorization
