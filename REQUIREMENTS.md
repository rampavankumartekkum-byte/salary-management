# Salary Management System — Requirements

## Persona
HR Manager at ACME, responsible for salary data of ~10,000 employees across multiple countries,
currently managed in spreadsheets.

## Goal
Replace spreadsheet-based salary tracking with a web application that lets the HR manager
manage individual employee salary records and answer aggregate questions about how the
organization pays people — without needing to build pivot tables by hand.

## In Scope (MVP)

**Employee records**
- CRUD for employee salary records: name, employee code, department, designation, country,
  currency, base salary, bonus, employment type (full-time/contract), manager, date joined.
- Search by name/employee code, filter by department/country/designation/employment type/salary
  range, server-side pagination and sorting (required at 10k-row scale — no "load everything
  into the browser").

**Answering pay questions (the actual product goal)**
- A dashboard that answers the questions an HR manager actually asks:
  - Total payroll, and average/median salary by department, by country, by designation.
  - Salary distribution (bands / histogram) org-wide and per department.
  - Headcount and payroll cost breakdown by country/department.
- CSV export of the current filtered view, for anything the dashboard doesn't cover.

**Data**
- Seed script generating 10,000 realistic employees across ~5 countries, ~8 departments,
  multiple designations/salary bands, with a fixed random seed for reproducibility.

## Deliberately Out of Scope (and why)

- **Authentication / RBAC.** The persona is a single trusted HR manager; the assessment is
  scoped to the salary-management domain, not identity. Every endpoint would sit behind auth
  in a real deployment — I've left a clear seam (a single `SecurityConfig` class) to add
  Spring Security + JWT without touching business logic.
- **Payroll disbursement / bank integration.** This is a record-keeping and reporting tool,
  not a payments system — actually moving money is a different, much larger system with
  different compliance requirements.
- **Live FX conversion.** Each employee has a currency stored against their salary, but I'm
  not calling a live FX API to normalize everything to one currency — rates change constantly
  and an assessment sandbox shouldn't depend on an external API being reachable. Dashboard
  aggregates are computed per-currency; a `FxRateProvider` interface is the seam for adding
  real conversion later.
- **Salary revision history / audit trail.** Real HR systems need to track "who changed what,
  when, why" for compliance. I only store the current salary snapshot — versioning meaningfully
  increases schema and UI complexity and isn't needed to demonstrate the core workflow.
- **Open-ended natural-language Q&A over the data.** "Answer questions about how the org pays
  people" is satisfied with a structured, reliable analytics dashboard rather than an
  LLM-over-SQL feature, which would be more impressive but much less trustworthy for something
  as sensitive as compensation data — wrong SQL from an NLQ layer on salary data is a real risk,
  not a toy problem.
- **Employee self-service.** Only the HR manager persona is in scope; employees don't log in.
- **Multi-tenant / multi-org support.** Single organization (ACME) only.

## Tech Stack
- Backend: Java 21, Spring Boot 3 (Web, Data JPA, Validation), PostgreSQL, Flyway migrations.
- Frontend: React + Vite, MUI, Recharts for charts, Axios.
- Tests: JUnit 5 + Mockito (service layer), MockMvc (controller layer), Vitest + Testing
  Library (frontend components).
- Packaging: Docker Compose (postgres + backend + frontend).
