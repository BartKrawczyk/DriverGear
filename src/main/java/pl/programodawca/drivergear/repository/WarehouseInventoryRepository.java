package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.WarehouseInventory;

import java.util.Optional;

/**
 * Repository interface for managing warehouse inventory items.
 * Provides methods for finding and checking inventory by clothing type and quantity.
 */
@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {

    /**
     * Find a warehouse inventory item by clothing type.
     * 
     * @param clothingType The clothing type to search for
     * @return An Optional containing the warehouse inventory item if found, or empty if not found
     */
    Optional<WarehouseInventory> findByClothingType(ClothingType clothingType);

    /**
     * Check if a warehouse inventory item exists with the specified clothing type
     * and quantity greater than the specified value.
     * 
     * @param clothingType The clothing type to search for
     * @param quantity The minimum quantity required
     * @return true if an item exists with the specified criteria, false otherwise
     */
    boolean existsByClothingTypeAndQuantityGreaterThan(ClothingType clothingType, int quantity);
}
