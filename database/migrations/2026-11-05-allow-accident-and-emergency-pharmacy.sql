ALTER TABLE pharmacies
    DROP CONSTRAINT IF EXISTS pharmacies_department_check;

ALTER TABLE pharmacies
    ADD CONSTRAINT pharmacies_department_check
    CHECK (department IN (
        'MAIN_PHARMACY',
        'ACCIDENT_AND_EMERGENCY',
        'GENERAL',
        'CARDIOLOGY',
        'ORTHOPEDICS',
        'PEDIATRICS',
        'NEUROLOGY',
        'DERMATOLOGY',
        'LABORATORY'
    ));
