ALTER TABLE submission_photos
    ADD COLUMN IF NOT EXISTS upload_status VARCHAR(30);

UPDATE submission_photos
SET upload_status = 'PENDING'
WHERE upload_status IS NULL;

ALTER TABLE submission_photos
    ALTER COLUMN upload_status SET NOT NULL;