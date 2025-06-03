package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingTypeEntitlementDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.dto.EmployeeEntitlementDTO;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;
import pl.programodawca.drivergear.service.EmployeeEntitlementService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.PositionClothingAllowanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the EmployeeEntitlementService interface.
 * Calculates dynamic clothing entitlements for employees based on their position,
 * current assignments, and compensation eligibility.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeEntitlementServiceImpl implements EmployeeEntitlementService {

    private final EmployeeService employeeService;
    private final PositionClothingAllowanceService positionAllowanceService;
    private final ClothingAssignmentService clothingAssignmentService;
    private final ClothingCompensationService clothingCompensationService;

    @Override
    public EmployeeEntitlementDTO calculateCurrentEntitlement(Long employeeId) {
        // Retrieve employee data
        EmployeeDTO employee = employeeService.findById(employeeId);
        if (employee == null) {
            throw new ResourceNotFoundException("Employee", "id", employeeId);
        }

        // Retrieve position clothing allowances for the employee's position
        List<PositionClothingAllowanceDTO> positionAllowances = 
            positionAllowanceService.getPositionAllowancesByPositionId(employee.getPositionId());

        // Retrieve all clothing assignments for the employee
        List<ClothingAssignmentDTO> allAssignments = 
            clothingAssignmentService.getAssignmentsByEmployeeId(employeeId);

        // Group assignments by clothing type
        Map<Long, List<ClothingAssignmentDTO>> assignmentsByClothingType = 
            groupAssignmentsByClothingType(allAssignments);

        // Calculate entitlements for each clothing type
        List<ClothingTypeEntitlementDTO> clothingEntitlements = 
            calculateClothingTypeEntitlements(positionAllowances, assignmentsByClothingType, employee.getHireDate());

        // Build and return the employee entitlement DTO
        return EmployeeEntitlementDTO.builder()
            .employeeId(employee.getId())
            .employeeName(employee.getFullName())
            .clothingEntitlements(clothingEntitlements)
            .build();
    }

    /**
     * Group clothing assignments by clothing type ID.
     * 
     * @param assignments The list of clothing assignments
     * @return A map of clothing type ID to list of assignments
     */
    private Map<Long, List<ClothingAssignmentDTO>> groupAssignmentsByClothingType(
            List<ClothingAssignmentDTO> assignments) {

        Map<Long, List<ClothingAssignmentDTO>> result = new HashMap<>();

        for (ClothingAssignmentDTO assignment : assignments) {
            if (assignment.getClothingTypeId() != null) {
                result.computeIfAbsent(assignment.getClothingTypeId(), k -> new ArrayList<>())
                    .add(assignment);
            }
        }

        return result;
    }

    /**
     * Calculate entitlements for each clothing type based on position allowances and assignments.
     * 
     * @param positionAllowances The list of position clothing allowances
     * @param assignmentsByClothingType The map of clothing type ID to list of assignments
     * @param hireDate The employee's hire date
     * @return A list of clothing type entitlement DTOs
     */
    private List<ClothingTypeEntitlementDTO> calculateClothingTypeEntitlements(
            List<PositionClothingAllowanceDTO> positionAllowances,
            Map<Long, List<ClothingAssignmentDTO>> assignmentsByClothingType,
            LocalDate hireDate) {

        LocalDate today = LocalDate.now();
        List<ClothingTypeEntitlementDTO> result = new ArrayList<>();

        // Process each position allowance
        for (PositionClothingAllowanceDTO allowance : positionAllowances) {
            // Process each clothing item in the allowance
            for (PositionClothingItemDTO clothingItem : allowance.getClothingItems()) {
                Long clothingTypeId = clothingItem.getClothingTypeId();
                List<ClothingAssignmentDTO> typeAssignments = 
                    assignmentsByClothingType.getOrDefault(clothingTypeId, new ArrayList<>());

                // Calculate issued quantity (issued and still valid)
                int issuedQuantity = calculateIssuedQuantity(typeAssignments, today);

                // Calculate pending quantity (assigned but not issued)
                int pendingQuantity = calculatePendingQuantity(typeAssignments, today, hireDate);

                // Calculate expired quantity (expired and not compensated)
                int expiredQuantity = calculateExpiredQuantity(typeAssignments, today);

                // Determine last issued date and next renewal date
                LocalDate lastIssuedDate = findLastIssuedDate(typeAssignments);
                LocalDate nextRenewalDate = calculateNextRenewalDate(
                    hireDate, clothingItem.getValidityPeriod());

                // Find the most recent expiry date of expired assignments
                LocalDate lastExpiredDate = findLastExpiredDate(typeAssignments, today);

                // Determine compensation eligibility and value
                boolean eligibleForCompensation = isEligibleForCompensation(typeAssignments);
                BigDecimal compensationValue = clothingItem.getCompensationAmount();

                // Build the clothing type entitlement DTO
                ClothingTypeEntitlementDTO entitlement = ClothingTypeEntitlementDTO.builder()
                    .clothingTypeId(clothingTypeId)
                    .clothingTypeName(clothingItem.getClothingTypeName())
                    .standardQuantity(clothingItem.getQuantity())
                    .validityMonths(clothingItem.getValidityPeriod())
                    .issuedQuantity(issuedQuantity)
                    .pendingQuantity(pendingQuantity)
                    .expiredQuantity(expiredQuantity)
                    .lastIssuedDate(lastIssuedDate)
                    .nextRenewalDate(nextRenewalDate)
                    .lastExpiredDate(lastExpiredDate)
                    .eligibleForCompensation(eligibleForCompensation)
                    .compensationValue(compensationValue)
                    .build();

                result.add(entitlement);
            }
        }

        return result;
    }

    /**
     * Calculate the quantity of issued and still valid assignments.
     * 
     * @param assignments The list of assignments for a clothing type
     * @param today The current date
     * @return The quantity of issued and still valid assignments
     */
    private int calculateIssuedQuantity(List<ClothingAssignmentDTO> assignments, LocalDate today) {
        return assignments.stream()
            .filter(a -> a.getStatus() == AssignmentStatus.ISSUED 
                && Boolean.TRUE.equals(a.getIssuedToEmployee())
                && a.getExpiryDate() != null 
                && a.getExpiryDate().isAfter(today))
            .mapToInt(ClothingAssignmentDTO::getQuantity)
            .sum();
    }

    /**
     * Calculate the quantity of pending assignments (assigned but not issued).
     * Only counts assignments as pending if today is on or after the hire date.
     * 
     * @param assignments The list of assignments for a clothing type
     * @param today The current date
     * @param hireDate The employee's hire date
     * @return The quantity of pending assignments
     */
    private int calculatePendingQuantity(List<ClothingAssignmentDTO> assignments, LocalDate today, LocalDate hireDate) {
        // Only count assignments as pending if today is on or after the hire date
        if (today.isBefore(hireDate)) {
            return 0; // Not eligible for issuance yet
        }

        // Debug logging to show which assignments are being counted
        System.out.println("[DEBUG_LOG] Calculating pending quantity for " + assignments.size() + " assignments");

        List<ClothingAssignmentDTO> pendingAssignments = assignments.stream()
            .filter(a -> a.getStatus() != AssignmentStatus.ISSUED 
                && !Boolean.TRUE.equals(a.getIssuedToEmployee())
                && (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ASSIGNED))
            // Remove EXPIRED from valid statuses
            .collect(Collectors.toList());

        // Log the pending assignments
        pendingAssignments.forEach(a -> 
            System.out.println("[DEBUG_LOG] Pending assignment: ID=" + a.getId() + 
                ", Type=" + a.getClothingTypeName() + 
                ", Status=" + a.getStatus() + 
                ", Quantity=" + a.getQuantity()));

        int total = pendingAssignments.stream()
            .mapToInt(ClothingAssignmentDTO::getQuantity)
            .sum();

        System.out.println("[DEBUG_LOG] Total pending quantity: " + total);

        return total;
    }

    /**
     * Calculate the quantity of expired assignments (expired and not compensated).
     * 
     * @param assignments The list of assignments for a clothing type
     * @param today The current date
     * @return The quantity of expired assignments
     */
    private int calculateExpiredQuantity(List<ClothingAssignmentDTO> assignments, LocalDate today) {
        return assignments.stream()
            .filter(a -> a.getExpiryDate() != null 
                && a.getExpiryDate().isBefore(today)
                && a.getStatus() != AssignmentStatus.COMPENSATED
                && !Boolean.TRUE.equals(a.getIssuedToEmployee()))
            .mapToInt(ClothingAssignmentDTO::getQuantity)
            .sum();
    }

    /**
     * Find the most recent issued date for a clothing type.
     * 
     * @param assignments The list of assignments for a clothing type
     * @return The most recent issued date, or null if no assignments have been issued
     */
    private LocalDate findLastIssuedDate(List<ClothingAssignmentDTO> assignments) {
        return assignments.stream()
            .filter(a -> a.getIssuedDate() != null)
            .map(ClothingAssignmentDTO::getIssuedDate)
            .max(LocalDate::compareTo)
            .orElse(null);
    }

    /**
     * Calculate the next renewal date based on the hire date and validity period.
     * This method finds the next anniversary of the hire date after today,
     * using the validity period to determine the cycle length.
     * 
     * @param hireDate The employee's hire date
     * @param validityPeriod The validity period in months
     * @return The next renewal date, or null if there is no hire date
     */
    private LocalDate calculateNextRenewalDate(LocalDate hireDate, Integer validityPeriod) {
        if (hireDate == null || validityPeriod == null) {
            return null;
        }

        // Calculate the next anniversary of hire date after today
        LocalDate today = LocalDate.now();
        LocalDate nextRenewalDate = hireDate;

        while (nextRenewalDate.isBefore(today) || nextRenewalDate.isEqual(today)) {
            nextRenewalDate = nextRenewalDate.plusMonths(validityPeriod);
        }

        return nextRenewalDate;
    }

    /**
     * Determine if a clothing type is eligible for compensation.
     * 
     * @param assignments The list of assignments for a clothing type
     * @return true if any assignment is eligible for compensation, false otherwise
     */
    private boolean isEligibleForCompensation(List<ClothingAssignmentDTO> assignments) {
        return assignments.stream()
            .anyMatch(a -> Boolean.TRUE.equals(a.getEligibleForCompensation()) || 
                     (a.getStatus() == AssignmentStatus.EXPIRED && 
                      !Boolean.TRUE.equals(a.getIssuedToEmployee())));
    }

    /**
     * Find the most recent expiry date of expired assignments.
     * 
     * @param assignments The list of assignments for a clothing type
     * @param today The current date
     * @return The most recent expiry date, or null if no assignments have expired
     */
    private LocalDate findLastExpiredDate(List<ClothingAssignmentDTO> assignments, LocalDate today) {
        return assignments.stream()
            .filter(a -> a.getExpiryDate() != null 
                && a.getExpiryDate().isBefore(today)
                && a.getStatus() != AssignmentStatus.COMPENSATED
                && !Boolean.TRUE.equals(a.getIssuedToEmployee()))
            .map(ClothingAssignmentDTO::getExpiryDate)
            .max(LocalDate::compareTo)
            .orElse(null);
    }
}
