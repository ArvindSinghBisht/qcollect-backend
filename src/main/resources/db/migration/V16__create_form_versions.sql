CREATE TABLE IF NOT EXISTS form_versions (

                                             id UUID PRIMARY KEY,

                                             form_id UUID NOT NULL,

                                             version INTEGER NOT NULL,

                                             survey_json TEXT NOT NULL,

                                             published BOOLEAN NOT NULL DEFAULT FALSE,

                                             active BOOLEAN NOT NULL DEFAULT TRUE,

                                             created_at TIMESTAMP,

                                             updated_at TIMESTAMP,

                                             created_by UUID,

                                             updated_by UUID,

                                             CONSTRAINT fk_form_versions_form
                                             FOREIGN KEY (form_id)
    REFERENCES forms(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_form_versions_form
    ON form_versions(form_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_form_version
    ON form_versions(form_id, version);