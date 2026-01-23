# PetClinic Microservices - Task Breakdown

## Document Information
- **Version**: 1.0
- **Date**: January 23, 2026
- **Reference**: [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md)

---

## Task Categories

| Category | Description |
|----------|-------------|
| 🏗️ INFRA | Infrastructure setup |
| 🔧 CONFIG | Configuration |
| 💾 DATA | Database/Schema |
| 🎯 DOMAIN | Domain service implementation |
| 🔗 INTEG | Integration/Communication |
| 🐳 DEPLOY | Containerization/Deployment |
| ✅ TEST | Testing |

---

## Phase 1: Project Foundation & Infrastructure

### TASK-001: Create Multi-Module Project Structure
**Category**: 🏗️ INFRA  
**Priority**: P0 (Critical)  
**Estimated Time**: 30 minutes  
**Dependencies**: None

**Description**: Create the parent POM and directory structure for all microservices.

**Acceptance Criteria**:
- [ ] Parent `pom.xml` created with module definitions
- [ ] All service directories created
- [ ] Spring Boot 4.0.1 and Spring Cloud 2024.0.x configured
- [ ] Common dependencies defined in parent

**Files to Create**:
```
spring-petclinic-microservices/
├── pom.xml
├── config-server/
├── discovery-server/
├── api-gateway/
├── owner-service/
├── pet-service/
├── visit-service/
└── vet-service/
```

---

### TASK-002: Implement Config Server
**Category**: 🏗️ INFRA  
**Priority**: P0 (Critical)  
**Estimated Time**: 45 minutes  
**Dependencies**: TASK-001

**Description**: Create Spring Cloud Config Server for centralized configuration.

**Acceptance Criteria**:
- [ ] Config Server application class created
- [ ] Native profile configured for local filesystem
- [ ] Server starts on port 8888
- [ ] Configuration endpoint accessible

**Files to Create**:
```
config-server/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/config/
│   └── ConfigServerApplication.java
└── src/main/resources/
    └── application.yml
```

**Key Implementation**:
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

---

### TASK-003: Create Configuration Repository
**Category**: 🔧 CONFIG  
**Priority**: P0 (Critical)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-002

**Description**: Create centralized configuration files for all services.

**Acceptance Criteria**:
- [ ] Common `application.yml` with shared settings
- [ ] Service-specific YAML files created
- [ ] Database configurations per service
- [ ] Eureka client settings configured

**Files to Create**:
```
config-repo/
├── application.yml
├── owner-service.yml
├── pet-service.yml
├── visit-service.yml
└── vet-service.yml
```

---

### TASK-004: Implement Discovery Server (Eureka)
**Category**: 🏗️ INFRA  
**Priority**: P0 (Critical)  
**Estimated Time**: 45 minutes  
**Dependencies**: TASK-001

**Description**: Create Netflix Eureka Server for service discovery.

**Acceptance Criteria**:
- [ ] Eureka Server application class created
- [ ] Standalone mode configured (no peer replication)
- [ ] Server starts on port 8761
- [ ] Dashboard accessible at http://localhost:8761

**Files to Create**:
```
discovery-server/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/discovery/
│   └── DiscoveryServerApplication.java
└── src/main/resources/
    └── application.yml
```

---

### TASK-005: Implement API Gateway
**Category**: 🏗️ INFRA  
**Priority**: P0 (Critical)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-004

**Description**: Create Spring Cloud Gateway for routing and load balancing.

**Acceptance Criteria**:
- [ ] Gateway application class created
- [ ] Routes configured for all domain services
- [ ] Eureka integration for service discovery
- [ ] CORS configuration added
- [ ] Gateway starts on port 8080

**Files to Create**:
```
api-gateway/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/gateway/
│   └── ApiGatewayApplication.java
└── src/main/resources/
    └── application.yml
```

**Route Definitions**:
| Route | Target Service |
|-------|---------------|
| `/api/owners/**` | owner-service |
| `/api/pets/**` | pet-service |
| `/api/pet-types/**` | pet-service |
| `/api/visits/**` | visit-service |
| `/api/vets/**` | vet-service |
| `/api/specialties/**` | vet-service |

