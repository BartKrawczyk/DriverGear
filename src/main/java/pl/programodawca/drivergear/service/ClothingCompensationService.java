package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.model.ClothingCompensation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing clothing compensations.
 */
public interface ClothingCompensationService {
    /**
     * Create compensation records for all eligible assignments.
     * This method finds all assignments marked as eligible for compensation
     * and creates a compensation record for each one.
     * 
     * @return The number of compensation records created
     */
    int createCompensationsForEligibleAssignments();
    /**
     * Calculate and create a new compensation for a clothing assignment.
     * 
     * @param employeeId The ID of the employee
     * @param assignmentId The ID of the clothing assignment
     * @return The created compensation DTO
     */
    ClothingCompensationDTO calculateAndCreateCompensation(Long employeeId, Long assignmentId);

    /**
     * Approve a compensation.
     * 
     * @param compensationId The ID of the compensation
     * @return The updated compensation DTO
     */
    ClothingCompensationDTO approveCompensation(Long compensationId);

    /**
     * Mark a compensation as paid.
     * 
     * @param compensationId The ID of the compensation
     * @return The updated compensation DTO
     */
    ClothingCompensationDTO markAsPaid(Long compensationId);

    /**
     * Get pending compensations for an employee.
     * 
     * @param employeeId The ID of the employee
     * @return A list of pending compensation DTOs
     */
    List<ClothingCompensationDTO> getPendingCompensations(Long employeeId);

    /**
     * Calculate the total amount of pending compensations for an employee.
     * 
     * @param employeeId The ID of the employee
     * @return The total amount of pending compensations
     */
    BigDecimal calculateTotalPendingCompensations(Long employeeId);

    /**
     * Cancel a compensation.
     * 
     * @param compensationId The ID of the compensation
     * @param reason The reason for cancellation
     * @return The updated compensation DTO
     */
    ClothingCompensationDTO cancelCompensation(Long compensationId, String reason);

    /**
     * Get the compensation history for an employee.
     * 
     * @param employeeId The ID of the employee
     * @return A list of compensation DTOs
     */
    List<ClothingCompensationDTO> getEmployeeCompensationHistory(Long employeeId);

    /**
     * Get compensations for a clothing assignment.
     * 
     * @param assignmentId The ID of the clothing assignment
     * @return A list of compensation DTOs
     */
    List<ClothingCompensationDTO> getCompensationsByAssignmentId(Long assignmentId);

    /**
     * Get compensations for a position.
     * 
     * @param positionId The ID of the position
     * @return A list of compensation DTOs
     */
    List<ClothingCompensationDTO> getCompensationsByPositionId(Long positionId);

    /**
     * Get compensations for a department.
     * 
     * @param departmentId The ID of the department
     * @return A list of compensation DTOs
     */
    List<ClothingCompensationDTO> getCompensationsByDepartmentId(Long departmentId);

    /**
     * Mark all pending compensations for an employee as paid in a single transaction.
     * 
     * @param employeeId The ID of the employee
     * @return The number of compensations marked as paid and the total amount
     */
    Map<String, Object> markAllPendingAsPaid(Long employeeId);
}
