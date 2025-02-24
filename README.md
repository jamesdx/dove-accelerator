# Dove Accelerator

Dove Accelerator is an innovative AI-driven software development initiative aimed at creating a 24/7 operational AI Agent team to efficiently deliver enterprise-level applications.

## Features

- AI-driven development assistance
- Microservices architecture
- MariaDB integration
- Flyway database migration
- Spring Boot backend
- Multi-terminal support
- Internationalization

## Prerequisites

- Java 17
- MariaDB
- Maven
- Docker (optional)

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/dove-accelerator.git
   cd dove-accelerator
   ```

2. Configure the application:
   - Copy `src/main/resources/application-example.properties` to `src/main/resources/application.properties`
   - Update the database credentials and OpenAI API key in `application.properties`

3. Build the project:
   ```bash
   ./mvnw clean install
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   Or using Docker:
   ```bash
   docker-compose up -d
   ```

## Database Migration

The project uses Flyway for database migration. Migration scripts are located in `src/main/resources/db/migration`.

## Development

### VS Code Setup

1. Install required extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack

2. Open the project in VS Code
3. Use the Spring Boot Dashboard or Debug view to run/debug the application

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

[Your License Here] 