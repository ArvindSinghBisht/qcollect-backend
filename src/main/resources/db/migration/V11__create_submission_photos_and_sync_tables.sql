CREATE TABLE IF NOT EXISTS submission_photos
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_id UUID,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    mobile_photo_id UUID UNIQUE,
    photo_url VARCHAR(1000),
    synced BOOLEAN NOT NULL DEFAULT FALSE,
    synced_at TIMESTAMP,
    sync_version INT NOT NULL DEFAULT 0,
    last_modified_device VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_submission_photo_submission
        FOREIGN KEY (submission_id)
            REFERENCES submissions(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_submission_photos_submission ON submission_photos(submission_id);

CREATE TABLE IF NOT EXISTS sync_logs
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID,
    device_id VARCHAR(255) NOT NULL,
    uploaded_count INT NOT NULL DEFAULT 0,
    downloaded_count INT NOT NULL DEFAULT 0,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    message VARCHAR(1000),
    conflict_count INT,
    conflict_strategy VARCHAR(50),
    conflict_summary TEXT,
    CONSTRAINT fk_sync_logs_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS notifications
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);
