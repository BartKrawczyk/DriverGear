-- Aggregate quantities by clothing type and drop the size column

-- Create temporary table to hold aggregated quantities
CREATE TABLE temp_warehouse_inventory AS
SELECT clothing_type_id, SUM(quantity) as total_quantity, MAX(last_updated) as last_updated
FROM warehouse_inventory
GROUP BY clothing_type_id;

-- Drop the original table
DROP TABLE warehouse_inventory;

-- Create new table without size column
CREATE TABLE warehouse_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clothing_type_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    last_updated DATETIME,
    FOREIGN KEY (clothing_type_id) REFERENCES clothing_types(id)
);

-- Insert aggregated data
INSERT INTO warehouse_inventory (clothing_type_id, quantity, last_updated)
SELECT clothing_type_id, total_quantity, last_updated
FROM temp_warehouse_inventory;

-- Drop temporary table
DROP TABLE temp_warehouse_inventory;