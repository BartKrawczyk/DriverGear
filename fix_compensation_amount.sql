-- Fix for "Field 'compensation_amount' doesn't have a default value" Error
-- This script should be executed directly on the database to add a default value to the compensation_amount column

-- Add default value to compensation_amount column in position_clothing_items table
ALTER TABLE position_clothing_items MODIFY compensation_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Verify the change
DESCRIBE position_clothing_items;