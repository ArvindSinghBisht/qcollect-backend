CREATE TABLE IF NOT EXISTS submission_answer_reviews
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_answer_id UUID UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    CONSTRAINT fk_answer_review_answer
        FOREIGN KEY (submission_answer_id)
            REFERENCES submission_answers(id),
    CONSTRAINT fk_answer_review_user
        FOREIGN KEY (reviewed_by)
            REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS submission_photo_reviews
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_photo_id UUID UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL,
    remarks TEXT,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    CONSTRAINT fk_photo_review_photo
        FOREIGN KEY (submission_photo_id)
            REFERENCES submission_photos(id),
    CONSTRAINT fk_photo_review_user
        FOREIGN KEY (reviewed_by)
            REFERENCES users(id)
);
