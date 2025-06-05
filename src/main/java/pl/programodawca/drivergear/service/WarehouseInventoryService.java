package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.StockOverviewDTO;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.WarehouseInventory;

import java.util.List;

/**
 * Service interface for managing warehouse inventory.
 * Provides methods for checking stock availability and updating inventory levels.
 */
public interface WarehouseInventoryService {

    /**
     * Check if a specific clothing item is available in stock with the required quantity.
     * 
     * @param clothingType The type of clothing to check
     * @param quantity The required quantity
     * @return true if the item is in stock with sufficient quantity, false otherwise
     */
    boolean isItemInStock(ClothingType clothingType, int quantity);

    /**
     * Decrease the stock level of a specific clothing item.
     * Throws a BusinessException if there is not enough stock available.
     * 
     * @param clothingType The type of clothing to update
     * @param quantity The quantity to decrease
     * @throws pl.programodawca.drivergear.exception.BusinessException if there is not enough stock
     */
    void decreaseStock(ClothingType clothingType, int quantity);

    /**
     * Increase the stock level of a specific clothing item.
     * If the item doesn't exist in inventory, a new record will be created.
     * 
     * @param clothingType The type of clothing to update
     * @param quantity The quantity to increase
     */
    void increaseStock(ClothingType clothingType, int quantity);

    /**
     * Retrieve all inventory items.
     * 
     * @return A list of all warehouse inventory items
     */
    List<WarehouseInventory> findAllInventory();

    /**
     * Get stock overview for all inventory items.
     * 
     * @return A list of stock overview DTOs
     */
    List<StockOverviewDTO> getStockOverview();
}
