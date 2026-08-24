# Architecture

```
┌──────────────────┐        HTTP/JSON        ┌────────────────────────┐        JDBC        ┌────────────┐
│  React (Vite)     │ ───────────────────────▶│  Spring Boot API        │ ───────────────────▶│ PostgreSQL │
│  MUI + Recharts    │◀─────────────────────── │  Controller → Service   │◀─────────────────────│            │
└──────────────────┘                          │  → Repository (JPA)     │                     └────────────┘
                                               └────────────────────────┘
```

## Backend layering
- **Controller** — HTTP concerns only (path/query binding, status codes). No business logic.
- **Service** — business rules: uniqueness checks, orchestration, transactions.
- **Repository** — `Spring Data JpaRepository` + `JpaSpecificationExecutor` for composable,
  null-safe dynamic filtering (`EmployeeSpecifications`) instead of hand-writing a combinatorial
  explosion of query methods for every filter combination.
- **DTOs** at the controller boundary — the JPA `Employee` entity is never serialized directly,
  so persistence changes don't leak into the API contract.

## Why Specifications over query methods or QueryDSL
The employee list screen needs to filter on any combination of ~6 optional fields. Spring Data
derived query methods would need one method per combination (2^6), and a raw JPQL string would
need manual null-checks and string concatenation. `Specification` composes cleanly, stays
type-safe, and each predicate is independently unit-testable.

## Why native SQL for analytics, not JPQL/streams
`AnalyticsServiceImpl` uses native SQL with `GROUP BY` and `PERCENTILE_CONT` rather than pulling
rows into Java and aggregating there. At 10k+ employees, computing averages/medians/group-bys in
the database is the only approach that stays fast as the dataset grows — pulling every row into
the JVM on every dashboard load doesn't scale and duplicates what Postgres already does well.

## Pagination
The employee list is server-side paginated (`Pageable`/`Page`) from day one — with 10,000 rows,
an unpaginated "load everything" list would be the first thing to fall over, and retrofitting
pagination after building an in-memory list-and-filter UI is more work than starting with it.

## Seed data design
`DataSeeder` (backend/src/main/java/.../seed/DataSeeder.java) generates realistic-looking data by
combining a small set of countries/currencies and department/designation/salary-multiplier
combinations, rather than fully random values — this makes the analytics dashboard produce
sensible, explainable numbers (e.g. Engineering Managers consistently earn more than QA
Engineers) instead of noise. It's seeded with a fixed `Random(42)` so the dataset is reproducible.

## What I'd add next (out of scope now, see REQUIREMENTS.md)
1. Spring Security + JWT auth (seam: `WebConfig`, would add a `SecurityFilterChain` bean).
2. Salary revision history as an append-only `salary_history` table.
3. FX normalization service (`FxRateProvider` interface) so cross-country dashboard totals are
   meaningful in one reporting currency.
4. Bulk CSV import for onboarding a whole department at once, mirroring the existing CSV export.
