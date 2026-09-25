# Driver Service — RideLink

Driver & Vehicle microservice for the RideLink ride-sharing platform. Handles driver registration, vehicle attachment, availability status, location tracking, and driver discovery for the Ride Service.

## Tech Stack

- Java 21, Spring Boot 4.1.1, Maven
- H2 Database (file-based, persistent)
- Spring Data JPA / Hibernate
- Spring Security + JWT (JJWT 0.12.6)
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

## Running Locally

```bash
cd driver-service
mvn spring-boot:run
```

Service runs on **port 8082**.

## Authentication

All endpoints except driver registration and the available-drivers lookup require a valid JWT in the `Authorization: Bearer <token>` header, issued by the Account Service. The token must carry a `DRIVER` role for vehicle and availability/location endpoints.

## API Documentation

Interactive Swagger UI (service must be running):