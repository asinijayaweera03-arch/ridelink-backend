\# RideLink – Ride Management Service



\## Overview



The Ride Management Service is responsible for managing the ride lifecycle in the RideLink backend microservices platform.



The service supports:



\- Ride request creation

\- Available driver assignment

\- Ride retrieval and filtering

\- Ride acceptance

\- Ride start

\- Ride completion

\- Ride cancellation

\- Payment processing during ride completion

\- Payment retry handling

\- JWT-based authentication and role-based authorization

\- Structured error handling

\- OpenAPI / Swagger documentation



This service is implemented using Java 21, Spring Boot and an H2 database.



\---



\## Service Information



| Item | Value |

|---|---|

| Service | Ride Management Service |

| Port | `8083` |

| Base URL | `http://localhost:8083` |

| API Base Path | `/api/rides` |

| Database | H2 |

| Java Version | Java 21 |

| Authentication | JWT Bearer Token |

| API Documentation | Swagger / OpenAPI |



\---



\## Responsibilities



The Ride Management Service manages the main lifecycle of a ride.



The supported lifecycle is:



```text

REQUESTED

&#x20;   |

&#x20;   v

ASSIGNED

&#x20;   |

&#x20;   v

ACCEPTED

&#x20;   |

&#x20;   v

IN\_PROGRESS

&#x20;   |

&#x20;   v

COMPLETED

```



Cancellation is allowed from supported non-terminal states.



```text

REQUESTED ----\\

ASSIGNED ------> CANCELLED

ACCEPTED -----/

```



`COMPLETED` and `CANCELLED` are terminal states.



Invalid lifecycle transitions return an HTTP `409 Conflict` response.



\---



\## Prerequisites



Before running the Ride Management Service, install:



\- Java 21

\- Git

\- Maven is not required separately because the project contains the Maven Wrapper.



Verify Java:



```bash

java -version

```



The output should show Java 21.



\---



\## Configuration



The main configuration file is:



```text

src/main/resources/application.properties

```



The Ride Management Service runs on:



```text

http://localhost:8083

```



The service uses a persistent H2 database:



```text

jdbc:h2:file:./data/ridedb

```



\### External Services



The Ride Management Service communicates with:



```text

Driver \& Vehicle Service

http://localhost:8082

```



and:



```text

Fare \& Payment Service

http://localhost:8084

```



The URLs are configured using the following properties:



```properties

ridelink.driver-service.base-url=http://localhost:8082

ridelink.payment-service.base-url=http://localhost:8084

```



HTTP connection and read timeouts are also configured.



\---



\## Environment Variables



\### JWT Secret



The RideLink services must use a compatible JWT secret.



Environment variable:



```text

JWT\_SECRET

```



\### Payment Internal API Key



Communication with the internal payment creation API uses:



```text

PAYMENT\_INTERNAL\_API\_KEY

```



Do not commit production secrets to the repository.



Development fallback values are provided only for local development.



\---



\## Running the Service



Open Command Prompt inside the `ride-service` directory.



On Windows:



```cmd

mvnw.cmd spring-boot:run

```



The service starts on:



```text

http://localhost:8083

```



\---



\## Running Tests



Run all Ride Management Service tests using:



```cmd

mvnw.cmd test

```



The current test suite covers controller security, exception handling, ride lifecycle rules, persistence and service-level behaviour.



\---



\## Swagger / OpenAPI



Start the Ride Management Service and open:



```text

http://localhost:8083/swagger-ui/index.html

```



OpenAPI JSON is available at:



```text

http://localhost:8083/v3/api-docs

```



Swagger documents:



\- Ride endpoints

\- Request schemas

\- Request examples

\- Ride lifecycle operations

\- JWT Bearer authentication



\### Using JWT in Swagger



Click:



```text

Authorize

```



Enter a valid JWT Bearer token.



The RideLink JWT contains a role such as:



```text

PASSENGER

```



or:



```text

DRIVER

```



The API applies role-based authorization using Spring Security.



\---



\## REST API Endpoints



\### Create Ride



```http

POST /api/rides

```



Required role:



```text

PASSENGER

```



Example request:



```json

{

&#x20; "riderId": 101,

&#x20; "pickupLocation": "SLIIT Malabe Campus",

&#x20; "destination": "Battaramulla",

&#x20; "pickupLatitude": 6.9147,

&#x20; "pickupLongitude": 79.9729,

&#x20; "destinationLatitude": 6.9022,

&#x20; "destinationLongitude": 79.9182,

&#x20; "distanceKm": 8.5

}

```



