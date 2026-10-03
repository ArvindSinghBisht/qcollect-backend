CREATE TABLE IF NOT EXISTS submissions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    form_id UUID NOT NULL,
    accessor_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    remarks VARCHAR(2000),
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    checked_by UUID,
    checked_at TIMESTAMP,
    remarks_string VARCHAR(500),
    device_id VARCHAR(255),
    is_synced BOOLEAN NOT NULL DEFAULT FALSE,
    synced_at TIMESTAMP,
    sync_version INT NOT NULL DEFAULT 1,
    last_modified_device VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_submission_form
        FOREIGN KEY (form_id)
            REFERENCES forms(id),
    CONSTRAINT fk_submission_accessor
        FOREIGN KEY (accessor_id)
            REFERENCES users(id),
    CONSTRAINT fk_submission_checked_by
        FOREIGN KEY (checked_by)
            REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_submissions_form ON submissions(form_id);
CREATE INDEX IF NOT EXISTS idx_submissions_accessor ON submissions(accessor_id);
