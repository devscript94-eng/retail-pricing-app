# Engineering Paper 

## Hexagonal Pricing API


Spring Boot microservice that exposes a REST endpoint to retrieve the applicable price for a product and brand at a given date.

The service is implemented using a **hexagonal architecture (ports and adapters)** to keep business logic isolated from technical concerns such as REST, JPA, and H2.

![Hexagonal architecture](assets/hexagonal-view.png)

---

## 1. Functional goal

The API must:

- accept:
    - application date
    - product identifier
    - brand identifier
- return:
    - product identifier
    - brand identifier
    - price list to apply
    - validity dates
    - final price

The pricing rule is:

> when several prices are valid for the same product, brand, and date, the one with the **highest priority** must be applied.

The database is initialized with the sample data provided in the exercise, using an **in-memory H2 database**.

---

## 2. Architectural style

This project follows **Hexagonal Architecture**, also known as **Ports and Adapters**.

The main idea is:

- The **core** contains business concepts and use cases
- External technologies are isolated in **adapters**
- Dependencies point **inward**, toward the core

This gives several benefits:

- Business logic is independent from frameworks
- Domain behavior is easier to test
- Infrastructure can be replaced without changing core logic
- Clearer separation of responsibilities

---

## 3. Package structure

```text
src/main/java/com/retail/pricing
│
├── domain
│   ├── model
│   │   ├── Price.java
│   │   └── PriceRequest.java
│   ├── repository
│   │   └── PriceRepository.java
│   └── exception
│       └── PriceNotFoundException.java
│
├── application
│   ├── port
│   │   └── GetPriceUseCase.java
│   └── service
│       └── PriceService.java
│
├── infrastructure
│   ├── adapters
│   │   ├── in
│   │   │   └── web
│   │   │       ├── controller
│   │   │       │   └── PriceController.java
│   │   │       ├── dto
│   │   │       │   └── PriceResponseDTO.java
│   │   │       ├── handler
│   │   │       │   └── RestExceptionHandler.java
│   │   │       └── mapper
│   │   │           └── PriceWebMapper.java
│   │   └── out
│   │       └── persistence
│   │           ├── adapter
│   │           │   └── PricePersistenceAdapter.java
│   │           ├── entity
│   │           │   └── PriceEntity.java
│   │           ├── mapper
│   │           │   └── PricePersistenceMapper.java
│   │           └── repository
│   │               └── JpaPriceRepository.java
│   └── config
│
└── PricingApplication.java
```

---

## 4. Layer responsibilities

### 4.1 Domain Layer

The domain package contains the business core.

**Price** :

Represents a price in the system.

It contains only business data:

- brand
- validity dates
- price list
- product
- priority
- amount
- currency

It is intentionally framework-free:

- no JPA annotations
- no Spring annotations
- no persistence concerns 


**PriceRequest** :

Represents the business search criteria:
- application date
- product id
- brand id

This avoids passing loose parameters around and makes the use case contract more explicit.

**PriceRepository** :

This is the outbound port.

Despite its name, it is not the Spring Data repository.
It is the interface the core uses to express what it needs from the outside world:

```“I need to find the applicable price for this request.”```

This is a key hexagonal concept:
- The core depends on an abstraction
- Infrastructure provides the implementation

**PriceNotFoundException** :

Domain-specific exception thrown when no price matches the request.

This keeps error semantics aligned with the business language.

### 4.2 Application Layer

The application package orchestrates the use case.

**GetPriceUseCase**

This is the inbound port.

It defines what the system offers to external callers :

```Price execute(PriceRequest request);```

In hexagonal architecture, inbound adapters do not call services directly by implementation type; they call a use case contract.

**PriceService**

This is the use case implementation.

It orchestrates the pricing query:

- Receives a PriceRequest
- Asks the outbound port (PriceRepository) for the applicable price
- Throws a domain exception if none exists
- Returns the selected domain object

The application layer contains orchestration logic, not technical concerns.

