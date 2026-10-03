CREATE TABLE user_sessions
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    token VARCHAR(1000),

    device_name VARCHAR(255),

    browser VARCHAR(255),

    os VARCHAR(255),

    ip_address VARCHAR(100),

    login_time TIMESTAMP,

    logout_time TIMESTAMP,

    last_activity TIMESTAMP,

    active BOOLEAN DEFAULT TRUE,

    FOREIGN KEY(user_id)
        REFERENCES users(id)
);