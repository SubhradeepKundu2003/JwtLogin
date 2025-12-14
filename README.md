# Environment Variables Setup

This project uses **OS environment variables** for sensitive configuration such as  
JWT secrets and database credentials.

❗ **Do NOT hardcode secrets in code or commit them to Git.**

---

## Required Environment Variables

| Variable Name   | Description                         | Example |
|-----------------|-------------------------------------|---------|
| JWT_SECRET      | Secret key for signing JWT tokens   | superSecureJwtKeyForSpringBoot321Java21Postgres |
| JWT_EXPIRATION  | JWT expiration time (ms)             | 3600000 |
| DB_URL          | PostgreSQL JDBC URL                  | jdbc:postgresql://localhost:5432/login |
| DB_USERNAME     | PostgreSQL username                  | postgres |
| DB_PASSWORD     | PostgreSQL password                  | root |

---

## Windows (PowerShell)

Run the following commands **once**:

```bash

setx JWT_SECRET "superSecureJwtKeyForSpringBoot321Java21Postgres"
setx JWT_EXPIRATION "3600000"

setx DB_URL "jdbc:postgresql://localhost:5432/login"
setx DB_USERNAME "postgres"
setx DB_PASSWORD "root"
```

## MacOS/Linux (Terminal)

Run the following commands **once**:

```bash 

export JWT_SECRET="superSecureJwtKeyForSpringBoot321Java21Postgres"
export JWT_EXPIRATION="3600000"

export DB_URL="jdbc:postgresql://localhost:5432/login"
export DB_USERNAME="postgres"
export DB_PASSWORD="root"
```

