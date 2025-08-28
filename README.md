Application Microservices
=========================

This project is a sample application that demonstrates the use of Spring WebFlux and R2DBC in a microservice architecture with a Reactive Web API, using the Bancolombia Clean Architecture Scaffolding.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Gradle 7.6 or higher

## Microservices

The application is composed of the following microservices:

- **authentication_ms**: Microservice that manages user authentication and authorization. Run on port 8080.
- **application_ms**: Microservice that manages application registration and management. Run on port 8081.

## Commands

To run the application, use the following commands:

```bash
./gradlew bootRun
```

To run tests, use the following command:

```bash
./gradlew test
```

To build the application, use the following command:

```bash
./gradlew build
```

## TODO:

- [ ] Add more documentation
- [x] Add tests
- [x] Add swagger documentation