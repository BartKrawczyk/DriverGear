package pl.programodawca.drivergear.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;

import java.time.LocalDate;

/**
 * Service interface for warehouse history views.
 */
public interface WarehouseHistoryService {

    /**
     * Get issued clothing history with pagination and filtering.
     *
     * @param firstName The first name filter (optional)
     * @param lastName The last name filter (optional)
     * @param employeeNumber The employee number filter (optional)
     * @param clothingTypeId The clothing type ID filter (optional)
     * @param startDate The start date filter (optional)
     * @param endDate The end date filter (optional)
     * @param pageable The pagination information
     * @return A page of clothing assignment DTOs
     */
    Page<ClothingAssignmentDTO> getIssuedClothingHistory(
            String firstName,
            String lastName,
            String employeeNumber,
            Long clothingTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    /**
     * Get paid compensation history with pagination and filtering.
     *
     * @param firstName The first name filter (optional)
     * @param lastName The last name filter (optional)
     * @param employeeNumber The employee number filter (optional)
     * @param clothingTypeId The clothing type ID filter (optional)
     * @param startDate The start date filter (optional)
     * @param endDate The end date filter (optional)
     * @param pageable The pagination information
     * @return A page of clothing compensation DTOs
     */
    Page<ClothingCompensationDTO> getPaidCompensationHistory(
            String firstName,
            String lastName,
            String employeeNumber,
            Long clothingTypeId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);
}