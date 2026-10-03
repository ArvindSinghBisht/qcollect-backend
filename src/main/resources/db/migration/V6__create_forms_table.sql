CREATE TABLE IF NOT EXISTS forms
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT fk_form_project
        FOREIGN KEY(project_id)
            REFERENCES projects(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_forms_project ON forms(project_id);
