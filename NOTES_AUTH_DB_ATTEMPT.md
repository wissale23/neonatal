# Auth + Cloud DB attempt (WIP, not merged)

## Goal
Move authentication/user access to a cloud Postgres database so credentials and roles could be managed centrally.

## What I implemented / changed
- Started an alternative DB-backed authentication path (user + role lookup from Postgres).
- Added basic DB connection/initialisation wiring and a small Java check to validate connectivity in the deployed environment.
- Attempted to add/verify startup logging for auth/DB initialisation on Tsuru.

## What didn’t work (symptoms)
- The DB check initially failed because the PostgreSQL JDBC driver wasn’t being found (`ClassNotFoundException: org.postgresql.Driver`).
- After pointing to the correct driver jar, the DB connection worked, but the expected tables weren’t present (final check returned `false | false` for `app_users` and `user_babies`), so we couldn’t complete an end-to-end DB-backed login flow.
- Deployment/debugging was also slowed down by Tsuru CLI authentication issues (`401 Invalid session`), which made restarts/verification harder.

## What I tried to debug
- Confirmed the JDBC driver location and reran the connectivity check using the jar packaged with the deployed app.
- Fixed the table-existence query and verified whether the required tables were available.
- Used Tsuru logs and attempted app restarts to validate init behaviour.

## Why we decided to drop it
With the deadline approaching and the DB-backed path not stable/verified in production, we chose not to merge it into the submission branch.

## Next steps (if someone continues)
- Ensure the Postgres JDBC driver is reliably available in all deploy environments.
- Confirm the correct DB/schema and create/migrate the required tables (`app_users`, `user_babies`).
- Add a simple “DB health” check (log or endpoint) to confirm DB connectivity on deploy.
- Resolve Tsuru CLI session/auth issues to speed up testing and iteration.
