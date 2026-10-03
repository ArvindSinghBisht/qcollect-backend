CREATE TABLE qc_delegations (

                                id UUID PRIMARY KEY,

                                project_id UUID NOT NULL,

                                form_id UUID NOT NULL,

                                accessor_id UUID NOT NULL,

                                original_qc_id UUID NOT NULL,

                                delegate_qc_id UUID NOT NULL,

                                start_date DATE NOT NULL,

                                end_date DATE NOT NULL,

                                reason VARCHAR(500),

                                active BOOLEAN NOT NULL DEFAULT TRUE,

                                created_at TIMESTAMP,

                                updated_at TIMESTAMP,

                                created_by UUID,

                                updated_by UUID,

                                FOREIGN KEY(project_id) REFERENCES projects(id),

                                FOREIGN KEY(form_id) REFERENCES forms(id),

                                FOREIGN KEY(accessor_id) REFERENCES users(id),

                                FOREIGN KEY(original_qc_id) REFERENCES users(id),

                                FOREIGN KEY(delegate_qc_id) REFERENCES users(id)
);