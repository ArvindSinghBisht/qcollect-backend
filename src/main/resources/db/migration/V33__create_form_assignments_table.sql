CREATE TABLE form_assignments
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    project_id UUID NOT NULL,
    form_id UUID NOT NULL,
    accessor_id UUID NOT NULL,
    quality_checker_id UUID NOT NULL,
    assigned_by UUID NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,

    CONSTRAINT fk_form_assignment_project
        FOREIGN KEY (project_id)
            REFERENCES projects(id),

    CONSTRAINT fk_form_assignment_form
        FOREIGN KEY (form_id)
            REFERENCES forms(id),

    CONSTRAINT fk_form_assignment_accessor
        FOREIGN KEY (accessor_id)
            REFERENCES users(id),

    CONSTRAINT fk_form_assignment_qc
        FOREIGN KEY (quality_checker_id)
            REFERENCES users(id),

    CONSTRAINT fk_form_assignment_assigned_by
        FOREIGN KEY (assigned_by)
            REFERENCES users(id)
);

CREATE UNIQUE INDEX uk_form_assignment
    ON form_assignments(form_id, accessor_id);