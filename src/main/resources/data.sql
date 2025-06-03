-- Disable FK checks to allow truncation
SET FOREIGN_KEY_CHECKS = 0;

-- Clean all dependent tables first
TRUNCATE TABLE clothing_compensations;
TRUNCATE TABLE clothing_assignments;
TRUNCATE TABLE clothing_allowances;
TRUNCATE TABLE position_clothing_items;
TRUNCATE TABLE position_clothing_allowances;
TRUNCATE TABLE employees;
TRUNCATE TABLE warehouse_inventory;
TRUNCATE TABLE clothing_types;
TRUNCATE TABLE positions;
TRUNCATE TABLE departments;
TRUNCATE TABLE users;

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;

-- Users
INSERT IGNORE INTO users (username, password, role, first_name, last_name, locked) VALUES
                                                                                       ('testuser', '$2a$10$NLlrZizOVhKa3d.wuh2z5uhr6hLmzXw7noZwmIxhl7deKZxLg2Fcm', 'USER', 'Test', 'User', false),
                                                                                       ('admin', '$2a$10$NLlrZizOVhKa3d.wuh2z5uhr6hLmzXw7noZwmIxhl7deKZxLg2Fcm', 'ADMIN', 'Admin', 'Admin', false);

-- Departments
INSERT INTO departments (id, code, name, description, active) VALUES
                                                                  (1, 'ADMIN', 'Administracja', 'Dział administracyjny', true),
                                                                  (2, 'MAG', 'Magazyn', 'Magazyn części', true),
                                                                  (3, 'LOG', 'Logistyka i przewozy', 'Dział logistyki i przewozów pasażerskich', true),
                                                                  (4, 'IT', 'Informatyka', 'Dział informatyczny', true),
                                                                  (5, 'WAR', 'Warsztat', 'Warsztat naprawczy pojazdów', true);

-- Positions
INSERT INTO positions (id, name, code, description, active, department_id) VALUES
                                                                               (1, 'Sekretarka', 'S001', 'Zarządza biurem', true, 1),
                                                                               (2, 'Pracownik magazynowy', 'PM001', 'Pracuje na magazynie części', true, 2),
                                                                               (3, 'Kierowca autobusu', 'KIERA001', 'Kierowca pojazdów transportu publicznego', true, 3),
                                                                               (4, 'Mechanik', 'ME001', 'Pracownik techniczny warsztatu', true, 5),
                                                                               (5, 'Kierownik IT', 'KIERIT001', 'Zarządza działem informatycznym', true, 4),
                                                                               (6, 'Specjalista IT', 'SPECIT001', 'Pracownik działu informatycznego', true, 4);

-- Clothing Types
INSERT INTO clothing_types (id, name, description, barcode, compensation_value, standard_lifetime_months, active) VALUES
                                                                                                                      (1, 'Kask', 'Standardowy kask zabezpieczający głowę', 'KASK001', 50.00, 24, true),
                                                                                                                      (2, 'Rękawice robocze', 'Wzmocnione rękawice robocze', 'GLOV001', 20.00, 6, true),
                                                                                                                      (3, 'Buty ochronne', 'Obuwie ochronne', 'BOOT001', 100.00, 12, true),
                                                                                                                      (4, 'Kamizelka odblaskowa', 'Dobrze widoczna kamizelka odblaskowa', 'VEST001', 30.00, 12, true),
                                                                                                                      (5, 'Kurtka robocza', 'Wzmocniona kurtka robocza', 'JACK001', 120.00, 24, true),
                                                                                                                      (6, 'Spodnie robocze', 'Wzmocnione spodnie robocze', 'PANT001', 80.00, 12, true);

-- Employees
INSERT INTO employees (id, first_name, last_name, position_id, active, employee_number, hire_date, department_id, phone_number, email, gender, created_at) VALUES
                                                                                                                                                               (1, 'John', 'Smith', 2, true, 'PW10001', '2025-01-15', 2, '+48123456789', 'john.smith@example.com', 'MALE', '2025-06-01 12:00:00'),
                                                                                                                                                               (2, 'Anna', 'Kowalska', 3, true, 'DR10002', '2024-06-10', 3, '+48987654321', 'anna.kowalska@example.com', 'FEMALE', '2025-06-01 12:00:00');

