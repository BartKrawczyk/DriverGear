package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.dto.ClothingTypeEntitlementDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.dto.EmployeeEntitlementDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.CompensationStatus;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.WarehouseInventory;
import pl.programodawca.drivergear.repository.ClothingTypeRepository;
import pl.programodawca.drivergear.repository.WarehouseInventoryRepository;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.EmployeeEntitlementService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.PositionService;
import pl.programodawca.drivergear.service.WarehouseInventoryService;
import java.util.Optional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/warehouse/employees")
@RequiredArgsConstructor
public class WarehouseEmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseEmployeeController.class);

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final ClothingAssignmentService clothingAssignmentService;
    private final ClothingCompensationService clothingCompensationService;
    private final WarehouseInventoryService warehouseInventoryService;
    private final ClothingTypeRepository clothingTypeRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final EmployeeEntitlementService employeeEntitlementService;

    @GetMapping
    public String listEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long positionId,
            Model model) {

        // Get all employees with optional filtering
        Page<EmployeeDTO> employeesPage = employeeService.findByFilters(
            firstName, lastName, employeeNumber, departmentId, positionId, Pageable.unpaged());

        model.addAttribute("employees", employeesPage.getContent());
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        model.addAttribute("positions", positionService.findActivePositions());

        // Add filter values to model for form persistence
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("employeeNumber", employeeNumber);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("positionId", positionId);

        return "warehouse/employees/employee-list";
    }

    @GetMapping("/{id}")
    public String showEmployeeDetails(@PathVariable Long id, 
                                     Model model, 
                                     RedirectAttributes redirectAttributes) {
        try {
            logger.info("Showing employee details for employee ID: {}", id);

            // Get employee details
            EmployeeDTO employee = employeeService.findById(id);
            model.addAttribute("employee", employee);
            logger.info("Employee found: {} {}", employee.getFirstName(), employee.getLastName());

            // Get dynamic entitlement data
            EmployeeEntitlementDTO entitlementData = employeeEntitlementService.calculateCurrentEntitlement(id);
            model.addAttribute("entitlementData", entitlementData);
            logger.info("Retrieved entitlement data for employee ID: {}", id);

            // Create a map to store stock quantities for each clothing type
            Map<Long, Integer> stockQuantities = new HashMap<>();
            for (ClothingTypeEntitlementDTO entitlement : entitlementData.getClothingEntitlements()) {
                // Get the stock quantity for this clothing type
                int stockQuantity = 0;
                try {
                    // Get the ClothingType object from the repository
                    ClothingType clothingType = clothingTypeRepository.findById(entitlement.getClothingTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("ClothingType not found"));

                    // Check if the item is in stock
                    Optional<WarehouseInventory> inventory = warehouseInventoryRepository
                            .findByClothingType(clothingType);
                    if (inventory.isPresent()) {
                        stockQuantity = inventory.get().getQuantity();
                    }
                } catch (Exception e) {
                    logger.error("Error retrieving stock for type {}", entitlement.getClothingTypeId(), e);
                }
                stockQuantities.put(entitlement.getClothingTypeId(), stockQuantity);
                logger.info("Stock quantity for clothing type ID {}: {}", entitlement.getClothingTypeId(), stockQuantity);
            }

            // Ensure all clothing types in entitlementData have an entry in the stockQuantities map
            for (ClothingTypeEntitlementDTO entitlement : entitlementData.getClothingEntitlements()) {
                stockQuantities.putIfAbsent(entitlement.getClothingTypeId(), 0);
            }

            model.addAttribute("stockQuantities", stockQuantities);

            // For backward compatibility, keep these attributes in the model
            // but they can be removed in the future when UI is updated
            List<ClothingAssignmentDTO> allAssignments = clothingAssignmentService.getAssignmentsByEmployeeId(id);

            // Get clothing assignments eligible for issue (status = PENDING or ASSIGNED and not issued to employee)
            List<ClothingAssignmentDTO> pendingAssignments = allAssignments.stream()
                .filter(a -> a.getStatus() != AssignmentStatus.ISSUED 
                    && !Boolean.TRUE.equals(a.getIssuedToEmployee())
                    && (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ASSIGNED))
                .toList();
            model.addAttribute("pendingAssignments", pendingAssignments);

            // Get issued clothing items (either status = ISSUED or issuedToEmployee = true)
            List<ClothingAssignmentDTO> issuedItems = allAssignments.stream()
                .filter(a -> a.getStatus() == AssignmentStatus.ISSUED || Boolean.TRUE.equals(a.getIssuedToEmployee()))
                .toList();
            model.addAttribute("issuedItems", issuedItems);

            // Calculate total pending compensation
            BigDecimal totalPendingCompensation = 
                clothingCompensationService.calculateTotalPendingCompensations(id);
            model.addAttribute("totalPendingCompensation", totalPendingCompensation);

            // Get employee compensation history - filter for PAID compensations
            List<ClothingCompensationDTO> paidCompensations = clothingCompensationService.getEmployeeCompensationHistory(id)
                .stream()
                .filter(c -> c.getStatus() == CompensationStatus.PAID)
                .toList();
            model.addAttribute("paidCompensations", paidCompensations);

            // Calculate total paid compensation amount
            BigDecimal totalPaidCompensation = paidCompensations.stream()
                .map(ClothingCompensationDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("totalPaidCompensation", totalPaidCompensation);

            return "warehouse/employees/employee-details";
        } catch (ResourceNotFoundException e) {
            logger.error("Employee not found with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/warehouse/employees";
        }
    }

    @PostMapping("/issue-clothing/{clothingTypeId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> issueClothing(
            @PathVariable Long clothingTypeId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            // Extract employeeId from request body
            Long employeeId = Long.valueOf(requestBody.get("employeeId").toString());
            logger.info("Issuing clothing type ID {} to employee ID {}", clothingTypeId, employeeId);

            // Retrieve the list of assignments for the employee
            List<ClothingAssignmentDTO> assignments = clothingAssignmentService.getAssignmentsByEmployeeId(employeeId);

            // Filter to get the oldest valid and pending assignment for the given clothingTypeId
            ClothingAssignmentDTO assignmentToIssue = assignments.stream()
                .filter(a -> a.getClothingTypeId().equals(clothingTypeId))
                .filter(a -> a.getStatus() != AssignmentStatus.ISSUED 
                    && !Boolean.TRUE.equals(a.getIssuedToEmployee())
                    && (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ASSIGNED))
                .min((a1, a2) -> a1.getAssignmentDate().compareTo(a2.getAssignmentDate()))
                .orElse(null);

            if (assignmentToIssue == null) {
                logger.warn("No valid pending assignment found for clothing type ID {} and employee ID {}", 
                    clothingTypeId, employeeId);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No valid pending assignment found for this clothing type");
                return ResponseEntity.ok(response);
            }

            // Mark the assignment as issued
            ClothingAssignmentDTO updatedAssignment = clothingAssignmentService.markAsIssued(
                assignmentToIssue.getId(), LocalDate.now());

            logger.info("Issued clothing item: {} to employee: {}", 
                updatedAssignment.getClothingTypeName(), updatedAssignment.getEmployeeName());

            // Explicitly refresh the entitlement data to ensure the view shows the latest data
            // This is important to make sure the expired quantity is updated correctly
            EmployeeEntitlementDTO refreshedEntitlementData = employeeEntitlementService.calculateCurrentEntitlement(employeeId);
            logger.info("Refreshed entitlement data for employee ID {}: {} expired items, {} pending items", 
                employeeId, 
                refreshedEntitlementData.getClothingEntitlements().stream().mapToInt(e -> e.getExpiredQuantity()).sum(),
                refreshedEntitlementData.getClothingEntitlements().stream().mapToInt(e -> e.getPendingQuantity()).sum());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Clothing item issued successfully");
            response.put("refreshRequired", true); // Signal to the frontend that it should refresh the page
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error issuing clothing", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error issuing clothing: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/pay-compensation/{employeeId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payCompensation(
            @PathVariable Long employeeId,
            @RequestBody(required = false) Map<String, Object> requestBody) {
        try {
            EmployeeDTO employee = employeeService.findById(employeeId);
            BigDecimal amount;
            String message;

            // Check if a specific clothing type is requested for compensation
            if (requestBody != null && requestBody.containsKey("clothingTypeId")) {
                Long clothingTypeId = Long.valueOf(requestBody.get("clothingTypeId").toString());
                logger.info("Processing compensation for clothing type ID {} and employee ID {}", 
                    clothingTypeId, employeeId);

                // Get assignments for this employee and clothing type that are eligible for compensation
                List<ClothingAssignmentDTO> assignments = clothingAssignmentService.getAssignmentsByEmployeeId(employeeId)
                    .stream()
                    .filter(a -> a.getClothingTypeId().equals(clothingTypeId))
                    .filter(a -> Boolean.TRUE.equals(a.getEligibleForCompensation()))
                    .toList();

                if (assignments.isEmpty()) {
                    logger.warn("No eligible assignments found for compensation for clothing type ID {} and employee ID {}", 
                        clothingTypeId, employeeId);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No eligible assignments found for compensation");
                    return ResponseEntity.ok(response);
                }

                // Process compensation for each eligible assignment
                amount = BigDecimal.ZERO;
                for (ClothingAssignmentDTO assignment : assignments) {
                    ClothingCompensationDTO compensation = 
                        clothingCompensationService.calculateAndCreateCompensation(employeeId, assignment.getId());
                    clothingCompensationService.markAsPaid(compensation.getId());
                    amount = amount.add(compensation.getAmount());
                }

                message = "Compensation for " + assignments.get(0).getClothingTypeName() + " processed successfully";
            } else {
                // Process all pending compensations
                logger.info("Processing all pending compensations for employee ID {}", employeeId);

                Map<String, Object> result = clothingCompensationService.markAllPendingAsPaid(employeeId);
                int paidCount = (int) result.get("count");
                BigDecimal totalAmount = (BigDecimal) result.get("totalAmount");

                if (paidCount == 0) {
                    logger.warn("No pending compensations found for employee ID {}", employeeId);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "No pending compensations found");
                    return ResponseEntity.ok(response);
                }

                amount = totalAmount;
                message = paidCount + " pending compensations processed successfully";
            }

            logger.info("Processed compensation of {} for employee: {}", amount, employee.getFullName());

            // Explicitly refresh the entitlement data to ensure the view shows the latest data
            // This is important to make sure the expired quantity is updated correctly
            EmployeeEntitlementDTO refreshedEntitlementData = employeeEntitlementService.calculateCurrentEntitlement(employeeId);
            logger.info("Refreshed entitlement data for employee ID {}: {} expired items, {} pending items", 
                employeeId, 
                refreshedEntitlementData.getClothingEntitlements().stream().mapToInt(e -> e.getExpiredQuantity()).sum(),
                refreshedEntitlementData.getClothingEntitlements().stream().mapToInt(e -> e.getPendingQuantity()).sum());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", message);
            response.put("amount", amount);
            response.put("refreshRequired", true); // Signal to the frontend that it should refresh the page
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing compensation payment", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error processing compensation payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
