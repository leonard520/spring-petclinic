# PetClinic Microservices Re-Architecture Specification

## Document Information
- **Version**: 1.0
- **Date**: January 23, 2026
- **Status**: Draft

## Applied Guidelines
- **Guideline**: Custom (monolith-to-microservices pattern not available in guidelines)
- **Reference**: Industry-standard microservices patterns, Spring Cloud documentation

---

## 1. Executive Summary

This specification defines the re-architecture of the Spring PetClinic monolithic application into a microservices-based architecture. The goal is to achieve independent deployability, scalability, and maintainability while preserving all existing functionality.

## 2. Current State Analysis

### 2.1 Monolithic Architecture

```
spring-petclinic/
├── model/           # Shared base entities (BaseEntity, Person, NamedEntity)
├── owner/           # Owner, Pet, Visit, PetType entities + controllers + repositories
├── vet/             # Vet, Specialty entities + controller + repository
└── system/          # Configuration, welcome page, error handling
```

### 2.2 Entity Relationships (Current)

```
Owner (1) ──────► (*) Pet (1) ──────► (*) Visit
                      │
                      ▼
                  PetType

Vet (*) ◄────────► (*) Specialty
```

### 2.3 Current Database Schema

| Table | Description | Relationships |
|-------|-------------|---------------|
| `owners` | Pet owners | Has many pets |
| `pets` | Pets | Belongs to owner, has type, has visits |
| `pet_types` | Types of pets | Referenced by pets |
| `visits` | Vet appointments | Belongs to pet |
| `vets` | Veterinarians | Has many specialties |
| `specialties` | Vet specialties | Many-to-many with vets |
| `vet_specialties` | Join table | Links vets and specialties |

### 2.4 Current Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Welcome page |
| `/owners/new` | GET/POST | Create owner |
| `/owners/find` | GET | Search owners form |
| `/owners` | GET | List/search owners |
| `/owners/{id}` | GET | Owner details |
| `/owners/{id}/edit` | GET/POST | Edit owner |
| `/owners/{id}/pets/new` | GET/POST | Add pet |
| `/owners/{id}/pets/{id}/edit` | GET/POST | Edit pet |
| `/owners/{id}/pets/{id}/visits/new` | GET/POST | Add visit |
| `/vets.html` | GET | Vets list (HTML) |
| `/vets` | GET | Vets list (JSON) |
| `/oups` | GET | Error page demo |

---

## 3. Target State Architecture

### 3.1 Microservices Overview

| Service | Port | Responsibility | Database |
|---------|------|----------------|----------|
| `config-server` | 8888 | Centralized configuration | None |
| `discovery-server` | 8761 | Service registry (Eureka) | None |
| `api-gateway` | 8080 | Routing, load balancing | None |
| `owner-service` | 8081 | Owner management | owner_db |
| `pet-service` | 8082 | Pet and pet type management | pet_db |
| `visit-service` | 8083 | Visit management | visit_db |
| `vet-service` | 8084 | Vet and specialty management | vet_db |

### 3.2 Service Boundaries (Domain Decomposition)

#### 3.2.1 Owner Service
**Bounded Context**: Customer Management

**Entities**:
- `Owner` (id, firstName, lastName, address, city, telephone)

**API Endpoints**:
```
GET    /api/owners              - List all owners (paginated)
GET    /api/owners/{id}         - Get owner by ID
POST   /api/owners              - Create new owner
PUT    /api/owners/{id}         - Update owner
DELETE /api/owners/{id}         - Delete owner
GET    /api/owners/search       - Search by last name
```

**Events Published**:
- `OwnerCreated`
- `OwnerUpdated`
- `OwnerDeleted`

#### 3.2.2 Pet Service
**Bounded Context**: Pet Management

**Entities**:
- `Pet` (id, name, birthDate, typeId, ownerId)
- `PetType` (id, name)

**API Endpoints**:
```
GET    /api/pets                - List all pets
GET    /api/pets/{id}           - Get pet by ID
POST   /api/pets                - Create new pet
PUT    /api/pets/{id}           - Update pet
DELETE /api/pets/{id}           - Delete pet
GET    /api/pets/owner/{ownerId} - Get pets by owner
GET    /api/pet-types           - List all pet types
```

**Events Published**:
- `PetCreated`
- `PetUpdated`
- `PetDeleted`