-- Warehouse Inventory (simplified, no size)
INSERT INTO warehouse_inventory (clothing_type_id, quantity, last_updated) VALUES
                                                                               (1, 30, NOW()), -- Kaski
                                                                               (2, 15, NOW()), -- Rękawice robocze
                                                                               (3, 20, NOW()); -- Buty ochronne

-- Position Clothing Allowances
INSERT INTO position_clothing_allowances (id, position_id, department_id, notes, active, created_at) VALUES
                                                                                                         (1, 2, 2, 'Standardowe wyposażenie magazynu', true, NOW()),
                                                                                                         (2, 3, 3, 'Wyposażenie kierowcy', true, NOW());

-- Position Clothing Items
INSERT INTO position_clothing_items (id, position_clothing_allowance_id, clothing_type_id, quantity, validity_period, mandatory, notes, active, created_at) VALUES
                                                                                                                                                                (1, 1, 1, 1, 24, true, 'Magazynowy kask', true, NOW()),
                                                                                                                                                                (2, 1, 2, 2, 6, true, 'Rękawice robocze', true, NOW()),
                                                                                                                                                                (3, 1, 3, 1, 12, true, 'Buty ochronne', true, NOW()),
                                                                                                                                                                (4, 2, 4, 1, 12, true, 'Kamizelka dla kierowcy', true, NOW());

-- Clothing Allowances
INSERT INTO clothing_allowances (id, employee_id, position_id, start_date, end_date, notes, active, created_at, status)
VALUES
    (1, 1, 2, '2024-01-01', '2025-01-01', 'Przydział testowy', true, NOW(), 'ACTIVE');

-- Clothing Assignments (rozmiary pominięte lub NULL)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
)
VALUES
    (1, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 1, false, false, 'Kask przydzielony', 1, NOW()),
    (2, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 2, false, false, 'Rękawice przydzielone', 2, NOW()),
    (3, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 1, false, false, 'Buty przydzielone', 3, NOW());



-- ----------------------------------------------------------------------------------
-- ----------------------------- SCENARIUSZE ----------------------------------------
-- ----------------------------------------------------------------------------------

-- ----------------------------------------------------------------------------------
-- ------------ TEST CASE: AUTOMATIC CLOTHING COMPENSATION GENERATION ---------------
-- ----------------------------------------------------------------------------------

-- 1. Create a warehouse worker employee hired more than one year ago
INSERT INTO employees (
    id, first_name, last_name, position_id, active, employee_number, hire_date,
    department_id, phone_number, email, gender, created_at
) VALUES (
             20, 'Auto', 'Compensation', 2, true, 'COMP001', '2023-05-20',
             2, '+48555666777', 'auto.comp@example.com', 'MALE', NOW()
         );

-- 2. Create an active clothing allowance for the employee
INSERT INTO clothing_allowances (
    id, employee_id, position_id, start_date, end_date, notes, active, created_at, status
) VALUES (
             20, 20, 2, '2023-05-20', '2024-05-20', 'Test case for automatic compensation generation',
             true, NOW(), 'ACTIVE'
         );

-- 3. Create pending clothing assignments with past expiry dates
-- Helmet assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             200, 20, 1, '2023-05-20', '2023-12-31', 'PENDING',
             NULL, 1, false, false, 'Expired helmet assignment for auto compensation', 1, NOW()
         );

-- Work gloves assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             201, 20, 1, '2023-05-20', '2023-11-20', 'PENDING',
             NULL, 2, false, false, 'Expired gloves assignment for auto compensation', 2, NOW()
         );

-- Safety boots assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             202, 20, 1, '2023-05-20', '2024-01-31', 'PENDING',
             NULL, 1, false, false, 'Expired boots assignment for auto compensation', 3, NOW()
         );

-- Work jacket assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             203, 20, 1, '2023-05-20', '2024-02-28', 'PENDING',
             NULL, 1, false, false, 'Expired jacket assignment for auto compensation', 5, NOW()
         );

-- ----------------------------------------------------------------------------------