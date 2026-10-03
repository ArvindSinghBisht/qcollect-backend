CREATE TABLE IF NOT EXISTS form_fields
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    form_id UUID NOT NULL,
    label VARCHAR(255) NOT NULL,
    field_type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    placeholder VARCHAR(255),
    default_value TEXT,
    field_order INT,
    options TEXT,
    validation_json TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT fk_form_fields_form
        FOREIGN KEY(form_id)
            REFERENCES forms(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_form_fields_form ON form_fields(form_id);
