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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
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
}