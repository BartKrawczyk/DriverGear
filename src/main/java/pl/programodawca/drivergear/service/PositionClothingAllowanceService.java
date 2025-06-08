package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;

import java.util.List;

/**
 * Service interface for managing position-based clothing allowances.
 */
public interface PositionClothingAllowanceService {

    /**
     * Create a new position clothing allowance with multiple clothing items.
     * 
     * @param departmentId The ID of the department
     * @param positionId The ID of the position
     * @param clothingItems List of clothing items to add to the allowance
     * @param notes Additional notes
     * @return The created position clothing allowance DTO
     */
    PositionClothingAllowanceDTO createPositionAllowance(Long departmentId, Long positionId, 
                                                        List<PositionClothingItemDTO> clothingItems, 
                                                        String notes);

    /**
     * Get a position clothing allowance by ID.
     * 
     * @param allowanceId The ID of the allowance
     * @return The position clothing allowance DTO
     */
    PositionClothingAllowanceDTO getPositionAllowanceById(Long allowanceId);

    /**
     * Get all position clothing allowances.
     * 
     * @return A list of position clothing allowance DTOs
     */
    List<PositionClothingAllowanceDTO> getAllPositionAllowances();

    /**
     * Get active position clothing allowances.
     * 
     * @return A list of active position clothing allowance DTOs
     */
    List<PositionClothingAllowanceDTO> getActivePositionAllowances();

    /**
     * Get position clothing allowances for a position.
     * 
     * @param positionId The ID of the position
     * @return A list of position clothing allowance DTOs
     */
    List<PositionClothingAllowanceDTO> getPositionAllowancesByPositionId(Long positionId);

    /**
     * Get position clothing allowances for a department.
     * 
     * @param departmentId The ID of the department
     * @return A list of position clothing allowance DTOs
     */
    List<PositionClothingAllowanceDTO> getPositionAllowancesByDepartmentId(Long departmentId);

    /**
     * Get position clothing allowances that include a specific clothing type.
     * 
     * @param clothingTypeId The ID of the clothing type
     * @return A list of position clothing allowance DTOs
     */
    List<PositionClothingAllowanceDTO> getPositionAllowancesByClothingTypeId(Long clothingTypeId);

    /**
     * Update a position clothing allowance.
     * 
     * @param allowanceId The ID of the allowance
     * @param clothingItems The updated list of clothing items
     * @param active Whether the allowance is active
     * @param notes The new notes
     * @return The updated position clothing allowance DTO
     */
    PositionClothingAllowanceDTO updatePositionAllowance(Long allowanceId, 
                                                        List<PositionClothingItemDTO> clothingItems, 
                                                        Boolean active, 
                                                        String notes);

    /**
     * Add a clothing item to a position clothing allowance.
     * 
     * @param allowanceId The ID of the allowance
     * @param clothingItem The clothing item to add
     * @return The updated position clothing allowance DTO
     */
    PositionClothingAllowanceDTO addClothingItemToAllowance(Long allowanceId, PositionClothingItemDTO clothingItem);

    /**
     * Update a clothing item in a position clothing allowance.
     * 
     * @param itemId The ID of the clothing item
     * @param clothingItem The updated clothing item
     * @return The updated clothing item DTO
     */
    PositionClothingItemDTO updateClothingItem(Long itemId, PositionClothingItemDTO clothingItem);

    /**
     * Remove a clothing item from a position clothing allowance.
     * 
     * @param itemId The ID of the clothing item to remove
     */
    void removeClothingItem(Long itemId);

    /**
     * Activate a position clothing allowance.
     * 
     * @param allowanceId The ID of the allowance
     * @return The updated position clothing allowance DTO
     */
    PositionClothingAllowanceDTO activatePositionAllowance(Long allowanceId);

    /**
     * Deactivate a position clothing allowance.
     * 
     * @param allowanceId The ID of the allowance
     * @return The updated position clothing allowance DTO
     */
    PositionClothingAllowanceDTO deactivatePositionAllowance(Long allowanceId);

    /**
     * Delete a position clothing allowance.
     * 
     * @param allowanceId The ID of the allowance
     */
    void deletePositionAllowance(Long allowanceId);

    /**
     * Check if a position clothing allowance is in use.
     * 
     * @param allowanceId The ID of the allowance
     * @return true if the allowance is in use, false otherwise
     */
    boolean isPositionAllowanceInUse(Long allowanceId);
}
