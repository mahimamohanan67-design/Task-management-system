# Morrow — Task Studio

Morrow is a responsive React + Vite task workspace with a Spring Boot 3 REST API, PostgreSQL persistence, Flyway schema migrations, BCrypt passwords, and stateless JWT authentication.

## Requirements

- Java 17+
- Maven 3.9+
- Node.js 20+
- A local PostgreSQL 14+ installation (Docker is optional)

## Start PostgreSQL without Docker

On Windows, copy `.env.example` to `.env`, set `DATABASE_PASSWORD` and `JWT_SECRET`, then run this from the repository root in PowerShell:

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\start-local.ps1
```

The script uses the installed PostgreSQL command-line tools, initializes a project-local development cluster if needed, starts it on the port from `DATABASE_URL` (5434 by default), creates `morrow_db`, loads the variables from `.env`, and starts Spring Boot. It also finds Maven on `PATH` or in the standard Maven wrapper cache. Keep `.env` private; it is ignored by Git. To use an existing PostgreSQL server instead, set `DATABASE_URL`, `DATABASE_USER`, and `DATABASE_PASSWORD` in your environment and run Maven as shown below. `docker-compose.yml` remains an optional alternative.

## Configure and run the API

Set environment variables before starting the API. Spring Boot reads the four required values `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`, and `JWT_SECRET`; defaults are provided for local development only. Use a randomly generated JWT secret with at least 32 bytes. Never commit `.env` or use development secrets in production.

PowerShell example:

```powershell
$env:DATABASE_URL = 'jdbc:postgresql://localhost:5434/morrow_db'
$env:DATABASE_USER = 'postgres'
$env:DATABASE_PASSWORD = 'your-local-password'
$env:JWT_SECRET = 'replace-with-a-random-secret-of-at-least-32-bytes'
cd backend
mvn spring-boot:run
```

The service listens on `http://localhost:8080`. Flyway applies versioned files from `backend/src/main/resources/db/migration` at startup. Hibernate uses `ddl-auto=validate`, so schema changes must be added as a new migration. HikariCP is configured as the connection pool; JDBC and Jackson timestamps use UTC. Set `CORS_ALLOWED_ORIGINS` to a comma-separated list of trusted frontend origins.

## Run the frontend

```sh
cd frontend
npm install
npm run dev
```

Vite defaults to `http://localhost:5173`. The frontend uses `http://localhost:8080/api` unless `VITE_API_URL` is set (for example, `http://localhost:8080/api`). Authenticated users use REST data. The optional demo workspace is stored separately in browser local storage and never sent to the API.

## Persistence and ownership

- Users own categories, tasks, notifications, and task activities. Every private query scopes by the authenticated user's id.
- Task tags are stored as rows in `task_tags`. Deleting a task removes its tags and activities; deleting a category sets its task references to `NULL`, preserving tasks.
- Registration creates starter categories. New task category labels that do not exist yet are created for that user.
- Task create/update/status operations record activity. Tasks created or rescheduled with a due date create a stored reminder notification.
- Task collection responses are paginated with `content`, `page`, `size`, `totalElements`, and `totalPages`. Filters include `search`, `status`, `priority`, `categoryId`, `dueFrom`, and `dueTo`; sorting accepts `createdAt`, `dueDate`, `priority`, `title`, or `updatedAt`.
- Dashboard routes accept an IANA `timezone` parameter. `today` and `upcoming` default to UTC if omitted or invalid.

## API routes

All private routes require `Authorization: Bearer <JWT>`.

| Method | Route | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | Authenticated |
| GET, POST | `/api/tasks` | Authenticated |
| GET, PUT, DELETE | `/api/tasks/{id}` | Authenticated, owner only |
| PATCH | `/api/tasks/{id}/status` | Authenticated, owner only |
| GET, POST | `/api/categories` | Authenticated |
| PUT, DELETE | `/api/categories/{id}` | Authenticated, owner only |
| GET | `/api/dashboard/stats`, `/today`, `/upcoming`, `/recent-activity` | Authenticated |
| GET | `/api/notifications` | Authenticated |
| PATCH | `/api/notifications/{id}/read`, `/api/notifications/read-all` | Authenticated, owner only |

Validation errors return HTTP 400 with field messages. Duplicate email/category names return 409, invalid credentials return 401, and missing or cross-user resources return 404.

## API examples

See [`backend/api-examples.http`](backend/api-examples.http) for register, login, create/list/update/status/delete task, category, dashboard, and notification requests. Replace the token placeholder after login.

## Build and test

```sh
cd backend
mvn test
mvn package
cd ../frontend
npm run build
```

Backend integration tests require PostgreSQL configured through the environment variables above. The frontend build does not require a running API.
