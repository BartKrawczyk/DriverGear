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
    (1, 'LOG-PAS', 'Dział Logistyki i Przewozów Pasażerskich', 'Dział zajmuje się organizacją przewozów pasażerskich', true);

-- Positions
INSERT INTO positions (id, name, code, description, active, department_id) VALUES
                                                                               (1, 'Kierowca autobusowy', 'POS001', 'Stanowisko: Kierowca autobusowy', true, 1),
                                                                               (2, 'Kierowca przetoku', 'POS002', 'Stanowisko: Kierowca przetoku', true, 1),
                                                                               (3, 'Kierowca autobusowy - Kierowca ADR', 'POS003', 'Stanowisko: Kierowca autobusowy - Kierowca ADR', true, 1),
                                                                               (4, 'Kontroler techniczny', 'POS004', 'Stanowisko: Kontroler techniczny', true, 1),
                                                                               (5, 'Kontroler jakości usług przewozowych', 'POS005', 'Stanowisko: Kontroler jakości usług przewozowych', true, 1),
                                                                               (6, 'Specjalista ds. Logistyki i Przewozów pasażerskich', 'POS006', 'Stanowisko: Specjalista ds. Logistyki i Przewozów pasażerskich', true, 1),
                                                                               (7, 'Kierownik Działu PT', 'POS007', 'Stanowisko: Kierownik Działu PT', true, 1),
                                                                               (8, 'Zastępca Kierownika Działu PT', 'POS008', 'Stanowisko: Zastępca Kierownika Działu PT', true, 1),
                                                                               (9, 'Kierownik Floty', 'POS009', 'Stanowisko: Kierownik Floty', true, 1),
                                                                               (10, 'Specjalista ds. Technicznych', 'POS0010', 'Stanowisko: Specjalista ds. Technicznych', true, 1),
                                                                               (11, 'Lider Specjalista ds. Planowania Służb i Rozkładów Jazdy', 'POS0011', 'Stanowisko: Lider Specjalista ds. Planowania Służb i Rozkładów Jazdy', true, 1);


-- Clothing Types
INSERT INTO clothing_types (id, name, description, barcode, compensation_value, standard_lifetime_months, active) VALUES
                                                                                                                      (1, 'Koszula z długim rękawem', 'Koszula z długim rękawem w kolorze białym', 'KOSZ001', 120.00, 12, true),
                                                                                                                      (2, 'Koszula z krótkim rękawem', 'Koszula z krótkim rękawem w kolorze białym', 'KOSZ002', 110.00, 12, true),
                                                                                                                      (3, 'Koszulka Polo', 'Koszulka polo w kolorze białym', 'KOSZ003', 90.00, 12, true),
                                                                                                                      (4, 'Krawat z klipsem / apaszka', 'Krawat lub apaszka', 'KRAW001', 40.00, 12, true),
                                                                                                                      (5, 'Spodnie / spódnica', 'Spodnie lub spódnica w kolorze czarnym', 'SPOD001', 80.00, 12, true),
                                                                                                                      (6, 'Bluza polarowa', 'Bluza polarowa', 'BLUZ001', 90.00, 12, true),
                                                                                                                      (7, 'Półbuty', 'Półbuty czarne', 'BUTY001', 160.00, 12, true),
                                                                                                                      (8, 'Buty ocieplane', 'Buty ocieplane czarne', 'BUTY002', 200.00, 12, true),
                                                                                                                      (9, 'Kamizelka ocieplana', 'Kamizelka ocieplana', 'KAMI001', 100.00, 12, true),
                                                                                                                      (10, 'Kamizelka odblaskowa', 'Kamizelka odblaskowa', 'KAMI002', 100.00, 12, true),
                                                                                                                      (11, 'Rękawice ochronne', 'Rękawice pięciopalcowe chroniące przed czynnikami mechanicznymi i olejami', 'REKA001', 100.00, 12, true);


-- Employees
INSERT INTO employees (id, first_name, last_name, position_id, active, employee_number, hire_date, department_id, phone_number, email, gender, created_at) VALUES
                                                                                                                                                               (1, 'Łukasz', 'Wiśniewski', 1, true, 'E1001', '2022-10-17', 1, '+48XXXXXXXX1', 'E1001@example.com', 'MALE', '2025-06-01 12:00:00'),
                                                                                                                                                               (2, 'Anna', 'Kowalska', 1, true, 'E1002', '2024-06-10', 1, '+48XXXXXXXX2', 'E1002@example.com', 'FEMALE', '2025-06-01 12:00:00');

-- Warehouse Inventory (simplified, no size)
INSERT INTO warehouse_inventory (clothing_type_id, quantity, last_updated) VALUES
                                                                               (1, 10, NOW()), -- Koszula z długim rękawem
                                                                               (2, 15, NOW()), -- Koszula z krótkim rękawem
                                                                               (3, 17, NOW()), -- Koszulka Polo
                                                                               (4, 23, NOW()), -- Krawat z klipsem / apaszka
                                                                               (5, 4, NOW()), -- Spodnie / spódnica
                                                                               (6, 67, NOW()), -- Bluza polarowa
                                                                               (7, 3, NOW()), -- Półbuty
                                                                               (8, 7, NOW()), -- Buty ocieplane
                                                                               (9, 9, NOW()), -- Kamizelka ocieplana
                                                                               (10, 9, NOW()), -- Kamizelka odblaskowa
                                                                               (11, 5, NOW()); -- Rękawice ochronne

