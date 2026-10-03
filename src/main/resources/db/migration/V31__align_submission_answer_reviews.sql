-- Align the existing submission_answer_reviews table with SubmissionAnswerReview.
-- V12 created the text column as "remarks", while the entity now maps it as "comment".
-- This migration is safe for existing databases and fresh installations.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'submission_answer_reviews'
          AND column_name = 'remarks'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'submission_answer_reviews'
          AND column_name = 'comment'
    ) THEN
        ALTER TABLE submission_answer_reviews
            RENAME COLUMN remarks TO comment;
    ELSIF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'submission_answer_reviews'
          AND column_name = 'comment'
    ) THEN
        ALTER TABLE submission_answer_reviews
            ADD COLUMN comment TEXT;
    END IF;
END
$$;

-- If both columns exist from a partially repaired environment, retain old review text.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'submission_answer_reviews'
          AND column_name = 'remarks'
    ) AND EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'submission_answer_reviews'
          AND column_name = 'comment'
    ) THEN
        EXECUTE 'UPDATE submission_answer_reviews
                 SET comment = remarks
                 WHERE comment IS NULL AND remarks IS NOT NULL';
    END IF;
END
$$;

-- SubmissionAnswerReview.reviewedAt is non-null in JPA.
UPDATE submission_answer_reviews
SET reviewed_at = COALESCE(reviewed_at, updated_at, created_at, CURRENT_TIMESTAMP)
WHERE reviewed_at IS NULL;

ALTER TABLE submission_answer_reviews
    ALTER COLUMN reviewed_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE submission_answer_reviews
    ALTER COLUMN reviewed_at SET NOT NULL;
