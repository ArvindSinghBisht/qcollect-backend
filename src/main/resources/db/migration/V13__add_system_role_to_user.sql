INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'SUPER_ADMIN', 'Platform Administrator', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'SUPER_ADMIN'
);

INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'TENANT_ADMIN', 'Tenant Administrator', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'TENANT_ADMIN'
);

INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'USER', 'Normal User', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'USER'
);
INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'PROJECT_ADMIN', 'Project Administrator', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'PROJECT_ADMIN'
);

INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'ACCESSOR', 'Accessor', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'ACCESSOR'
);

INSERT INTO roles (id, name, description, created_at, updated_at)
SELECT gen_random_uuid(), 'QUALITY_CHECKER', 'Quality Checker', NOW(), NOW()
    WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name = 'QUALITY_CHECKER'
);