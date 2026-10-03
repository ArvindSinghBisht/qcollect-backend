CREATE TABLE login_activities
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    active BOOLEAN DEFAULT TRUE,

    created_by UUID,

    updated_by UUID,

    created_at TIMESTAMP,

    updated_at TIMESTAMP,

    user_id UUID NOT NULL,

    ip_address VARCHAR(100),

    device VARCHAR(255),

    browser VARCHAR(255),

    operating_system VARCHAR(255),

    login_time TIMESTAMP,

    logout_time TIMESTAMP,

    active_session BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_login_user
        FOREIGN KEY(user_id)
            REFERENCES users(id)
);