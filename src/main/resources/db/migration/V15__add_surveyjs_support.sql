-- ==========================================
-- SurveyJS Support
-- ==========================================

ALTER TABLE forms
    ADD COLUMN IF NOT EXISTS survey_json TEXT;

ALTER TABLE forms
    ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 1;

ALTER TABLE forms
    ADD COLUMN IF NOT EXISTS published BOOLEAN NOT NULL DEFAULT FALSE;