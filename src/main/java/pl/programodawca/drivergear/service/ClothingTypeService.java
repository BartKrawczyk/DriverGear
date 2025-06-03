package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.ClothingTypeDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;

import java.util.List;

public interface ClothingTypeService {
    /**
     * Find all clothing types
     * @return List of all clothing types
     */
    List<ClothingTypeDTO> findAllClothingTypes();
    
    /**
     * Find all active clothing types
     * @return List of active clothing types
     */
    List<ClothingTypeDTO> findActiveClothingTypes();
    
    /**
     * Find clothing type by ID
     * @param id Clothing type ID
     * @return Clothing type DTO
     * @throws ResourceNotFoundException if clothing type not found
     */
    ClothingTypeDTO findById(Long id) throws ResourceNotFoundException;
    
    /**
     * Create a new clothing type
     * @param clothingTypeDTO Clothing type data
     * @return Created clothing type DTO
     */
    ClothingTypeDTO createClothingType(ClothingTypeDTO clothingTypeDTO);
    
    /**
     * Update an existing clothing type
     * @param id Clothing type ID
     * @param clothingTypeDTO Updated clothing type data
     * @return Updated clothing type DTO
     * @throws ResourceNotFoundException if clothing type not found
     */
    ClothingTypeDTO updateClothingType(Long id, ClothingTypeDTO clothingTypeDTO) throws ResourceNotFoundException;
    
    /**
     * Delete a clothing type
     * @param id Clothing type ID
     * @throws ResourceNotFoundException if clothing type not found
     */
    void deleteClothingType(Long id) throws ResourceNotFoundException;
    
    /**
     * Check if a clothing type is in use
     * @param id Clothing type ID
     * @return true if clothing type is in use, false otherwise
     */
    boolean isClothingTypeInUse(Long id);
}