### 4.3 Application Layer

The infrastructure package contains technical implementations.

It is split into inbound adapters and outbound adapters.

---

## 5. Inbound Adapter Design

Inbound adapters are the entry points into the application.

In this project, the inbound adapter is a REST API.

**PriceController**

Receives HTTP requests and maps them to the use case.

Responsibilities:
- Parse query parameters
- Create a PriceRequest
- Call GetPriceUseCase
- Map the domain response to an API DTO

It intentionally does not contain business rules.

**PriceResponseDTO**

Represents the REST response payload.

It is separate from the domain model because:
- API contracts may evolve differently from the domain
- External naming can differ from internal naming
- It avoids exposing domain structures directly

**PriceWebMapper**

MapStruct mapper converting:

Price → PriceResponseDTO

MapStruct was chosen to:
- Reduce boilerplate
- Keep mappings explicit
- Generate type-safe code at compile time

**RestExceptionHandler**

Centralized exception mapping for REST errors.

Responsibilities:
- Translate domain/application exceptions into HTTP responses
- Keep controllers simple
- Ensure consistent API error behavior

---

## 6. Outbound adapter design

Outbound adapters implement technical access to external systems.

In this project, the external system is the database.

**JpaPriceRepository**

This is the Spring Data JPA repository.

It is a technical repository, not the domain port.

Its responsibility is:
- Executing the database query
- Using JPA/Spring Data naming or JPQL
- Returning persistence entities

The selected query strategy is:

```ask the database to directly return the highest-priority valid row.```

This is an important design decision.

Instead of:
- Loading all valid rows
- Sorting/selecting in Java

the system does:
- Filtering in SQL
- Sorting in SQL
- Returning only the first result

This is better because:
- Less data is transferred
- Less memory is used
- Less mapping is needed
- The database is optimized for this kind of work

**PriceEntity**

JPA representation of the prices table.

It exists only for persistence concerns:

- Table mapping
- Column mapping
- JPA lifecycle

This class is separated from the domain model so the domain remains persistence-agnostic.

**PricePersistenceMapper**

MapStruct mapper converting:

PriceEntity → Price

This keeps the adapter responsible for translation between technical and domain models.

**PricePersistenceAdapter**

Implements the domain PriceRepository port using JPA.

Responsibilities:

- Receive the domain PriceRequest
- Call JpaPriceRepository
- Map PriceEntity to Price
- Return an Optional<Price>

This class is the actual outbound adapter in hexagonal terms.

---

## 7. Why PriceRepository is the outbound port

A common point of confusion is this:
- PriceRepository in domain.repository
- JpaPriceRepository in infrastructure

They are not the same thing.

**PriceRepository**

This is the port.
It belongs to the core and expresses a business need.

**JpaPriceRepository**

This is a technical helper.
It belongs to infrastructure and speaks JPA.

This separation is intentional.

The core should not depend on:
- SQL
- H2
- JPA
- Spring Data

It should depend only on what it needs in business terms.

---

## 8. API design

Endpoint

```GET /api/v1/prices```

**Query parameters**
- applicationDate
- productId
- brandId

**Example request**

```GET /api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1```

**Example response**

```
{
    "productId": 35455,
    "brandId": 1,
    "priceList": 2,
    "startDate": "2020-06-14T15:00:00",
    "endDate": "2020-06-14T18:30:00",
    "finalPrice": 25.45,
    "currency": "EUR"
}
```

**Error response**

If no price exists for the request, the API returns 404 Not Found.

---

## 9. Request flow

![Sequence Diagram](assets/request-flow-sequence-diagram.png)

The runtime flow is:

- client calls REST endpoint
- PriceController receives the request
- controller creates PriceRequest
- controller calls GetPriceUseCase
- PriceService orchestrates the use case
- PriceService calls PriceRepository port
- PricePersistenceAdapter implements the port
- adapter calls JpaPriceRepository
- JPA queries H2
- entity is mapped to domain
- domain model is mapped to response DTO
- response is returned to the client

