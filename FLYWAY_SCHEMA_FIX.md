# Flyway / Hibernate schema fix

## Root cause

`ProjectInvitation` extends `BaseEntity`. Therefore Hibernate expects the table
`project_invitations` to contain all inherited audit fields:

- `id`
- `active`
- `created_by`
- `updated_by`
- `created_at`
- `updated_at`

Migrations `V26` through `V29` created every required field except
`updated_by`. Hibernate was correctly stopping startup with:

```text
Schema-validation: missing column [updated_by] in table [project_invitations]
```

## Fix added

`V30__align_project_invitations_audit_columns.sql`

The migration:

1. Adds `updated_by` using `IF NOT EXISTS`.
2. Backfills null audit values for any existing invitation rows.
3. Aligns `active`, `created_at`, and `updated_at` defaults/nullability with
   `BaseEntity`.

Do not edit or rename migrations `V1` through `V29`, because they may already
be recorded in `flyway_schema_history`.

## Run

From the project directory on Windows:

```bat
mvnw.cmd clean spring-boot:run
```

Or in IntelliJ, use **Build > Rebuild Project**, then start the application.
Flyway should apply V30 automatically and Hibernate validation should pass.

## Expected Flyway output

```text
Current version of schema "public": 29
Migrating schema "public" to version "30 - align project invitations audit columns"
Successfully applied 1 migration ... now at version v30
```

The PostgreSQL 18 / Flyway support message is a compatibility warning, not the
cause of this startup failure.
