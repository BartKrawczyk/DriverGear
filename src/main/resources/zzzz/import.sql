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
                                                                                                                      (2, 'Rękawice robocze', 'Wzwmocnione rękawice robocze', 'GLOV001', 20.00, 6, true),
                                                                                                                      (3, 'Buty ochronne', 'Obuwie ochronne', 'BOOT001', 100.00, 12, true),
                                                                                                                      (4, 'Kamizelka odblaskowa', 'Dobrze widoczna kamizelka odblaskowa', 'VEST001', 30.00, 12, true),
                                                                                                                      (5, 'Kurtka robocza', 'Wzmocniona kurtka robocza', 'JACK001', 120.00, 24, true),
                                                                                                                      (6, 'Spodnie robocze', 'Wzmocnione spodnie robocze', 'PANT001', 80.00, 12, true);

-- Employees
INSERT INTO employees (id, first_name, last_name, position_id, active, employee_number, hire_date, department_id, phone_number, email, gender, created_at) VALUES
                                                                                                                                                               (1, 'John', 'Smith', 2, true, 'PW10001', '2022-01-15', 2, '+48123456789', 'john.smith@example.com', 'MALE', NOW()),
                                                                                                                                                               (2, 'Anna', 'Kowalska', 3, true, 'DR10002', '2021-06-10', 3, '+48987654321', 'anna.kowalska@example.com', 'FEMALE', NOW()),
                                                                                                                                                               (3, 'Piotr', 'Nowak', 4, true, 'ME10003', '2020-03-22', 4, '+48555666777', 'piotr.nowak@example.com', 'MALE', NOW()),
                                                                                                                                                               (4, 'Marta', 'Wójcik', 5, true, 'WW10004', '2022-09-05', 3, '+48111222333', 'marta.wojcik@example.com', 'FEMALE', NOW());

-- Position Clothing Allowances
INSERT INTO position_clothing_allowances (id, position_id, department_id, notes, active, created_at) VALUES
                                                                                                         (1, 2, 2, 'Standardowe wyposażenie pracownika magazynu', true, NOW()),
                                                                                                         (2, 3, 3, 'Standardowe wyposażenie kierowcy', true, NOW()),
                                                                                                         (3, 4, 5, 'Standardowe wyposażenie mechanika', true, NOW()),
                                                                                                         (4, 6, 4, 'Standardowe wyposażenie informatyka', true, NOW());

-- Position Clothing Items
INSERT INTO position_clothing_items (id, position_clothing_allowance_id, clothing_type_id, quantity, validity_period, mandatory, notes, active, created_at) VALUES
                                                                                                                                                                (1, 1, 1, 1, 24, true, 'Required for all production areas', true, NOW()),
                                                                                                                                                                (2, 1, 2, 2, 6, true, 'Replace every 6 months or when worn', true, NOW()),
                                                                                                                                                                (3, 1, 3, 1, 12, true, 'Required for all production areas', true, NOW()),
                                                                                                                                                                (4, 1, 4, 1, 12, true, 'Required for all production areas', true, NOW()),
                                                                                                                                                                (5, 2, 4, 1, 12, true, 'Required for visibility', true, NOW()),
                                                                                                                                                                (6, 2, 5, 1, 24, true, 'For cold weather', true, NOW()),
                                                                                                                                                                (7, 2, 3, 1, 12, true, 'Required for safety', true, NOW()),
                                                                                                                                                                (8, 3, 1, 1, 24, true, 'Required for all maintenance work', true, NOW()),
                                                                                                                                                                (9, 3, 2, 3, 4, true, 'Replace frequently due to heavy wear', true, NOW()),
                                                                                                                                                                (10, 3, 3, 1, 12, true, 'Required for safety', true, NOW()),
                                                                                                                                                                (11, 3, 6, 2, 12, true, 'Durable work pants', true, NOW()),
                                                                                                                                                                (12, 4, 3, 1, 12, true, 'Required for safety', true, NOW()),
                                                                                                                                                                (13, 4, 4, 1, 12, true, 'Required for visibility', true, NOW()),
                                                                                                                                                                (14, 4, 2, 2, 6, true, 'For handling materials', true, NOW());

-- Clothing Allowances
INSERT INTO clothing_allowances (id, employee_id, position_id, start_date, end_date, status, notes) VALUES
                                                                                                        (1, 1, 2, '2023-01-01', '2024-01-01', 'ACTIVE', 'Annual allowance'),
                                                                                                        (2, 2, 3, '2023-01-01', '2024-01-01', 'ACTIVE', 'Annual allowance'),
                                                                                                        (3, 3, 4, '2023-01-01', '2024-01-01', 'ACTIVE', 'Annual allowance'),
                                                                                                        (4, 4, 5, '2023-01-01', '2024-01-01', 'ACTIVE', 'Annual allowance');

-- Clothing Assignments
INSERT INTO clothing_assignments (id, employee_id, position_clothing_allowance_id, issue_date, status, notes, created_at) VALUES
                                                                                                                              (1, 1, 1, '2023-01-15', 'ISSUED', 'Standard issue', NOW()),
                                                                                                                              (2, 2, 2, '2023-01-20', 'ISSUED', 'Standard issue', NOW()),
                                                                                                                              (3, 3, 3, '2023-02-01', 'ISSUED', 'Standard issue', NOW()),
                                                                                                                              (4, 4, 4, '2023-02-10', 'ISSUED', 'Standard issue', NOW());

-- Clothing Compensations
INSERT INTO clothing_compensations (id, employee_id, clothing_assignment_id, amount, period_start, period_end, status, notes, created_at) VALUES
                                                                                                                                              (1, 1, 1, 50.00, '2023-01-01', '2023-06-30', 'PENDING', 'Compensation for missing helmet', NOW()),
                                                                                                                                              (2, 2, 2, 120.00, '2023-01-01', '2023-12-31', 'APPROVED', 'Compensation for missing jacket', NOW()),
                                                                                                                                              (3, 3, 3, 100.00, '2023-01-01', '2023-12-31', 'PAID', 'Compensation for missing boots', NOW()),
                                                                                                                                              (4, 4, 4, 20.00, '2023-01-01', '2023-06-30', 'CANCELLED', 'Compensation request cancelled', NOW());
