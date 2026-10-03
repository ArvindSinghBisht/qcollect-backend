# Backend schema fix – V31

## Root cause

`SubmissionAnswerReview.comment` maps to `submission_answer_reviews.comment`, but V12 created the column as `remarks`. Hibernate therefore stopped startup with:

```text
Schema-validation: missing column [comment] in table [submission_answer_reviews]
```

## Fix

`V31__align_submission_answer_reviews.sql` safely:

- renames `remarks` to `comment` when appropriate;
- adds `comment` when neither column exists;
- copies old values if both columns exist;
- backfills `reviewed_at` and enforces the entity's non-null contract.

Do not edit migrations V1–V30 because they may already be recorded in `flyway_schema_history`.

## Run

```powershell
.\mvnw.cmd clean spring-boot:run
```

Expected Flyway output:

```text
Current version of schema "public": 30
Migrating schema "public" to version "31 - align submission answer reviews"
Successfully applied 1 migration
```