---

## Phase 2: Domain Services

### TASK-006: Implement Vet Service - Project Setup
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-003, TASK-004

**Description**: Create vet-service module with Spring Boot application.

**Acceptance Criteria**:
- [ ] Module `pom.xml` with required dependencies
- [ ] Application class with required annotations
- [ ] Bootstrap configuration for Config Server
- [ ] Service starts on port 8084

**Files to Create**:
```
vet-service/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/vet/
│   └── VetServiceApplication.java
└── src/main/resources/
    ├── application.yml
    └── bootstrap.yml
```

---

### TASK-007: Implement Vet Service - Domain Model
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-006

**Description**: Create Vet and Specialty entities.

**Acceptance Criteria**:
- [ ] `Vet` entity with JPA annotations
- [ ] `Specialty` entity with JPA annotations
- [ ] `Vets` wrapper class for XML/JSON
- [ ] Base entity class for common fields

**Files to Create**:
```
vet-service/src/main/java/.../vet/
├── model/
│   ├── BaseEntity.java
│   ├── Person.java
│   ├── NamedEntity.java
│   ├── Vet.java
│   ├── Specialty.java
│   └── Vets.java
```

---

### TASK-008: Implement Vet Service - Repository Layer
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 20 minutes  
**Dependencies**: TASK-007

**Description**: Create Spring Data JPA repositories.

**Acceptance Criteria**:
- [ ] `VetRepository` with pagination support
- [ ] `SpecialtyRepository` interface
- [ ] Custom query methods as needed

**Files to Create**:
```
vet-service/src/main/java/.../vet/
└── repository/
    ├── VetRepository.java
    └── SpecialtyRepository.java
```

---

### TASK-009: Implement Vet Service - REST Controller
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 45 minutes  
**Dependencies**: TASK-008

**Description**: Create REST API endpoints for vets and specialties.

**Acceptance Criteria**:
- [ ] `GET /api/vets` - List vets (paginated)
- [ ] `GET /api/vets/{id}` - Get vet by ID
- [ ] `POST /api/vets` - Create vet
- [ ] `PUT /api/vets/{id}` - Update vet
- [ ] `GET /api/specialties` - List specialties
- [ ] Proper HTTP status codes
- [ ] Validation error handling

**Files to Create**:
```
vet-service/src/main/java/.../vet/
├── controller/
│   ├── VetController.java
│   └── SpecialtyController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

### TASK-010: Implement Vet Service - Database Schema
**Category**: 💾 DATA  
**Priority**: P1 (High)  
**Estimated Time**: 20 minutes  
**Dependencies**: TASK-007

**Description**: Create database schema and seed data.

**Acceptance Criteria**:
- [ ] `schema.sql` creates vets, specialties, vet_specialties tables
- [ ] `data.sql` inserts sample vets and specialties
- [ ] H2 console enabled for development

**Files to Create**:
```
vet-service/src/main/resources/db/
├── schema.sql
└── data.sql
```

---

### TASK-011: Implement Owner Service - Project Setup
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-003, TASK-004

**Description**: Create owner-service module with Spring Boot application.

**Acceptance Criteria**:
- [ ] Module `pom.xml` with required dependencies
- [ ] Application class with required annotations
- [ ] Service starts on port 8081

**Files to Create**:
```
owner-service/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/owner/
│   └── OwnerServiceApplication.java
└── src/main/resources/
    ├── application.yml
    └── bootstrap.yml
```

---

### TASK-012: Implement Owner Service - Domain Model
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-011

**Description**: Create Owner entity (decoupled from Pet).

**Acceptance Criteria**:
- [ ] `Owner` entity WITHOUT pet relationship
- [ ] Validation annotations (@NotBlank, @Pattern)
- [ ] Base entity classes

**Files to Create**:
```
owner-service/src/main/java/.../owner/
├── model/
│   ├── BaseEntity.java
│   ├── Person.java
│   └── Owner.java
└── dto/
    ├── OwnerRequest.java
    └── OwnerResponse.java
