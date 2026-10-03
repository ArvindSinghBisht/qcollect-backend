CREATE TABLE project_google_sheets
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_by UUID,

    updated_by UUID,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    project_id UUID NOT NULL UNIQUE,

    google_email VARCHAR(255),

    spreadsheet_id VARCHAR(255),

    spreadsheet_name VARCHAR(255),

    sheet_name VARCHAR(255),

    access_token TEXT,

    refresh_token TEXT,

    token_expiry TIMESTAMP,

    connected BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_google_project
        FOREIGN KEY(project_id)
            REFERENCES projects(id)
            ON DELETE CASCADE
);