-- Position Clothing Allowances
INSERT INTO position_clothing_allowances (id, position_id, department_id, notes, active, created_at) VALUES
    (1, 1, 1, 'Standardowe wyposażenie magazynu', true, NOW());

-- Position Clothing Items
INSERT INTO position_clothing_items (id, position_clothing_allowance_id, clothing_type_id, quantity, validity_period, mandatory, notes, active, created_at) VALUES
                                                                                                                                                                (1, 1, 1, 1, 24, true, 'Koszula z długim rękawem', true, NOW()),
                                                                                                                                                                (2, 1, 2, 2, 6, true, 'Koszula z krótkim rękawem', true, NOW()),
                                                                                                                                                                (3, 1, 3, 1, 12, true, 'Koszulka Polo', true, NOW()),
                                                                                                                                                                (4, 1, 4, 1, 12, true, 'Krawat z klipsem / apaszka', true, NOW()),
                                                                                                                                                                (5, 1, 5, 1, 12, true, 'Spodnie / spódnica', true, NOW()),
                                                                                                                                                                (6, 1, 6, 1, 12, true, 'Bluza polarowa', true, NOW()),
                                                                                                                                                                (7, 1, 7, 1, 12, true, 'Półbuty', true, NOW()),
                                                                                                                                                                (8, 1, 8, 1, 12, true, 'Buty ocieplane', true, NOW()),
                                                                                                                                                                (9, 1, 9, 1, 12, true, 'Kamizelka ocieplana', true, NOW()),
                                                                                                                                                                (10, 1, 10, 1, 12, true, 'Kamizelka odblaskowa', true, NOW()),
                                                                                                                                                                (11, 1, 11, 1, 12, true, 'Rękawice ochronne', true, NOW());

-- Clothing Allowances
INSERT INTO clothing_allowances (id, employee_id, position_id, start_date, end_date, notes, active, created_at, status)
VALUES
    (1, 1, 1, '2024-01-01', '2025-01-01', 'Pierwszy przydział', true, NOW(), 'ACTIVE'),
    (2, 2, 1, '2024-06-10', '2025-06-10', 'Pierwszy przydział', true, NOW(), 'ACTIVE');

-- Clothing Assignments (rozmiary pominięte lub NULL)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
)
VALUES
    (1, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 1, false, false, 'Kask przydzielony', 1, NOW()),
    (2, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 2, false, false, 'Rękawice przydzielone', 2, NOW()),
    (3, 1, 1, '2024-01-01', '2026-01-01', 'PENDING', NULL, 1, false, false, 'Buty przydzielone', 3, NOW()),
-- Koszula z długim rękawem
    (100, 2, 1, '2024-06-10', '2026-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 1, NOW()),
-- Koszula z krótkim rękawem
    (101, 2, 1, '2024-06-10', '2024-12-10', 'PENDING', NULL, 2, false, false, 'Automatyczny przydział', 2, NOW()),
-- Koszulka Polo
    (102, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 3, NOW()),
-- Krawat z klipsem / apaszka
    (103, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 4, NOW()),
-- Spodnie / spódnica
    (104, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 5, NOW()),
-- Bluza polarowa
    (105, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 6, NOW()),
-- Półbuty
    (106, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 7, NOW()),
-- Buty ocieplane
    (107, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 8, NOW()),
-- Kamizelka ocieplana
    (108, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 9, NOW()),
-- Kamizelka odblaskowa
    (109, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 10, NOW()),
-- Rękawice ochronne
    (110, 2, 1, '2024-06-10', '2025-06-10', 'PENDING', NULL, 1, false, false, 'Automatyczny przydział', 11, NOW());



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
             3, 'Marek', 'Spóźnialski', 1, true, 'E1003', '2023-05-20',
             1, '+48555666777', 'E1003@example.com', 'MALE', NOW()
         );

-- 2. Create an active clothing allowance for the employee
INSERT INTO clothing_allowances (
    id, employee_id, position_id, start_date, end_date, notes, active, created_at, status
) VALUES (
             20, 3, 1, '2023-05-20', '2024-05-20', 'Test case for automatic compensation generation',
             true, NOW(), 'ACTIVE'
         );

-- 3. Create pending clothing assignments with past expiry dates
-- Helmet assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             200, 3, 1, '2023-05-20', '2023-12-31', 'PENDING',
             NULL, 1, false, false, 'Expired', 1, NOW()
         );

-- Work gloves assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             201, 3, 1, '2023-05-20', '2023-11-20', 'PENDING',
             NULL, 1, false, false, 'Expired ', 2, NOW()
         );

-- Safety boots assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             202, 3, 1, '2023-05-20', '2024-01-31', 'PENDING',
             NULL, 1, false, false, 'Expired', 3, NOW()
         );

-- Work jacket assignment (expired)
INSERT INTO clothing_assignments (
    id, employee_id, position_clothing_allowance_id, assignment_date, expiry_date, status,
    size, quantity, issued_to_employee, eligible_for_compensation, notes, clothing_type_id, created_at
) VALUES (
             203, 3, 1, '2023-05-20', '2024-02-28', 'PENDING',
             NULL, 1, false, false, 'Expired', 5, NOW()
         );

-- ----------------------------------------------------------------------------------