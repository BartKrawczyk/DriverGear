package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.ClothingType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing clothing assignments to employees.
 */
public interface ClothingAssignmentService {

    /**
     * Create a new clothing assignment for an employee based on a position clothing allowance.
     * 
     * @param employeeId The ID of the employee
     * @param positionClothingAllowanceId The ID of the position clothing allowance
     * @param size The size of the clothing item (optional)
     * @param quantity The quantity of items to assign (default is 1)
     * @param notes Additional notes about the assignment
     * @return The created clothing assignment DTO
     */
    ClothingAssignmentDTO createAssignment(Long employeeId,
                                           Long positionClothingAllowanceId,
                                           ClothingType clothingType,
                                           String size,
                                           Integer quantity,
                                           String notes);

    /**
     * Get a clothing assignment by ID.
     * 
     * @param assignmentId The ID of the assignment
     * @return The clothing assignment DTO
     */
    ClothingAssignmentDTO getAssignmentById(Long assignmentId);

    /**
     * Get all clothing assignments for an employee.
     * 
     * @param employeeId The ID of the employee
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getAssignmentsByEmployeeId(Long employeeId);

    /**
     * Get clothing assignments for an employee with a specific status.
     * 
     * @param employeeId The ID of the employee
     * @param status The status of the assignments
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getAssignmentsByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status);

    /**
     * Get clothing assignments for a position clothing allowance.
     * 
     * @param positionClothingAllowanceId The ID of the position clothing allowance
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getAssignmentsByPositionClothingAllowanceId(Long positionClothingAllowanceId);

    /**
     * Get clothing assignments with a specific status.
     * 
     * @param status The status of the assignments
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getAssignmentsByStatus(AssignmentStatus status);

    /**
     * Get clothing assignments that are active on a specific date.
     * 
     * @param date The date to check (defaults to today if null)
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getActiveAssignmentsOnDate(LocalDate date);

    /**
     * Get clothing assignments that have expired but still have a non-expired status.
     * 
     * @param date The date to check against (defaults to today if null)
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getExpiredAssignments(LocalDate date);

    /**
     * Update the status of a clothing assignment.
     * 
     * @param assignmentId The ID of the assignment
     * @param status The new status
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO updateAssignmentStatus(Long assignmentId, AssignmentStatus status);

    /**
     * Mark a clothing assignment as issued to the employee.
     * 
     * @param assignmentId The ID of the assignment
     * @param issuedDate The date the item was issued (defaults to today if null)
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO markAsIssued(Long assignmentId, LocalDate issuedDate);

    /**
     * Mark a clothing assignment as eligible for compensation.
     * 
     * @param assignmentId The ID of the assignment
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO markAsEligibleForCompensation(Long assignmentId);

    /**
     * Update the notes for a clothing assignment.
     * 
     * @param assignmentId The ID of the assignment
     * @param notes The new notes
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO updateAssignmentNotes(Long assignmentId, String notes);

    /**
     * Cancel a clothing assignment.
     * 
     * @param assignmentId The ID of the assignment
     * @param reason The reason for cancellation (will be added to notes)
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO cancelAssignment(Long assignmentId, String reason);

    /**
     * Automatically update the status of expired assignments.
     * 
     * @return The number of assignments updated
     */
    int updateExpiredAssignments();

    /**
     * Get clothing assignments that are eligible for compensation.
     * 
     * @return A list of clothing assignment DTOs
     */
    List<ClothingAssignmentDTO> getAssignmentsEligibleForCompensation();

    /**
     * Mark a clothing assignment as compensated.
     * 
     * @param assignmentId The ID of the assignment
     * @return The updated clothing assignment DTO
     */
    ClothingAssignmentDTO markAsCompensated(Long assignmentId);
}
