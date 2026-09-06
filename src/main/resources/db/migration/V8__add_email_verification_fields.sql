ALTER TABLE users
    ADD COLUMN IF NOT EXISTS verified boolean NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS email_verification_token varchar(255);

UPDATE users
SET verified = true
WHERE verified IS FALSE AND email IS NOT NULL;
