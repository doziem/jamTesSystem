CREATE TABLE IF NOT EXISTS lab_request (
    id uuid PRIMARY KEY,
    patient_id uuid NOT NULL,
    requested_by uuid NOT NULL,
    request_date date,
    CONSTRAINT fk_lab_request_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_lab_request_user FOREIGN KEY (requested_by) REFERENCES users(id)
);

ALTER TABLE lab_report
    ADD COLUMN IF NOT EXISTS lab_request_id uuid;

INSERT INTO users (id, name, email, phone, password, role, active)
SELECT
    'f0000000-0000-0000-0000-000000000001'::uuid,
    'Legacy Lab Doctor',
    'legacy.lab.doctor@jamtes.local',
    '08000000999',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOeBfEXAMPLEHASH',
    'DOCTOR',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = 'f0000000-0000-0000-0000-000000000001'::uuid
);

INSERT INTO lab_request (id, patient_id, requested_by, request_date)
SELECT
    gen_random_uuid(),
    lr.patient_id,
    u.id,
    COALESCE(lr.request_date, CURRENT_DATE)
FROM lab_report lr
JOIN users u ON u.id::text = lr.requested_by
WHERE lr.lab_request_id IS NULL
GROUP BY lr.patient_id, u.id, COALESCE(lr.request_date, CURRENT_DATE);

INSERT INTO lab_request (id, patient_id, requested_by, request_date)
SELECT
    gen_random_uuid(),
    lr.patient_id,
    'f0000000-0000-0000-0000-000000000001'::uuid,
    COALESCE(lr.request_date, CURRENT_DATE)
FROM lab_report lr
WHERE lr.lab_request_id IS NULL
GROUP BY lr.patient_id, COALESCE(lr.request_date, CURRENT_DATE);

UPDATE lab_report lr
SET lab_request_id = lreq.id
FROM lab_request lreq, users u
WHERE lr.lab_request_id IS NULL
  AND u.id::text = lr.requested_by
  AND lreq.patient_id = lr.patient_id
  AND lreq.requested_by = u.id
  AND lreq.request_date = COALESCE(lr.request_date, CURRENT_DATE);

UPDATE lab_report lr
SET lab_request_id = lreq.id
FROM lab_request lreq
WHERE lr.lab_request_id IS NULL
  AND lreq.patient_id = lr.patient_id
  AND lreq.requested_by = 'f0000000-0000-0000-0000-000000000001'::uuid
  AND lreq.request_date = COALESCE(lr.request_date, CURRENT_DATE);

ALTER TABLE lab_report
    ALTER COLUMN lab_request_id SET NOT NULL;

ALTER TABLE lab_report
    ADD CONSTRAINT fk_lab_report_lab_request
    FOREIGN KEY (lab_request_id) REFERENCES lab_request(id);