```

**Key Change from Monolith**:
```java
// REMOVED: @OneToMany List<Pet> pets
// Owner is now self-contained
```

---

### TASK-013: Implement Owner Service - Repository & Controller
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 45 minutes  
**Dependencies**: TASK-012

**Description**: Create repository and REST controller.

**Acceptance Criteria**:
- [ ] `OwnerRepository` with search by last name
- [ ] `GET /api/owners` - List owners (paginated)
- [ ] `GET /api/owners/{id}` - Get owner by ID
- [ ] `POST /api/owners` - Create owner
- [ ] `PUT /api/owners/{id}` - Update owner
- [ ] `DELETE /api/owners/{id}` - Delete owner
- [ ] `GET /api/owners/search?lastName=` - Search

**Files to Create**:
```
owner-service/src/main/java/.../owner/
├── repository/
│   └── OwnerRepository.java
├── service/
│   └── OwnerService.java
├── controller/
│   └── OwnerController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

### TASK-014: Implement Owner Service - Database Schema
**Category**: 💾 DATA  
**Priority**: P1 (High)  
**Estimated Time**: 15 minutes  
**Dependencies**: TASK-012

**Description**: Create database schema and seed data.

**Acceptance Criteria**:
- [ ] `schema.sql` creates owners table
- [ ] `data.sql` inserts sample owners

**Files to Create**:
```
owner-service/src/main/resources/db/
├── schema.sql
└── data.sql
```

---

### TASK-015: Implement Pet Service - Project Setup
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-003, TASK-004

**Description**: Create pet-service module with Feign client support.

**Acceptance Criteria**:
- [ ] Module `pom.xml` with Feign dependency
- [ ] Application class with `@EnableFeignClients`
- [ ] Service starts on port 8082

**Files to Create**:
```
pet-service/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/pet/
│   └── PetServiceApplication.java
└── src/main/resources/
    ├── application.yml
    └── bootstrap.yml
```

---

### TASK-016: Implement Pet Service - Domain Model
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-015

**Description**: Create Pet and PetType entities.

**Acceptance Criteria**:
- [ ] `Pet` entity with `ownerId` as INTEGER reference
- [ ] `PetType` entity
- [ ] Remove Visit relationship from Pet

**Files to Create**:
```
pet-service/src/main/java/.../pet/
├── model/
│   ├── BaseEntity.java
│   ├── NamedEntity.java
│   ├── Pet.java
│   └── PetType.java
└── dto/
    ├── PetRequest.java
    ├── PetResponse.java
    └── OwnerDto.java
```

**Key Change from Monolith**:
```java
// CHANGED: @ManyToOne Owner owner → Integer ownerId
// REMOVED: @OneToMany List<Visit> visits
```

---

### TASK-017: Implement Pet Service - Feign Client
**Category**: 🔗 INTEG  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-013, TASK-016

**Description**: Create Feign client to validate owner exists.

**Acceptance Criteria**:
- [ ] `OwnerServiceClient` interface with `@FeignClient`
- [ ] Fallback class for circuit breaker
- [ ] Integration with Pet creation/update

**Files to Create**:
```
pet-service/src/main/java/.../pet/
└── client/
    ├── OwnerServiceClient.java
    └── OwnerServiceFallback.java
```

---

### TASK-018: Implement Pet Service - Repository & Controller
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-017

**Description**: Create repositories and REST controllers.

**Acceptance Criteria**:
- [ ] `PetRepository` with findByOwnerId
- [ ] `PetTypeRepository`
- [ ] `GET /api/pets` - List pets
- [ ] `GET /api/pets/{id}` - Get pet by ID
- [ ] `POST /api/pets` - Create pet (validates ownerId)
- [ ] `PUT /api/pets/{id}` - Update pet
- [ ] `GET /api/pets/owner/{ownerId}` - Get pets by owner
- [ ] `GET /api/pet-types` - List pet types

