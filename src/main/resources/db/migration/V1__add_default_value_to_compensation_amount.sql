-- Add default value to compensation_amount column in position_clothing_items table
ALTER TABLE position_clothing_items MODIFY compensation_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;