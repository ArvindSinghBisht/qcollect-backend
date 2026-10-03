-- ProjectInvitation extends BaseEntity, so project_invitations must contain
-- the complete BaseEntity audit column set.
--
-- V26-V29 created active, created_by and updated_at, but omitted updated_by.
-- This migration also safely backfills audit timestamps before enforcing the
-- same NOT NULL rules declared by BaseEntity.

ALTER TABLE project_invitations
    ADD COLUMN IF NOT EXISTS updated_by UUID;

UPDATE project_invitations
SET active = COALESCE(active, TRUE),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, created_at, CURRENT_TIMESTAMP)
WHERE active IS NULL
   OR created_at IS NULL
   OR updated_at IS NULL;

ALTER TABLE project_invitations
    ALTER COLUMN active SET DEFAULT TRUE,
    ALTER COLUMN active SET NOT NULL,
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN updated_at SET NOT NULL;