**Files to Create**:
```
pet-service/src/main/java/.../pet/
├── repository/
│   ├── PetRepository.java
│   └── PetTypeRepository.java
├── service/
│   └── PetService.java
├── controller/
│   ├── PetController.java
│   └── PetTypeController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

### TASK-019: Implement Pet Service - Database Schema
**Category**: 💾 DATA  
**Priority**: P1 (High)  
**Estimated Time**: 15 minutes  
**Dependencies**: TASK-016

**Description**: Create database schema and seed data.

**Acceptance Criteria**:
- [ ] `schema.sql` creates pets, pet_types tables
- [ ] `data.sql` inserts sample pets and types

**Files to Create**:
```
pet-service/src/main/resources/db/
├── schema.sql
└── data.sql
```

---

### TASK-020: Implement Visit Service - Project Setup
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-003, TASK-004

**Description**: Create visit-service module with Feign client support.

**Acceptance Criteria**:
- [ ] Module `pom.xml` with Feign dependency
- [ ] Application class with `@EnableFeignClients`
- [ ] Service starts on port 8083

**Files to Create**:
```
visit-service/
├── pom.xml
├── src/main/java/org/springframework/samples/petclinic/visit/
│   └── VisitServiceApplication.java
└── src/main/resources/
    ├── application.yml
    └── bootstrap.yml
```

---

### TASK-021: Implement Visit Service - Domain Model & Feign Client
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-020

**Description**: Create Visit entity and Pet service client.

**Acceptance Criteria**:
- [ ] `Visit` entity with `petId` as INTEGER reference
- [ ] `PetServiceClient` Feign interface
- [ ] Fallback for circuit breaker

**Files to Create**:
```
visit-service/src/main/java/.../visit/
├── model/
│   ├── BaseEntity.java
│   └── Visit.java
├── dto/
│   ├── VisitRequest.java
│   ├── VisitResponse.java
│   └── PetDto.java
└── client/
    ├── PetServiceClient.java
    └── PetServiceFallback.java
```

---

### TASK-022: Implement Visit Service - Repository & Controller
**Category**: 🎯 DOMAIN  
**Priority**: P1 (High)  
**Estimated Time**: 45 minutes  
**Dependencies**: TASK-021

**Description**: Create repository and REST controller.

**Acceptance Criteria**:
- [ ] `VisitRepository` with findByPetId
- [ ] `GET /api/visits` - List visits
- [ ] `GET /api/visits/{id}` - Get visit by ID
- [ ] `POST /api/visits` - Create visit (validates petId)
- [ ] `PUT /api/visits/{id}` - Update visit
- [ ] `GET /api/visits/pet/{petId}` - Get visits by pet

**Files to Create**:
```
visit-service/src/main/java/.../visit/
├── repository/
│   └── VisitRepository.java
├── service/
│   └── VisitService.java
├── controller/
│   └── VisitController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

### TASK-023: Implement Visit Service - Database Schema
**Category**: 💾 DATA  
**Priority**: P1 (High)  
**Estimated Time**: 15 minutes  
**Dependencies**: TASK-021

**Description**: Create database schema and seed data.

**Acceptance Criteria**:
- [ ] `schema.sql` creates visits table
- [ ] `data.sql` inserts sample visits

**Files to Create**:
```
visit-service/src/main/resources/db/
├── schema.sql
└── data.sql
```

---

## Phase 3: Integration

### TASK-024: Configure Circuit Breakers
**Category**: 🔗 INTEG  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-017, TASK-021

**Description**: Add Resilience4j circuit breakers to Feign clients.

**Acceptance Criteria**:
- [ ] Resilience4j dependency added
- [ ] Circuit breaker config in application.yml
- [ ] Fallback methods implemented
- [ ] Timeout configurations set

