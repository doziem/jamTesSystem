ALTER TABLE users
    ALTER COLUMN id TYPE uuid USING id::uuid;

ALTER TABLE doctors
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN user_id TYPE uuid USING user_id::uuid;

ALTER TABLE patients
    ALTER COLUMN id TYPE uuid USING id::uuid;

ALTER TABLE pharmacies
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN main_pharmacy_id TYPE uuid USING main_pharmacy_id::uuid;

ALTER TABLE medications
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN unit_price SET DEFAULT 0;

ALTER TABLE lab_report
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN patient_id TYPE uuid USING patient_id::uuid;

ALTER TABLE billing
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN patient_id TYPE uuid USING patient_id::uuid,
    ALTER COLUMN total_amount SET DEFAULT 0,
    ALTER COLUMN paid SET DEFAULT false;

ALTER TABLE pharmacy_inventory
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN pharmacy_id TYPE uuid USING pharmacy_id::uuid,
    ALTER COLUMN medication_id TYPE uuid USING medication_id::uuid;

ALTER TABLE prescription
    ALTER COLUMN id TYPE uuid USING id::uuid,
    ALTER COLUMN patient_id TYPE uuid USING patient_id::uuid,
    ALTER COLUMN pharmacy_id TYPE uuid USING pharmacy_id::uuid,
    ALTER COLUMN status SET DEFAULT 'PENDING_PHARMACY_REVIEW',
    ALTER COLUMN payment_confirmed SET DEFAULT false,
    ALTER COLUMN total_cost SET DEFAULT 0;

UPDATE prescription
SET status = 'PENDING_PHARMACY_REVIEW'
WHERE status IS NULL;

UPDATE prescription
SET payment_confirmed = false
WHERE payment_confirmed IS NULL;

UPDATE prescription
SET total_cost = 0
WHERE total_cost IS NULL;

UPDATE billing
SET total_amount = 0
WHERE total_amount IS NULL;

UPDATE billing
SET paid = false
WHERE paid IS NULL;

UPDATE medications
SET unit_price = 0
WHERE unit_price IS NULL;

UPDATE pharmacy_inventory
SET quantity_in_stock = 0
WHERE quantity_in_stock IS NULL;

UPDATE pharmacy_inventory
SET reorder_level = 0
WHERE reorder_level IS NULL;
