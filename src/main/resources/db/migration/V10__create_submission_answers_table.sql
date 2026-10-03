CREATE TABLE IF NOT EXISTS submission_answers
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_id UUID NOT NULL,
    field_id UUID NOT NULL,
    value TEXT,
    synced BOOLEAN NOT NULL DEFAULT FALSE,
    synced_at TIMESTAMP,
    sync_version INT NOT NULL DEFAULT 0,
    last_modified_device VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_submission_answer_submission
        FOREIGN KEY(submission_id)
            REFERENCES submissions(id),
    CONSTRAINT fk_submission_answer_field
        FOREIGN KEY(field_id)
            REFERENCES form_fields(id)
);

CREATE INDEX IF NOT EXISTS idx_submission_answers_submission ON submission_answers(submission_id);
CREATE INDEX IF NOT EXISTS idx_submission_answers_field ON submission_answers(field_id);