**Configuration**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      owner-service:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
```

---

### TASK-025: Finalize API Gateway Routes
**Category**: 🔗 INTEG  
**Priority**: P1 (High)  
**Estimated Time**: 30 minutes  
**Dependencies**: TASK-005, All domain services

**Description**: Configure and test all gateway routes.

**Acceptance Criteria**:
- [ ] All routes properly configured
- [ ] Load balancing working (lb://)
- [ ] Timeout and retry policies set
- [ ] All endpoints accessible via gateway

---

### TASK-026: Add Global Exception Handling
**Category**: 🔗 INTEG  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: All domain services

**Description**: Standardize error responses across services.

**Acceptance Criteria**:
- [ ] Standard error response format
- [ ] Validation errors handled
- [ ] Not found errors handled
- [ ] Service unavailable errors handled

**Error Response Format**:
```json
{
  "timestamp": "2026-01-23T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/owners/999",
  "traceId": "abc123"
}
```

---

## Phase 4: Containerization

### TASK-027: Create Dockerfiles
**Category**: 🐳 DEPLOY  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: All services implemented

**Description**: Create Dockerfile for each service.

**Acceptance Criteria**:
- [ ] Dockerfile for config-server
- [ ] Dockerfile for discovery-server
- [ ] Dockerfile for api-gateway
- [ ] Dockerfile for owner-service
- [ ] Dockerfile for pet-service
- [ ] Dockerfile for visit-service
- [ ] Dockerfile for vet-service
- [ ] All images build successfully

**Files to Create**:
```
config-server/Dockerfile
discovery-server/Dockerfile
api-gateway/Dockerfile
owner-service/Dockerfile
pet-service/Dockerfile
visit-service/Dockerfile
vet-service/Dockerfile
```

---

### TASK-028: Create Docker Compose
**Category**: 🐳 DEPLOY  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-027

**Description**: Create Docker Compose for local development.

**Acceptance Criteria**:
- [ ] All services defined
- [ ] Proper startup dependencies
- [ ] Health checks configured
- [ ] Ports exposed correctly
- [ ] Network configured
- [ ] Entire stack starts with `docker-compose up`

**Files to Create**:
```
docker-compose.yml
docker-compose.override.yml (dev settings)
```

---

### TASK-029: Update Kubernetes Manifests
**Category**: 🐳 DEPLOY  
**Priority**: P3 (Low)  
**Estimated Time**: 2 hours  
**Dependencies**: TASK-027

**Description**: Create/update K8s deployment files.

**Acceptance Criteria**:
- [ ] Deployment YAML for each service
- [ ] Service YAML for internal communication
- [ ] ConfigMap for configuration
- [ ] Ingress for API Gateway

**Files to Create/Update**:
```
k8s/
├── namespace.yml
├── config-server/
│   ├── deployment.yml
│   └── service.yml
├── discovery-server/
│   ├── deployment.yml
│   └── service.yml
├── api-gateway/
│   ├── deployment.yml
│   ├── service.yml
│   └── ingress.yml
├── owner-service/
│   ├── deployment.yml
│   └── service.yml
├── pet-service/
│   ├── deployment.yml
│   └── service.yml
├── visit-service/
│   ├── deployment.yml
│   └── service.yml
└── vet-service/
    ├── deployment.yml
    └── service.yml
