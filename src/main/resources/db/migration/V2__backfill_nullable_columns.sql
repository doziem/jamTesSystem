UPDATE users
SET active = false
WHERE active IS NULL;

UPDATE doctors
SET experience = 0
WHERE experience IS NULL;

UPDATE pharmacies
SET main_pharmacy = false
WHERE main_pharmacy IS NULL;

UPDATE patients
SET active = false
WHERE active IS NULL;

UPDATE lab_report
SET requested_by = 'UNKNOWN'
WHERE requested_by IS NULL;

UPDATE lab_report
SET test_name = 'UNKNOWN'
WHERE test_name IS NULL;

UPDATE billing
SET payment_method = 'Unknown'
WHERE payment_method IS NULL;

UPDATE billing
SET billing_date = CURRENT_DATE
WHERE billing_date IS NULL;

UPDATE prescription
SET medication_name = 'UNKNOWN'
WHERE medication_name IS NULL;

UPDATE prescription
SET dosage = 'UNKNOWN'
WHERE dosage IS NULL;

UPDATE prescription
SET frequency = 'UNKNOWN'
WHERE frequency IS NULL;
