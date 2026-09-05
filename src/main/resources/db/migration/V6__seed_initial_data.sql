INSERT INTO users (id, name, email, phone, password, role, active)
VALUES
('11111111-1111-1111-1111-111111111111', 'Admin User', 'admin@jamtes.local', '08000000001', '$2a$10$7EqJtq98hPqEX7fNZaFWoOeBfEXAMPLEHASH', 'ADMIN', true),
('22222222-2222-2222-2222-222222222222', 'Dr John Doe', 'doctor@jamtes.local', '08000000002', '$2a$10$7EqJtq98hPqEX7fNZaFWoOeBfEXAMPLEHASH', 'DOCTOR', true);

INSERT INTO doctors (id, first_name, last_name, specialization, experience, user_id, availability)
VALUES
('33333333-3333-3333-3333-333333333333', 'John', 'Doe', 'General Medicine', 5, '22222222-2222-2222-2222-222222222222', 'Mon-Fri 9am-5pm');

INSERT INTO patients (id, first_name, last_name, email, phone, date_of_birth, gender, street, city, state, zip_code, country, active)
VALUES
('44444444-4444-4444-4444-444444444444', 'Jane', 'Smith', 'jane.smith@jamtes.local', '08000000003', '1995-01-15', 'Female', '12 Test Street', 'Lagos', 'Lagos', '100001', 'Nigeria', true);

INSERT INTO pharmacies (id, name, department, main_pharmacy, main_pharmacy_id)
VALUES
('55555555-5555-5555-5555-555555555555', 'Main Pharmacy', 'MAIN_PHARMACY', true, NULL);

INSERT INTO pharmacies (id, name, department, main_pharmacy, main_pharmacy_id)
VALUES
('66666666-6666-6666-6666-666666666666', 'Outpatient Pharmacy', 'GENERAL', false, '55555555-5555-5555-5555-555555555555');

INSERT INTO medications (id, name, category, unit_price, active)
VALUES
('77777777-7777-7777-7777-777777777777', 'Paracetamol', 'Analgesic', 5.00, true);

INSERT INTO pharmacy_inventory (id, pharmacy_id, medication_id, quantity_in_stock, reorder_level)
VALUES
('88888888-8888-8888-8888-888888888888', '55555555-5555-5555-5555-555555555555', '77777777-7777-7777-7777-777777777777', 100, 10),
('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '77777777-7777-7777-7777-777777777777', 25, 10);

INSERT INTO lab_report (id, patient_id, requested_by, test_name, result, report_date, request_date, conducted_by)
VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '44444444-4444-4444-4444-444444444444', 'Dr John Doe', 'Full Blood Count', 'Normal', '2026-09-01', '2026-08-31', 'Lab Tech');

INSERT INTO billing (id, patient_id, total_amount, paid, payment_method, billing_date)
VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '44444444-4444-4444-4444-444444444444', 20.00, true, 'Cash', '2026-09-01');

INSERT INTO prescription (id, patient_id, pharmacy_id, department, quantity, medication_name, dosage, frequency, prescribed_by, prescription_date, status, payment_confirmed, total_cost)
VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', '44444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666', 'GENERAL', 2, 'Paracetamol', '500mg', 'Twice daily', 'Dr John Doe', '2026-09-01', 'PENDING_PHARMACY_REVIEW', false, 10.00);