The service requests available driver information from the Driver \& Vehicle Service.



If an available driver is found, the ride is assigned.



If no driver is available, the service returns:



```text

409 Conflict

NO\_AVAILABLE\_DRIVER

```



If the Driver \& Vehicle Service cannot be reached, the service returns:



```text

503 Service Unavailable

DRIVER\_SERVICE\_UNAVAILABLE

```



\---



\### Get All / Filter Rides



```http

GET /api/rides

```



Requires authentication.



Optional query parameters:



```text

passengerId

driverId

status

```



Examples:



```text

GET /api/rides?passengerId=101

GET /api/rides?driverId=5

GET /api/rides?status=COMPLETED

```



\---



\### Get Ride by ID



```http

GET /api/rides/{id}

```



Requires authentication.



If the ride does not exist:



```text

404 Not Found

RIDE\_NOT\_FOUND

```



\---



\### Accept Ride



```http

PATCH /api/rides/{id}/accept

```



Required role:



```text

DRIVER

```



Valid transition:



```text

ASSIGNED -> ACCEPTED

```



\---



\### Start Ride



```http

PATCH /api/rides/{id}/start

```



Required role:



```text

DRIVER

```



Valid transition:



```text

ACCEPTED -> IN\_PROGRESS

```



\---



\### Complete Ride



```http

PATCH /api/rides/{id}/complete

```



Required role:



```text

DRIVER

```



Example request:



```json

{

&#x20; "peakHour": false,

&#x20; "paymentMethod": "CASH"

}

```



Valid transition:



```text

IN\_PROGRESS -> COMPLETED

```



When a ride is completed, the Ride Management Service attempts to record payment using the Fare \& Payment Service.



Ride distance is obtained from the stored ride and trip duration is calculated using ride lifecycle timestamps.



If the Payment Service is unavailable, payment remains pending and the API reports the downstream service failure.



\---



\### Retry Payment



```http

PATCH /api/rides/{id}/payment/retry

```



Required role:



```text

DRIVER

```



Example request:



```json

{

&#x20; "peakHour": false,

&#x20; "paymentMethod": "CASH"

}

```



This endpoint retries payment processing for a completed ride when payment requires another processing attempt.



\---



\### Cancel Ride



```http

PATCH /api/rides/{id}/cancel

```



Required role:



```text

PASSENGER

```



Example request:



```json

{

&#x20; "reason": "Passenger plans changed"

}

```



The cancellation reason is optional and has a maximum length of 500 characters.



Cancellation is permitted only from valid lifecycle states.



\---



\## Security and Roles



The Ride Management Service uses stateless JWT authentication.



The main application roles used by the Ride API are:



| Operation | Required Access |

|---|---|

| Create ride | PASSENGER |

| Retrieve rides | Authenticated user |

| Retrieve ride by ID | Authenticated user |

| Accept ride | DRIVER |

| Start ride | DRIVER |

| Complete ride | DRIVER |

| Retry payment | DRIVER |

| Cancel ride | PASSENGER |



Requests to protected endpoints without valid authentication are rejected by Spring Security.



\---



\## Error Response Format



The service uses centralized exception handling.



Errors follow a structured format similar to:



```json

{

&#x20; "code": "RIDE\_NOT\_FOUND",

&#x20; "message": "Ride not found",

&#x20; "timestamp": "2026-09-26T09:00:00",

&#x20; "details": {}

}

```



Depending on the error, `details` may contain additional information.



\---



\## Important Error Codes



| HTTP Status | Error Code | Meaning |

|---|---|---|

| 400 | `VALIDATION\_ERROR` | Request validation failed |

| 400 | `INVALID\_RIDE\_STATE` | Ride operation is not valid for the current state |

| 404 | `RIDE\_NOT\_FOUND` | Requested ride does not exist |

| 409 | `INVALID\_RIDE\_TRANSITION` | Requested lifecycle transition is not allowed |

| 409 | `NO\_AVAILABLE\_DRIVER` | No available driver could be found |

| 503 | `DRIVER\_SERVICE\_UNAVAILABLE` | Driver Service could not be reached |

| 503 | `PAYMENT\_SERVICE\_UNAVAILABLE` | Payment Service could not be reached |



