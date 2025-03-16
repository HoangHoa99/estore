# E-Store

Backend implementation for electronics store.

[Project description](./docs/Altech-Java_Backend_Technical_Assessment.pdf) 

## Requirements

- Java 17
- Gradle
- Docker (optional)

## Getting Started

### Run Locally

Build and run with Gradle:

```bash
# Build project
./gradlew clean build

# Run application
./gradlew bootRun
```

### Run Tests

```bash
# Run all tests
./gradlew test
```

### Run with Docker

```bash
# Build and start container
docker-compose up --build

# Stop container
docker-compose down
```

## Access Points

- API Base URL: http://localhost:8080/api
- API Documentation: http://localhost:8080/api/swagger-ui.html
- H2 Database Console: http://localhost:8080/api/h2-console
    - JDBC URL: `jdbc:h2:mem:estore`
    - Username: `admin`
    - Password: `admin`

## Features

- Admin can create/remove products and apply discounts
- Customers can add/remove items from cart
- System calculates receipts with appropriate discounts