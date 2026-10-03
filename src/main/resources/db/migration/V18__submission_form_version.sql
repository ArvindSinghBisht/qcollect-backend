ALTER TABLE submissions
    ADD COLUMN form_version_id UUID;

ALTER TABLE submissions
    ADD CONSTRAINT fk_submission_form_version
        FOREIGN KEY (form_version_id)
            REFERENCES form_versions(id);