For invalid transitions, the response can include the current state and allowed next states.



\---



\## Inter-Service Communication



\### Driver \& Vehicle Service



The Ride Management Service uses the Driver \& Vehicle Service to obtain an available driver during ride creation.



Expected downstream service:



```text

http://localhost:8082

```



The current Driver Service public contract supports available-driver lookup.



A dedicated internal Ride-to-Driver reservation/release contract is not currently available. Therefore, the Ride Management Service does not impersonate a driver or manually force the Driver Service into `ON\_TRIP`.



A future internal service contract could provide explicit reserve and release operations secured for service-to-service communication.



\---



\## Fare \& Payment Service



The Ride Management Service communicates with the Fare \& Payment Service when completing a ride.



Expected downstream service:



```text

http://localhost:8084

```



The Ride Service supplies ride information required for simulated payment processing, including:



\- Ride ID

\- Passenger ID

\- Distance

\- Trip duration

\- Peak-hour indicator

\- Payment method



The Ride entity stores payment-related information including payment status, payment identifier and fare amount.



\---



\## Payment States



Ride payment processing uses the following local states:



```text

NOT\_APPLICABLE

PENDING

RECORDED

FAILED

```



Payment is not applicable before completion.



During payment processing it may become `PENDING`.



Successful processing results in `RECORDED`.



A payment reported as failed is stored as `FAILED`.



\---



\## Downstream Failure Handling



The service converts known downstream communication failures into structured API errors.



Driver Service unavailable:



```text

503 DRIVER\_SERVICE\_UNAVAILABLE

```



Payment Service unavailable:



```text

503 PAYMENT\_SERVICE\_UNAVAILABLE

```



Payment state is saved before downstream payment processing so that a failed downstream call does not lose the local payment workflow state.



\---



\## Known Integration Considerations



\### Driver Reservation / Release



The current Driver \& Vehicle Service contract does not provide a suitable internal endpoint for the Ride Management Service to reserve a driver as `ON\_TRIP` and later release that driver.



The Ride Service therefore uses the available-driver lookup contract without bypassing Driver Service authorization rules.



\### Payment Retry



If a payment request reaches the Payment Service but the network response is lost, the Ride Service may retain a pending local payment state while the Payment Service has already processed the request.



A production distributed system would normally address this using an idempotency mechanism, event-driven processing, or another reliable distributed workflow strategy.



\---



\## H2 Database Console



The H2 console is enabled for local development.



Open:



```text

http://localhost:8083/h2-console

```



JDBC URL:



```text

jdbc:h2:file:./data/ridedb

```



Username:



```text

sa

```



The configured password is empty for the local development database.



\---



\## Testing Coverage



The automated test suite includes tests for:



\- Ride lifecycle transitions

\- Ride entity behaviour

\- Repository operations

\- Ride Service business logic

\- Driver assignment scenarios

\- No available driver handling

\- Downstream Driver Service failure

\- Ride completion

\- Payment processing

\- Payment retry

\- Payment Service failure

\- Validation and global exception handling

\- JWT / role-based controller security



Run:



```cmd

mvnw.cmd test

```



before committing changes.



\---



\## Recommended Local Startup Order



For integrated testing, start the required RideLink backend services before testing the complete ride workflow.



A practical startup sequence is:



```text

1\. Account Service

2\. Driver \& Vehicle Service

3\. Fare \& Payment Service

4\. Ride Management Service

```



Confirm that the services required by the test scenario are running before sending Ride Service requests.



\---



\## Postman Testing



A Ride Management Service Postman collection is provided for reproducible API testing.



The collection covers scenarios such as:



```text

Happy-path ride lifecycle

No available driver

Driver Service unavailable

Payment Service unavailable

Payment retry

Invalid ride transition

Unauthorized / forbidden access

```



JWT tokens used for protected endpoints should be supplied through Postman variables rather than hard-coded into requests.



\---



\## Technology Stack



\- Java 21

\- Spring Boot 4.1.1

\- Spring Web

\- Spring Data JPA

\- Spring Security

\- JWT

\- H2 Database

\- SpringDoc OpenAPI / Swagger UI

\- Maven Wrapper

\- JUnit

\- Mockito



\---



\## Ride Management Service Scope



This directory contains the Ride Management Service implementation.



Changes for this service should remain within the `ride-service` scope. Changes to other RideLink microservices should be coordinated with the responsible team member through the shared Git workflow.

