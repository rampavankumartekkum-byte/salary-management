# AI Usage Notes

This solution was built in conversation with Claude (Anthropic), used as a pair-programmer for
scaffolding, boilerplate, and first-draft implementation, with direction and review from me.

## Workflow
1. Discussed the requirements doc first — goal, persona, and what to deliberately leave out —
   before any code, per the assessment instructions.
2. Chose the stack together: Java/Spring Boot backend (to match the JD) and React/MUI frontend
   (per the assessment's technical constraints), PostgreSQL, Docker Compose.
3. Built backend layer by layer: entity → migration → repository/specifications → DTOs → service
   → controller → exception handling → seed script → tests, committing after each layer.
4. Built the frontend the same way: API client → components → pages → dashboard, with component
   tests.
5. Reviewed the generated code for correctness (e.g. null-safety in the dynamic filter
   specifications, N+1 query risk in the analytics queries, transaction boundaries in the service
   layer) rather than accepting it verbatim.

## What I changed / would still want to change
- [Fill this in with anything you personally adjusted — column names, seed data assumptions,
  additional validation, etc. Doing this before submitting is worth it: the interview will ask
  you to walk through design decisions and trade-offs directly.]

## Where I'd push back on my own AI-assisted output if I had more time
- The `AnalyticsServiceImpl.fetchBreakdown` method interpolates a column name into a native SQL
  string. It's safe today because the column always comes from a fixed internal allow-list
  (`"department"`, `"country"`, `"designation"` — never user input), but if this method were
  extended to accept a caller-supplied column, it would need an explicit allow-list check first.
- Salary bands aggregate raw `base_salary` values across currencies without FX normalization —
  flagged explicitly in both `REQUIREMENTS.md` and a code comment in `AnalyticsServiceImpl`,
  rather than silently producing a number that looks precise but conflates USD and INR.
