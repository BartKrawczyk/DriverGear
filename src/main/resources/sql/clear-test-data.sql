-- Usuwanie danych testowych dla pracownika ID = 20
DELETE FROM clothing_compensations WHERE employee_id = 20;
DELETE FROM clothing_assignments WHERE employee_id = 20;

-- Przywracanie przydziałów bazowych
INSERT INTO clothing_assignments (employee_id, clothing_type_id, assignment_date, expiry_date, status, quantity)
VALUES
    (20, 1, '2023-05-20', '2023-12-31', 'PENDING', 1),
    (20, 2, '2023-05-20', '2023-11-20', 'PENDING', 2),
    (20, 3, '2023-05-20', '2024-01-31', 'PENDING', 1),
    (20, 5, '2023-05-20', '2024-02-28', 'PENDING', 1);