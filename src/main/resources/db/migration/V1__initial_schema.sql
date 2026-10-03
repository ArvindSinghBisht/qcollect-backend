-- =====================================================
-- QCollect Initial Database Schema
-- Core system tables only. Forms/submissions are created
-- by later migrations to avoid duplicate table creation.
-- =====================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS tenants
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    logo_url TEXT
);

CREATE TABLE IF NOT EXISTS projects
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id UUID,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    CONSTRAINT fk_project_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenants(id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id UUID,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    CONSTRAINT fk_user_tenant
        FOREIGN KEY (tenant_id)
            REFERENCES tenants(id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_users
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    project_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT fk_project_users_project
        FOREIGN KEY (project_id)
            REFERENCES projects(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_users_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_project_users_role
        FOREIGN KEY (role_id)
            REFERENCES roles(id),
    CONSTRAINT uk_project_user UNIQUE(project_id, user_id)
);

CREATE TABLE IF NOT EXISTS audit_logs
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID,
    module VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_id UUID,
    description VARCHAR(1000),
    ip_address VARCHAR(100),
    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_project_tenant ON projects(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_tenant ON users(tenant_id);
CREATE INDEX IF NOT EXISTS idx_project_users_project ON project_users(project_id);
CREATE INDEX IF NOT EXISTS idx_project_users_user ON project_users(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id);
