-- Add column
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS system_role_id UUID;

-- Add foreign key
ALTER TABLE users
    ADD CONSTRAINT fk_users_system_role
        FOREIGN KEY (system_role_id)
            REFERENCES roles(id);

-- Set USER role for existing users
UPDATE users
SET system_role_id = (
    SELECT id
    FROM roles
    WHERE name = 'USER'
)
WHERE system_role_id IS NULL;

-- Make column mandatory
ALTER TABLE users
    ALTER COLUMN system_role_id SET NOT NULL;