This flow demonstrates the hexagonal direction of dependencies:
- adapters depend on core contracts
- core does not depend on adapters

---

## 10. Testing strategy

The project uses a layered testing strategy.

**Unit tests**

Focused on application behavior:
- PriceServiceTest

These tests validate:
- correct result returned
- exception thrown when not found

**Repository integration tests**

Focused on JPA query correctness with H2:
- JpaPriceRepositoryIntegrationTest

These validate:
- date range filtering
- product filtering
- brand filtering
- highest priority selection

**Full API integration tests**

Focused on end-to-end behavior:
- PriceApiIntegrationTest

These validate the 5 required scenarios from the exercise:
- 2020-06-14 10:00
- 2020-06-14 16:00
- 2020-06-14 21:00
- 2020-06-15 10:00
- 2020-06-16 21:00

This test mix gives high confidence while keeping tests meaningful.

---

## 11. Running the API

### Prerequisites

Before running the application, make sure the following tools are installed:
- Java 25
- Maven 3.9+ (or use the included Maven Wrapper if available)

**Start the application**

From the project root, run:

```mvn spring-boot:run```

If the project includes the Maven Wrapper, you can also use:

```./mvnw spring-boot:run```

The application starts on :

```http://localhost:8081```

![Postman Request](assets/postman-request.png)

---

## 12. Main design decisions summary

1. **Hexagonal architecture** : Chosen to isolate business logic from technical concerns.

2. **Domain model separated from persistence model** : Prevents JPA annotations and persistence concerns from leaking into the core.

3. **Inbound and outbound ports** : Used to make dependencies explicit and invert control.

4. **Repository query returns a single result** : The requirement expects one applicable price, so the repository returns one result directly.

5. **Selection done in the database** : Chosen for efficiency and scalability.

6. **MapStruct for mapping** : Chosen to reduce boilerplate and keep mapping responsibilities explicit.

7. **SQL initialization with H2** : Chosen for deterministic setup and alignment with the exercise.

8. **Thin controller** : Chosen to keep REST concerns separate from business rules.

9. **Domain-specific exception** : Chosen to express business failure clearly.

10. **Test pyramid approach** : Chosen to combine fast unit tests with realistic integration coverage.

---

## 13. Trade-offs

No architecture is free of trade-offs.

Why this design is good
- Clean separation of concerns
- Easy to understand
- Easy to test
- Easy to extend
- Good performance for the main query
- What could evolve later

If the service grew, possible next steps could include:
- Adding input validation objects for the REST layer
- Introducing more use cases
- Separating query and command models further
- Adding API documentation with OpenAPI
- Introducing JaCoCo for coverage reporting
- Adding structured logging and observability

For the current scope, the design remains intentionally simple.

---

## 14. Future Improvements

The current implementation is intentionally simple and focused on the exercise requirements.  
If this microservice evolved toward a production-grade service, the following improvements would be strong next steps.

### 14.1 API versioning

At the moment, the API is small and stable, but in a real system the contract can evolve over time.

API versioning would help:
- preserve backward compatibility
- introduce new response fields safely
- evolve validation rules without breaking existing consumers
- support gradual migration of client applications

A practical approach would be:
- keep the current endpoint under `/api/v1/prices`
- introduce future changes under `/api/v2/prices`
- document versions clearly in OpenAPI/Swagger
- define a deprecation strategy for old versions

This is especially useful when multiple client applications consume the API and cannot all migrate at the same pace.


### 14.2 Resilience

This exercise uses an in-memory database and has no remote dependencies, so resilience concerns are limited.  
In a real production environment, resilience becomes important as soon as the service depends on external infrastructure.

Examples of resilience improvements:
- timeouts on external calls
- retries with backoff
- circuit breakers
- bulkheads
- graceful degradation
- fallback strategies

