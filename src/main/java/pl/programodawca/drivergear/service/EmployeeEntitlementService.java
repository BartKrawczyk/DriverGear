package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.EmployeeEntitlementDTO;

/**
 * Service interface for calculating dynamic clothing entitlements for employees.
 * This service aggregates data from various sources to determine what clothing items
 * an employee is currently entitled to, what has been issued, and what is eligible for compensation.
 */
public interface EmployeeEntitlementService {
    
    /**
     * Calculate the current clothing entitlement for an employee.
     * This method retrieves the employee's position clothing allowance, current assignments,
     * and determines the dynamic entitlement data for each clothing type.
     * 
     * @param employeeId The ID of the employee
     * @return A DTO containing the employee's current clothing entitlement data
     */
    EmployeeEntitlementDTO calculateCurrentEntitlement(Long employeeId);
}