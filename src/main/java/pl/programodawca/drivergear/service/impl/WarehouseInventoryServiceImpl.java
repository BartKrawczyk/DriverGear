package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.StockOverviewDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.WarehouseInventory;
import pl.programodawca.drivergear.repository.WarehouseInventoryRepository;
import pl.programodawca.drivergear.service.WarehouseInventoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the WarehouseInventoryService interface.
 * Manages warehouse inventory operations including stock checks and updates.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WarehouseInventoryServiceImpl implements WarehouseInventoryService {

    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @Override
    public boolean isItemInStock(ClothingType clothingType, int quantity) {
        // Add debug logging
        log.debug("Checking stock for: {}, quantity: {}", clothingType.getName(), quantity);

        boolean isInStock = warehouseInventoryRepository.existsByClothingTypeAndQuantityGreaterThan(
                clothingType, quantity - 1);

        log.debug("Item in stock: {}", isInStock);
        return isInStock;
    }

    @Override
    public void decreaseStock(ClothingType clothingType, int quantity) {
        // Add debug logging
        log.debug("Decreasing stock for: {}, quantity: {}", clothingType.getName(), quantity);

        Optional<WarehouseInventory> inventoryOptional = warehouseInventoryRepository.findByClothingType(clothingType);

        if (inventoryOptional.isEmpty()) {
            log.warn("Item not found in inventory: {}", clothingType.getName());
            throw new BusinessException("Brak przedmiotu w magazynie: " + clothingType.getName());
        }

        WarehouseInventory inventory = inventoryOptional.get();
        log.debug("Found inventory item: {}, available quantity: {}", 
                inventory.getClothingType().getName(), inventory.getQuantity());

        if (inventory.getQuantity() < quantity) {
            log.warn("Insufficient quantity in inventory. Available: {}, required: {}", 
                    inventory.getQuantity(), quantity);
            throw new BusinessException("Niewystarczająca ilość w magazynie. Dostępne: " + 
                    inventory.getQuantity() + ", wymagane: " + quantity);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        warehouseInventoryRepository.save(inventory);
        log.debug("Stock decreased successfully. New quantity: {}", inventory.getQuantity());
    }

    @Override
    public void increaseStock(ClothingType clothingType, int quantity) {
        // Add debug logging
        log.debug("Increasing stock for: {}, quantity: {}", clothingType.getName(), quantity);

        Optional<WarehouseInventory> inventoryOptional = warehouseInventoryRepository.findByClothingType(clothingType);

        if (inventoryOptional.isPresent()) {
            WarehouseInventory inventory = inventoryOptional.get();
            log.debug("Found existing inventory item: {}, current quantity: {}", 
                    inventory.getClothingType().getName(), inventory.getQuantity());

            inventory.setQuantity(inventory.getQuantity() + quantity);
            warehouseInventoryRepository.save(inventory);
            log.debug("Stock increased successfully. New quantity: {}", inventory.getQuantity());
        } else {
            log.debug("No existing inventory item found. Creating new entry.");

            WarehouseInventory newInventory = WarehouseInventory.builder()
                    .clothingType(clothingType)
                    .quantity(quantity)
                    .build();
            warehouseInventoryRepository.save(newInventory);
            log.debug("New inventory item created with quantity: {}", quantity);
        }
    }

    @Override
    public List<WarehouseInventory> findAllInventory() {
        return warehouseInventoryRepository.findAll();
    }

    @Override
    public List<StockOverviewDTO> getStockOverview() {
        List<WarehouseInventory> allInventory = warehouseInventoryRepository.findAll();
        return allInventory.stream()
                .map(StockOverviewDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
