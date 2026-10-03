INSERT INTO roles(name, description)
VALUES
    ('SUPER_ADMIN','Platform Owner'),
    ('TENANT_ADMIN','Tenant Administrator'),
    ('PROJECT_ADMIN','Project Administrator'),
    ('ACCESSOR','Field Data Collector'),
    ('QUALITY_CHECKER','Quality Checker')
ON CONFLICT (name) DO NOTHING;