In this pricing context, resilience would be relevant if the service later integrates with:
- an external product catalog
- a promotion service
- a tax calculation service
- a currency exchange service
- a remote database or cache

Typical implementation choices:
- Resilience4j for retry, circuit breaker, rate limiting, and bulkhead
- clearly defined timeout policies
- fallback behavior for non-critical dependencies
- error classification between transient and permanent failures

The goal would be to make the service robust under partial failures and avoid cascading outages.

### 14.3 Observability

For a real microservice, observability is essential to understand behavior in production.

#### Logging

Structured logging would improve traceability and debugging.

Useful logging improvements:
- correlation/request id
- input request summary
- selected price list and final price
- not-found cases
- unexpected technical failures
- execution time of key operations

Logs should be:
- structured
- consistent
- meaningful
- free of sensitive data

A good approach would be JSON logs with fields such as:
- timestamp
- log level
- service name
- request id
- product id
- brand id
- application date
- selected price list

#### Metrics

Metrics would allow monitoring service health and performance.

Useful metrics for this API:
- total number of pricing requests
- request latency
- number of successful responses
- number of not-found responses
- number of technical failures
- database query duration
- distribution of selected price lists

Prometheus could be integrated through Spring Boot Actuator and Micrometer.

Typical setup:
- expose `/actuator/prometheus`
- scrape with Prometheus
- visualize in Grafana

This would make it possible to answer operational questions such as:
- how many pricing requests are received per minute
- what is the average response time
- how often no applicable price is found
- whether latency is increasing over time

### 14.4 Database versioning

At the moment, schema initialization is handled with schema.sql and data.sql, which is perfectly suitable for the exercise.
For a real project, database versioning should be managed with a migration tool.

The two most common options are:

- Flyway
- Liquibase

Why this matters:

- track schema evolution over time
- make DB changes reproducible
- avoid manual and error-prone updates
- support deployment across multiple environments
- provide a clear history of database changes
- Flyway

Flyway is usually simpler and very effective for straightforward SQL migration workflows.

Typical benefits:
- versioned migration scripts
- easy integration with Spring Boot
- simple mental model
- good fit for most microservices

Example naming convention:
- V1__create_prices_table.sql
- V2__insert_initial_prices.sql
- V3__add_index_on_product_brand_dates.sql

Liquibase

Liquibase can be a good choice when:

more complex database evolution is expected
multiple database engines must be supported
teams prefer declarative changelogs in XML/YAML/JSON

For this project, Flyway would probably be the most natural next step because the schema is small and SQL-driven.

### 14.5 Architecture Documentation with arc42

As the microservice grows, a valuable next step would be to formalize its architecture using the **arc42** documentation standard.

arc42 is a widely used template for documenting software architecture in a structured and maintainable way.  
It helps describe not only the static design, but also the key decisions, constraints, quality goals, and evolution path of the system.

Using arc42 for this microservice would bring several benefits:
- provide a shared architectural reference for developers and reviewers
- make design decisions explicit and traceable
- improve onboarding for new team members
- document technical risks and future evolution paths
- align code structure with a recognized architecture documentation standard

### Possible production evolution

If this microservice moved from coding exercise to production, a realistic evolution path would be:

- Introduce Flyway for database versioning
- Add structured logs and Prometheus metrics
- Add resilience mechanisms when external dependencies appear
- Keep API versioning policy from the beginning
- Introduce CQRS only if the domain expands into both reads and writes

This progression keeps the service simple today while preparing it for real operational requirements tomorrow.

---

## 15. Conclusion

This project was designed to solve a simple pricing use case with production-grade structure.

The key objective was not only to make the endpoint work, but to make the design:
- clean
- maintainable
- testable
- aligned with hexagonal principles

The final result is a service where:
- the business use case is clearly expressed
- the database is hidden behind an outbound port
- the REST API is only an adapter
- infrastructure can evolve without changing the core

That makes the codebase easier to reason about today and safer to evolve tomorrow.

