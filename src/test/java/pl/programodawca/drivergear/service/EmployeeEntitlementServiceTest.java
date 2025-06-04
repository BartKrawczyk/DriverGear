package pl.programodawca.drivergear.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingTypeEntitlementDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.dto.EmployeeEntitlementDTO;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.service.impl.EmployeeEntitlementServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeEntitlementServiceTest {
    @Mock
    private EmployeeService employeeService;

    @Mock
    private PositionClothingAllowanceService positionClothingAllowanceService;

    @Mock
    private ClothingAssignmentService clothingAssignmentService;

    @Mock
    private ClothingCompensationService clothingCompensationService;

    @InjectMocks
    private EmployeeEntitlementServiceImpl employeeEntitlementService;

    /**
     * Test that entitlements are correctly calculated for an employee with a hire date in the past.
     * Pending quantity should be calculated based on the hire date.
     */
    @Test
    void shouldCalculateEntitlementsCorrectlyForPastHireDate() {
        // given
        Long employeeId = 1L;

        // Set hire date to 30 days in the past
        LocalDate hireDate = LocalDate.now().minusDays(30);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setFirstName("Test");
        employeeDTO.setLastName("Employee");
        employeeDTO.setPositionId(1L);
        employeeDTO.setHireDate(hireDate);

        PositionClothingAllowanceDTO allowanceDTO = new PositionClothingAllowanceDTO();
        allowanceDTO.setId(1L);
        allowanceDTO.setPositionId(1L);

        PositionClothingItemDTO itemDTO = new PositionClothingItemDTO();
        itemDTO.setClothingTypeId(1L);
        itemDTO.setClothingTypeName("Test Clothing");
        itemDTO.setQuantity(2);
        itemDTO.setValidityPeriod(12);

        List<PositionClothingItemDTO> items = new ArrayList<>();
        items.add(itemDTO);
        allowanceDTO.setClothingItems(items);

        List<PositionClothingAllowanceDTO> allowances = new ArrayList<>();
        allowances.add(allowanceDTO);

        ClothingAssignmentDTO assignmentDTO = new ClothingAssignmentDTO();
        assignmentDTO.setId(1L);
        assignmentDTO.setEmployeeId(employeeId);
        assignmentDTO.setPositionClothingAllowanceId(1L);
        assignmentDTO.setClothingTypeId(1L);
        assignmentDTO.setQuantity(2);
        assignmentDTO.setStatus(AssignmentStatus.PENDING);
        assignmentDTO.setIssuedToEmployee(false);
        assignmentDTO.setAssignmentDate(hireDate);

        List<ClothingAssignmentDTO> assignments = new ArrayList<>();
        assignments.add(assignmentDTO);

        // Mock service calls
        when(employeeService.findById(employeeId)).thenReturn(employeeDTO);
        when(positionClothingAllowanceService.getPositionAllowancesByPositionId(anyLong())).thenReturn(allowances);
        when(clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)).thenReturn(assignments);

        // when
        EmployeeEntitlementDTO result = employeeEntitlementService.calculateCurrentEntitlement(employeeId);

        // then
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("Test Employee", result.getEmployeeName());

        List<ClothingTypeEntitlementDTO> entitlements = result.getClothingEntitlements();
        assertNotNull(entitlements);
        assertEquals(1, entitlements.size());

        ClothingTypeEntitlementDTO entitlement = entitlements.get(0);
        assertEquals(1L, entitlement.getClothingTypeId());
        assertEquals("Test Clothing", entitlement.getClothingTypeName());
        assertEquals(2, entitlement.getStandardQuantity());
        assertEquals(0, entitlement.getIssuedQuantity());
        assertEquals(2, entitlement.getPendingQuantity()); // Should be 2 because hire date is in the past
    }

    /**
     * Test that entitlements are correctly calculated for an employee with a hire date in the future.
     * Pending quantity should be 0 because the hire date is in the future.
     */
    @Test
    void shouldCalculateEntitlementsCorrectlyForFutureHireDate() {
        // given
        Long employeeId = 1L;

        // Set hire date to 30 days in the future
        LocalDate hireDate = LocalDate.now().plusDays(30);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setFirstName("Test");
        employeeDTO.setLastName("Employee");
        employeeDTO.setPositionId(1L);
        employeeDTO.setHireDate(hireDate);

        PositionClothingAllowanceDTO allowanceDTO = new PositionClothingAllowanceDTO();
        allowanceDTO.setId(1L);
        allowanceDTO.setPositionId(1L);

        PositionClothingItemDTO itemDTO = new PositionClothingItemDTO();
        itemDTO.setClothingTypeId(1L);
        itemDTO.setClothingTypeName("Test Clothing");
        itemDTO.setQuantity(2);
        itemDTO.setValidityPeriod(12);

        List<PositionClothingItemDTO> items = new ArrayList<>();
        items.add(itemDTO);
        allowanceDTO.setClothingItems(items);

        List<PositionClothingAllowanceDTO> allowances = new ArrayList<>();
        allowances.add(allowanceDTO);

        ClothingAssignmentDTO assignmentDTO = new ClothingAssignmentDTO();
        assignmentDTO.setId(1L);
        assignmentDTO.setEmployeeId(employeeId);
        assignmentDTO.setPositionClothingAllowanceId(1L);
        assignmentDTO.setClothingTypeId(1L);
        assignmentDTO.setQuantity(2);
        assignmentDTO.setStatus(AssignmentStatus.PENDING);
        assignmentDTO.setIssuedToEmployee(false);
        assignmentDTO.setAssignmentDate(hireDate);

        List<ClothingAssignmentDTO> assignments = new ArrayList<>();
        assignments.add(assignmentDTO);

        // Mock service calls
        when(employeeService.findById(employeeId)).thenReturn(employeeDTO);
        when(positionClothingAllowanceService.getPositionAllowancesByPositionId(anyLong())).thenReturn(allowances);
        when(clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)).thenReturn(assignments);

        // when
        EmployeeEntitlementDTO result = employeeEntitlementService.calculateCurrentEntitlement(employeeId);

        // then
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("Test Employee", result.getEmployeeName());

        List<ClothingTypeEntitlementDTO> entitlements = result.getClothingEntitlements();
        assertNotNull(entitlements);
        assertEquals(1, entitlements.size());

        ClothingTypeEntitlementDTO entitlement = entitlements.get(0);
        assertEquals(1L, entitlement.getClothingTypeId());
        assertEquals("Test Clothing", entitlement.getClothingTypeName());
        assertEquals(2, entitlement.getStandardQuantity());
        assertEquals(0, entitlement.getIssuedQuantity());
        assertEquals(0, entitlement.getPendingQuantity()); // Should be 0 because hire date is in the future
    }

    /**
     * Test that expired assignments are not visible after compensation payout.
     * This test verifies that:
     * 1. Expired assignments are not visible in "Przeterminowana ilość" after payout
     * 2. New pending assignments are created and visible after payout
     * 3. Issuing clothing only affects pending quantities, not expired quantities
     */
    @Test
    void shouldHandlePostCompensationLogicCorrectly() {
        // given
        Long employeeId = 1L;
        LocalDate hireDate = LocalDate.now().minusMonths(13); // Hire date over a year ago
        LocalDate today = LocalDate.now();

        // Create employee
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setFirstName("Test");
        employeeDTO.setLastName("Employee");
        employeeDTO.setPositionId(1L);
        employeeDTO.setHireDate(hireDate);

        // Create position allowance
        PositionClothingAllowanceDTO allowanceDTO = new PositionClothingAllowanceDTO();
        allowanceDTO.setId(1L);
        allowanceDTO.setPositionId(1L);

        // Create clothing item
        PositionClothingItemDTO itemDTO = new PositionClothingItemDTO();
        itemDTO.setClothingTypeId(1L);
        itemDTO.setClothingTypeName("Test Clothing");
        itemDTO.setQuantity(2);
        itemDTO.setValidityPeriod(12); // 12 months validity
        itemDTO.setCompensationAmount(BigDecimal.valueOf(100));

        List<PositionClothingItemDTO> items = new ArrayList<>();
        items.add(itemDTO);
        allowanceDTO.setClothingItems(items);

        List<PositionClothingAllowanceDTO> allowances = new ArrayList<>();
        allowances.add(allowanceDTO);

        // Create expired assignment (over a year old)
        ClothingAssignmentDTO expiredAssignmentDTO = new ClothingAssignmentDTO();
        expiredAssignmentDTO.setId(1L);
        expiredAssignmentDTO.setEmployeeId(employeeId);
        expiredAssignmentDTO.setPositionClothingAllowanceId(1L);
        expiredAssignmentDTO.setClothingTypeId(1L);
        expiredAssignmentDTO.setClothingTypeName("Test Clothing");
        expiredAssignmentDTO.setQuantity(2);
        expiredAssignmentDTO.setStatus(AssignmentStatus.EXPIRED);
        expiredAssignmentDTO.setIssuedToEmployee(false);
        expiredAssignmentDTO.setEligibleForCompensation(true);
        expiredAssignmentDTO.setAssignmentDate(hireDate);
        expiredAssignmentDTO.setExpiryDate(hireDate.plusMonths(12));

        List<ClothingAssignmentDTO> initialAssignments = new ArrayList<>();
        initialAssignments.add(expiredAssignmentDTO);

        // Mock initial service calls
        when(employeeService.findById(employeeId)).thenReturn(employeeDTO);
        when(positionClothingAllowanceService.getPositionAllowancesByPositionId(anyLong())).thenReturn(allowances);
        when(clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)).thenReturn(initialAssignments);

        // STEP 1: Calculate initial entitlement (before compensation)
        EmployeeEntitlementDTO initialResult = employeeEntitlementService.calculateCurrentEntitlement(employeeId);

        // Verify initial state
        assertNotNull(initialResult);
        List<ClothingTypeEntitlementDTO> initialEntitlements = initialResult.getClothingEntitlements();
        assertEquals(1, initialEntitlements.size());

        ClothingTypeEntitlementDTO initialEntitlement = initialEntitlements.get(0);
        assertEquals(2, initialEntitlement.getExpiredQuantity()); // Should have 2 expired items
        assertEquals(0, initialEntitlement.getPendingQuantity()); // No pending items yet

        // STEP 2: Simulate compensation payout
        // Create a new assignment that would be created after compensation
        ClothingAssignmentDTO newAssignmentDTO = new ClothingAssignmentDTO();
        newAssignmentDTO.setId(2L);
        newAssignmentDTO.setEmployeeId(employeeId);
        newAssignmentDTO.setPositionClothingAllowanceId(1L);
        newAssignmentDTO.setClothingTypeId(1L);
        newAssignmentDTO.setClothingTypeName("Test Clothing");
        newAssignmentDTO.setQuantity(2);
        newAssignmentDTO.setStatus(AssignmentStatus.PENDING);
        newAssignmentDTO.setIssuedToEmployee(false);
        newAssignmentDTO.setEligibleForCompensation(false);
        newAssignmentDTO.setAssignmentDate(today);
        newAssignmentDTO.setExpiryDate(today.plusMonths(12));

        // Update the expired assignment to be compensated
        ClothingAssignmentDTO compensatedAssignmentDTO = new ClothingAssignmentDTO();
        compensatedAssignmentDTO.setId(1L);
        compensatedAssignmentDTO.setEmployeeId(employeeId);
        compensatedAssignmentDTO.setPositionClothingAllowanceId(1L);
        compensatedAssignmentDTO.setClothingTypeId(1L);
        compensatedAssignmentDTO.setClothingTypeName("Test Clothing");
        compensatedAssignmentDTO.setQuantity(2);
        compensatedAssignmentDTO.setStatus(AssignmentStatus.COMPENSATED); // Now compensated
        compensatedAssignmentDTO.setIssuedToEmployee(false);
        compensatedAssignmentDTO.setEligibleForCompensation(false); // No longer eligible
        compensatedAssignmentDTO.setAssignmentDate(hireDate);
        compensatedAssignmentDTO.setExpiryDate(hireDate.plusMonths(12));

        List<ClothingAssignmentDTO> postCompensationAssignments = new ArrayList<>();
        postCompensationAssignments.add(compensatedAssignmentDTO);
        postCompensationAssignments.add(newAssignmentDTO);

        // Mock service calls after compensation
        when(clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)).thenReturn(postCompensationAssignments);

        // Calculate entitlement after compensation
        EmployeeEntitlementDTO postCompensationResult = employeeEntitlementService.calculateCurrentEntitlement(employeeId);

        // Verify post-compensation state
        assertNotNull(postCompensationResult);
        List<ClothingTypeEntitlementDTO> postCompensationEntitlements = postCompensationResult.getClothingEntitlements();
        assertEquals(1, postCompensationEntitlements.size());

        ClothingTypeEntitlementDTO postCompensationEntitlement = postCompensationEntitlements.get(0);
        assertEquals(0, postCompensationEntitlement.getExpiredQuantity()); // No expired items (they're compensated)
        assertEquals(2, postCompensationEntitlement.getPendingQuantity()); // 2 pending items from new assignment

        // STEP 3: Simulate issuing clothing
        // Update the new assignment to be issued
        ClothingAssignmentDTO issuedAssignmentDTO = new ClothingAssignmentDTO();
        issuedAssignmentDTO.setId(2L);
        issuedAssignmentDTO.setEmployeeId(employeeId);
        issuedAssignmentDTO.setPositionClothingAllowanceId(1L);
        issuedAssignmentDTO.setClothingTypeId(1L);
        issuedAssignmentDTO.setClothingTypeName("Test Clothing");
        issuedAssignmentDTO.setQuantity(2);
        issuedAssignmentDTO.setStatus(AssignmentStatus.ISSUED); // Now issued
        issuedAssignmentDTO.setIssuedToEmployee(true); // Issued to employee
        issuedAssignmentDTO.setEligibleForCompensation(false);
        issuedAssignmentDTO.setAssignmentDate(today);
        issuedAssignmentDTO.setExpiryDate(today.plusMonths(12));
        issuedAssignmentDTO.setIssuedDate(today);

        List<ClothingAssignmentDTO> postIssuanceAssignments = new ArrayList<>();
        postIssuanceAssignments.add(compensatedAssignmentDTO);
        postIssuanceAssignments.add(issuedAssignmentDTO);

        // Mock service calls after issuance
        when(clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)).thenReturn(postIssuanceAssignments);

        // Calculate entitlement after issuance
        EmployeeEntitlementDTO postIssuanceResult = employeeEntitlementService.calculateCurrentEntitlement(employeeId);

        // Verify post-issuance state
        assertNotNull(postIssuanceResult);
        List<ClothingTypeEntitlementDTO> postIssuanceEntitlements = postIssuanceResult.getClothingEntitlements();
        assertEquals(1, postIssuanceEntitlements.size());

        ClothingTypeEntitlementDTO postIssuanceEntitlement = postIssuanceEntitlements.get(0);
        assertEquals(0, postIssuanceEntitlement.getExpiredQuantity()); // Still no expired items
        assertEquals(0, postIssuanceEntitlement.getPendingQuantity()); // No pending items (all issued)
        assertEquals(2, postIssuanceEntitlement.getIssuedQuantity()); // 2 issued items
    }
}
