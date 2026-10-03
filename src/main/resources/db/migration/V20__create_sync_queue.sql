CREATE TABLE IF NOT EXISTS sync_queue (
                                          id UUID NOT NULL PRIMARY KEY,
                                          active BOOLEAN NOT NULL,
                                          created_at TIMESTAMP(6) NOT NULL,
    created_by UUID,
    updated_at TIMESTAMP(6) NOT NULL,
    updated_by UUID,

    error_message VARCHAR(1000),
    last_attempt TIMESTAMP(6),
    payload OID NOT NULL,
    retry_count INTEGER,
    status VARCHAR(255) NOT NULL,

    submission_id UUID NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT chk_sync_queue_status
    CHECK (status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED'))
    );