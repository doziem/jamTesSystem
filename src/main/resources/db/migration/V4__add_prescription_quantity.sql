ALTER TABLE prescription
    ADD COLUMN IF NOT EXISTS quantity integer;

UPDATE prescription
SET quantity = 1
WHERE quantity IS NULL OR quantity <= 0;

ALTER TABLE prescription
    ALTER COLUMN quantity SET DEFAULT 1,
    ALTER COLUMN quantity SET NOT NULL;
