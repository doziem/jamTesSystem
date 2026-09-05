ALTER TABLE prescription
    ADD COLUMN IF NOT EXISTS department varchar(255);

UPDATE prescription
SET department = 'GENERAL'
WHERE department IS NULL;

ALTER TABLE prescription
    ALTER COLUMN department SET DEFAULT 'GENERAL',
    ALTER COLUMN department SET NOT NULL;