```

---

## Phase 5: Testing

### TASK-030: Vet Service Unit Tests
**Category**: ✅ TEST  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-009

**Description**: Create unit tests for vet-service.

**Acceptance Criteria**:
- [ ] Repository tests with `@DataJpaTest`
- [ ] Controller tests with `@WebMvcTest`
- [ ] Service layer tests with mocks
- [ ] 80%+ code coverage

---

### TASK-031: Owner Service Unit Tests
**Category**: ✅ TEST  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-013

**Description**: Create unit tests for owner-service.

**Acceptance Criteria**:
- [ ] Repository tests
- [ ] Controller tests
- [ ] Validation tests
- [ ] 80%+ code coverage

---

### TASK-032: Pet Service Unit Tests
**Category**: ✅ TEST  
**Priority**: P2 (Medium)  
**Estimated Time**: 1.5 hours  
**Dependencies**: TASK-018

**Description**: Create unit tests for pet-service.

**Acceptance Criteria**:
- [ ] Repository tests
- [ ] Controller tests
- [ ] Feign client tests with WireMock
- [ ] 80%+ code coverage

---

### TASK-033: Visit Service Unit Tests
**Category**: ✅ TEST  
**Priority**: P2 (Medium)  
**Estimated Time**: 1 hour  
**Dependencies**: TASK-022

**Description**: Create unit tests for visit-service.

**Acceptance Criteria**:
- [ ] Repository tests
- [ ] Controller tests
- [ ] Feign client tests with WireMock
- [ ] 80%+ code coverage

---

### TASK-034: Integration Tests
**Category**: ✅ TEST  
**Priority**: P2 (Medium)  
**Estimated Time**: 2 hours  
**Dependencies**: TASK-030 through TASK-033

**Description**: Create integration tests with Testcontainers.

**Acceptance Criteria**:
- [ ] `@SpringBootTest` for each service
- [ ] Testcontainers for database
- [ ] Cross-service call testing
- [ ] API Gateway routing tests

---

### TASK-035: End-to-End Tests
**Category**: ✅ TEST  
**Priority**: P3 (Low)  
**Estimated Time**: 2 hours  
**Dependencies**: TASK-028

**Description**: Create E2E tests using Docker Compose.

**Acceptance Criteria**:
- [ ] Docker Compose test environment
- [ ] Critical path testing
- [ ] API contract validation
- [ ] Performance baseline

---

## Task Summary

| Phase | Tasks | Total Time |
|-------|-------|------------|
| Phase 1: Infrastructure | TASK-001 to TASK-005 | ~4 hours |
| Phase 2: Domain Services | TASK-006 to TASK-023 | ~10 hours |
| Phase 3: Integration | TASK-024 to TASK-026 | ~2.5 hours |
| Phase 4: Containerization | TASK-027 to TASK-029 | ~4 hours |
| Phase 5: Testing | TASK-030 to TASK-035 | ~8.5 hours |

**Total: ~29 hours** (reduced from initial estimate with parallel work)

---

## Task Dependency Graph

```
TASK-001 (Parent POM)
    ├── TASK-002 (Config Server)
    │       └── TASK-003 (Config Repo)
    │               ├── TASK-006 → TASK-007 → TASK-008 → TASK-009 → TASK-010 (Vet Service)
    │               ├── TASK-011 → TASK-012 → TASK-013 → TASK-014 (Owner Service)
    │               ├── TASK-015 → TASK-016 → TASK-017 → TASK-018 → TASK-019 (Pet Service)
    │               └── TASK-020 → TASK-021 → TASK-022 → TASK-023 (Visit Service)
    │
    └── TASK-004 (Discovery Server)
            └── TASK-005 (API Gateway)
                    └── TASK-025 (Gateway Routes)

TASK-017, TASK-021 → TASK-024 (Circuit Breakers)
All Services → TASK-026 (Exception Handling)
All Services → TASK-027 (Dockerfiles) → TASK-028 (Docker Compose) → TASK-029 (K8s)
Each Service → TASK-030 to TASK-033 (Unit Tests) → TASK-034 (Integration) → TASK-035 (E2E)
```

---

## Execution Order (Recommended)

### Day 1: Foundation
1. TASK-001: Parent POM
2. TASK-002: Config Server
3. TASK-003: Config Repository
4. TASK-004: Discovery Server
5. TASK-005: API Gateway

### Day 2: Vet & Owner Services
6. TASK-006 to TASK-010: Complete Vet Service
7. TASK-011 to TASK-014: Complete Owner Service

### Day 3: Pet & Visit Services
8. TASK-015 to TASK-019: Complete Pet Service
9. TASK-020 to TASK-023: Complete Visit Service

### Day 4: Integration & Deployment
10. TASK-024: Circuit Breakers
11. TASK-025: Gateway Routes
12. TASK-026: Exception Handling
13. TASK-027: Dockerfiles
14. TASK-028: Docker Compose

### Day 5: Testing & Polish
15. TASK-030 to TASK-033: Unit Tests
16. TASK-034: Integration Tests
17. TASK-035: E2E Tests
18. TASK-029: Kubernetes (optional)
