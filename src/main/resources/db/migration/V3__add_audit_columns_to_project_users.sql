ALTER TABLE project_users
    ADD COLUMN IF NOT EXISTS created_by UUID;

ALTER TABLE project_users
    ADD COLUMN IF NOT EXISTS updated_by UUID;

ALTER TABLE project_users
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE project_users
SET updated_at = COALESCE(updated_at, created_at, CURRENT_TIMESTAMP)
WHERE updated_at IS NULL;

ALTER TABLE project_users
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
