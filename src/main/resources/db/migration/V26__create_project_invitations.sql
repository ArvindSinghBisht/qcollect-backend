
CREATE TABLE project_invitations (

                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                     project_id UUID NOT NULL,

                                     email VARCHAR(255) NOT NULL,

                                     role_id UUID NOT NULL,

                                     token VARCHAR(255) NOT NULL UNIQUE,

                                     status VARCHAR(50) DEFAULT 'PENDING',

                                     expires_at TIMESTAMP,

                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                     accepted_at TIMESTAMP
);