**Dependencies**:
- Validates `ownerId` exists via Owner Service

#### 3.2.3 Visit Service
**Bounded Context**: Appointment Management

**Entities**:
- `Visit` (id, petId, date, description)

**API Endpoints**:
```
GET    /api/visits              - List all visits
GET    /api/visits/{id}         - Get visit by ID
POST   /api/visits              - Create new visit
PUT    /api/visits/{id}         - Update visit
DELETE /api/visits/{id}         - Delete visit
GET    /api/visits/pet/{petId}  - Get visits by pet
```

**Events Published**:
- `VisitCreated`
- `VisitUpdated`
- `VisitCancelled`

**Dependencies**:
- Validates `petId` exists via Pet Service

#### 3.2.4 Vet Service
**Bounded Context**: Veterinarian Management

**Entities**:
- `Vet` (id, firstName, lastName)
- `Specialty` (id, name)
- `VetSpecialty` (vetId, specialtyId) - join table

**API Endpoints**:
```
GET    /api/vets                - List all vets (paginated)
GET    /api/vets/{id}           - Get vet by ID
POST   /api/vets                - Create new vet
PUT    /api/vets/{id}           - Update vet
DELETE /api/vets/{id}           - Delete vet
GET    /api/specialties         - List all specialties
```

**Events Published**:
- `VetCreated`
- `VetUpdated`

### 3.3 Infrastructure Services

#### 3.3.1 Config Server
- Spring Cloud Config Server
- Git-backed configuration repository
- Environment-specific profiles (dev, test, prod)

#### 3.3.2 Discovery Server
- Netflix Eureka Server
- Service registration and discovery
- Health monitoring

#### 3.3.3 API Gateway
- Spring Cloud Gateway
- Route definitions for all services
- Load balancing
- Rate limiting
- CORS configuration
- Optional: Authentication/Authorization

### 3.4 Data Architecture

#### 3.4.1 Database Per Service

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   owner_db      │    │    pet_db       │    │   visit_db      │    │    vet_db       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ owners          │    │ pets            │    │ visits          │    │ vets            │
│ - id            │    │ - id            │    │ - id            │    │ - id            │
│ - first_name    │    │ - name          │    │ - pet_id (ref)  │    │ - first_name    │
│ - last_name     │    │ - birth_date    │    │ - visit_date    │    │ - last_name     │
│ - address       │    │ - type_id (FK)  │    │ - description   │    ├─────────────────┤
│ - city          │    │ - owner_id (ref)│    └─────────────────┘    │ specialties     │
│ - telephone     │    ├─────────────────┤                           │ - id            │
└─────────────────┘    │ pet_types       │                           │ - name          │
                       │ - id            │                           ├─────────────────┤
                       │ - name          │                           │ vet_specialties │
                       └─────────────────┘                           │ - vet_id        │
                                                                     │ - specialty_id  │
                                                                     └─────────────────┘
