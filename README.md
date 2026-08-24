# ACME Salary Management

Web app for ACME's HR manager to manage salary records for ~10,000 employees across multiple
countries, and answer aggregate questions about how the org pays people, replacing spreadsheets.

See [REQUIREMENTS.md](./REQUIREMENTS.md) for scope and the reasoning behind what's deliberately
left out, [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md) for how the system fits together, and
[docs/AI_USAGE.md](./docs/AI_USAGE.md) for how AI tools were used to build this.

## Stack
- **Backend:** Java 21, Spring Boot 3 (Web, Data JPA, Validation), PostgreSQL, Flyway.
- **Frontend:** React + Vite, MUI, Recharts.
- **Tests:** JUnit 5 + Mockito + MockMvc (backend), Vitest + Testing Library (frontend).

## Run everything with Docker (recommended)

```bash
docker compose up --build
```

This starts Postgres, runs Flyway migrations, seeds 10,000 employees on first boot, and serves:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Postgres: localhost:5432 (salary_db / salary_user / salary_pass)

The seed only runs once — it's a no-op if the `employees` table already has rows, so restarting
the stack won't duplicate data. To reset, run `docker compose down -v` (drops the volume) then
`docker compose up --build` again.

## Run locally without Docker

**Database**
```bash
docker run -d --name salary-postgres -p 5432:5432 \
  -e POSTGRES_DB=salary_db -e POSTGRES_USER=salary_user -e POSTGRES_PASSWORD=salary_pass \
  postgres:16-alpine
```

**Backend** (from `backend/`)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=seed   # first run, to seed data
mvn spring-boot:run                                    # subsequent runs
```

**Frontend** (from `frontend/`)
```bash
npm install
npm run dev
```
Visit http://localhost:5173.

## Running tests

**Backend** (from `backend/`) — uses an in-memory H2 database, no Docker/Postgres needed:
```bash
mvn test
```

**Frontend** (from `frontend/`):
```bash
npm install
npm test
```

## API overview

| Method | Path                     | Description                                   |
|--------|--------------------------|------------------------------------------------|
| GET    | /api/employees           | Paginated search (q, department, country, designation, employmentType, minSalary, maxSalary) |
| GET    | /api/employees/{id}      | Get one employee                              |
| POST   | /api/employees           | Create employee                               |
| PUT    | /api/employees/{id}      | Update employee                               |
| DELETE | /api/employees/{id}      | Delete employee                               |
| GET    | /api/analytics/dashboard | Payroll summary + breakdowns by department/country/designation + salary bands |

## Demo video
`docs/demo.mp4` — see notes there (placeholder — record after `docker compose up` locally; a
screen recording can't be produced from within this sandbox since it has no display/browser).
