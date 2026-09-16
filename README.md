# RideLink — Backend Microservices

IT3130 Application Development · Group Assignment · 4 independently executable Spring Boot services

## Services and ownership

| # | Service | Folder | Port | Owner | Owns |
|---|---------|--------|------|-------|------|
| 1 | Account Service | `account-service` | 8081 | Asini | Accounts, auth, roles |
| 2 | Driver & Vehicle Service | `driver-service` | 8082 | TBD | Driver/vehicle profiles, availability |
| 3 | Ride Management Service | `ride-service` | 8083 | TBD | Ride lifecycle |
| 4 | Fare & Payment Service | `payment-service` | 8084 | TBD | Fare calc, simulated payment |

## Rule
Each service owns its own database. No service reads or writes another service's tables directly —
only via that service's REST API.

## Branching
`main` = always demo-ready, protected, merges only from `develop`.
`develop` = shared integration branch.
`feature/<name>` = individual work, branched off `develop`, merged back via reviewed PR.
