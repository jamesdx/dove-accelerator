# Docker Environment Setup Guide

This guide explains how to set up the development environment using Docker.

## Prerequisites

- Docker Engine 24.0.0+
- Docker Compose v2.20.0+

## MariaDB Setup

The project uses MariaDB 10.11 LTS (Enterprise Grade) as the database. This version is chosen for its stability and long-term support.

### Configuration Files

- `docker-compose.yml`: Contains the MariaDB service configuration
- `.env`: Contains environment variables
- `mariadb/init/init.sql`: Database initialization script

### Environment Variables

Default values in `.env` file:
- MARIADB_ROOT_PASSWORD=root
- MARIADB_DATABASE=dove_accelerator
- MARIADB_USER=dove_user
- MARIADB_PASSWORD=dove_password

You can modify these values according to your needs.

### Starting the Database

1. Navigate to the docker directory:
   ```bash
   cd docker
   ```

2. Start the MariaDB container:
   ```bash
   docker-compose up -d
   ```

3. Verify the container is running:
   ```bash
   docker-compose ps
   ```

### Stopping the Database

To stop the database:
```bash
docker-compose down
```

To stop and remove all data (including volumes):
```bash
docker-compose down -v
```

### Connecting to the Database

- Host: localhost
- Port: 3306
- Database: dove_accelerator
- Username: dove_user
- Password: dove_password

Root access:
- Username: root
- Password: root

### Data Persistence

Database data is persisted in a Docker volume named `mariadb_data`. This ensures your data survives container restarts.

### Database Initialization

The `init.sql` script in `mariadb/init/` will automatically run when the container first starts. You can modify this script to add initial schema or data.

## Troubleshooting

1. If you can't connect to the database, check if the container is running:
   ```bash
   docker-compose ps
   ```

2. View container logs:
   ```bash
   docker-compose logs mariadb
   ```

3. If you need to reset everything:
   ```bash
   docker-compose down -v
   docker-compose up -d
   ``` 