```

#### 3.4.2 Cross-Service References
- `pet.owner_id` → Reference to Owner Service (validated via API)
- `visit.pet_id` → Reference to Pet Service (validated via API)

### 3.5 Communication Patterns

#### 3.5.1 Synchronous (REST)
- Client → API Gateway → Service
- Service → Service (via Feign clients with discovery)

#### 3.5.2 Asynchronous (Optional Enhancement)
- Event publishing via Spring Cloud Stream
- Message broker: RabbitMQ or Kafka

---

## 4. User Stories

### US-001: Owner Management
**As a** clinic staff member  
**I want to** manage pet owners  
**So that** I can maintain customer records

**Acceptance Criteria**:
- [ ] Can create new owner with validation
- [ ] Can search owners by last name
- [ ] Can view owner details
- [ ] Can update owner information
- [ ] Owner data persists in owner-service database

### US-002: Pet Management
**As a** clinic staff member  
**I want to** manage pets for owners  
**So that** I can track animals in the clinic

**Acceptance Criteria**:
- [ ] Can add pet to existing owner
- [ ] Can view pet details with type
- [ ] Can update pet information
- [ ] Pet validates owner exists before creation
- [ ] Pet data persists in pet-service database

### US-003: Visit Management
**As a** clinic staff member  
**I want to** schedule and track visits  
**So that** I can manage appointments

**Acceptance Criteria**:
- [ ] Can create visit for existing pet
- [ ] Can view visit history for a pet
- [ ] Visit validates pet exists before creation
- [ ] Visit data persists in visit-service database

### US-004: Vet Management
**As a** clinic administrator  
**I want to** view veterinarians and their specialties  
**So that** I can assign appropriate vets to cases

**Acceptance Criteria**:
- [ ] Can view paginated list of vets
- [ ] Can see vet specialties
- [ ] Vet data available via REST API (JSON)
- [ ] Vet data persists in vet-service database

### US-005: Service Discovery
**As a** system  
**I want** services to auto-register and discover each other  
**So that** communication is dynamic and resilient

**Acceptance Criteria**:
- [ ] All services register with Eureka on startup
- [ ] Services can discover other services by name
- [ ] Load balancing works across service instances

### US-006: Centralized Configuration
**As a** system administrator  
**I want** configuration externalized  
**So that** I can change settings without redeployment

**Acceptance Criteria**:
- [ ] Config server serves configuration for all services
- [ ] Services fetch config on startup
- [ ] Environment-specific profiles supported

### US-007: API Gateway Routing
**As a** client application  
**I want** a single entry point  
**So that** I don't need to know individual service locations

**Acceptance Criteria**:
- [ ] Gateway routes to appropriate services
- [ ] Service discovery integration works
- [ ] CORS is properly configured

---

## 5. Non-Functional Requirements

### 5.1 Performance
- Response time: < 500ms for 95th percentile
- Throughput: Support 100 concurrent users minimum

### 5.2 Scalability
- Each service independently scalable
- Horizontal scaling via multiple instances

### 5.3 Availability
- Target: 99.9% uptime
- Circuit breakers prevent cascade failures

### 5.4 Security
- API Gateway handles authentication (future)
- Service-to-service communication secured (future)

### 5.5 Observability
- Structured logging with correlation IDs
- Health endpoints on all services
- Actuator endpoints for monitoring

---

## 6. Technical Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Service Discovery | Eureka | Mature, well-integrated with Spring Cloud |
| API Gateway | Spring Cloud Gateway | Reactive, Spring ecosystem native |
| Configuration | Spring Cloud Config | Git-backed, encryption support |
| Inter-service calls | OpenFeign | Declarative, integrates with Eureka |
| Database | H2 (dev) / PostgreSQL (prod) | Lightweight dev, robust production |
| Build Tool | Maven | Existing project uses Maven |
| Containerization | Docker | Industry standard |
| Orchestration | Docker Compose (dev) / K8s (prod) | Easy local dev, production-ready |

---

## 7. Migration Strategy

### Phase 1: Infrastructure Setup
1. Create multi-module Maven project structure
2. Implement Config Server
3. Implement Discovery Server (Eureka)
4. Implement API Gateway

### Phase 2: Domain Services
1. Extract Vet Service (least coupled)
2. Extract Owner Service
3. Extract Pet Service (depends on Owner)
4. Extract Visit Service (depends on Pet)

### Phase 3: Integration
1. Configure Feign clients for cross-service calls
2. Set up API Gateway routes
3. Integration testing

### Phase 4: Deployment
1. Create Dockerfiles for each service
2. Create Docker Compose for local development
3. Update Kubernetes manifests

---

## 8. Risk Assessment

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Data consistency across services | High | Medium | Eventual consistency, saga pattern |
| Increased operational complexity | Medium | High | Good documentation, monitoring |
| Network latency | Medium | Medium | Caching, circuit breakers |
| Service discovery failures | High | Low | Eureka clustering, fallbacks |

---

## 9. Success Criteria

- [ ] All existing functionality preserved
- [ ] Each service independently deployable
- [ ] Services communicate via REST APIs
- [ ] Service discovery operational
- [ ] Centralized configuration working
- [ ] API Gateway routing all requests
- [ ] Docker Compose can start entire stack
- [ ] All existing tests pass (adapted for microservices)

---

## 10. Out of Scope (Future Enhancements)

- Authentication/Authorization (OAuth2, JWT)
- Distributed tracing (Zipkin/Jaeger)
- Centralized logging (ELK stack)
- Event-driven architecture (Kafka/RabbitMQ)
- Frontend SPA migration
- Kubernetes production deployment
- CI/CD pipeline setup
