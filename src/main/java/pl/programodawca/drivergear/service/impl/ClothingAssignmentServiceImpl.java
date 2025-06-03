package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.model.*;
import pl.programodawca.drivergear.repository.ClothingAssignmentRepository;
import pl.programodawca.drivergear.repository.EmployeeRepository;
import pl.programodawca.drivergear.repository.PositionClothingAllowanceRepository;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.WarehouseInventoryService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ClothingAssignmentServiceImpl implements ClothingAssignmentService {
    private final ClothingAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionClothingAllowanceRepository positionAllowanceRepository;
    private final WarehouseInventoryService warehouseInventoryService;
    private final ApplicationContext applicationContext;

    @Lazy
    private final ClothingCompensationService clothingCompensationService;

    public ClothingAssignmentServiceImpl(
            ClothingAssignmentRepository assignmentRepository,
            EmployeeRepository employeeRepository,
            PositionClothingAllowanceRepository positionAllowanceRepository,
            WarehouseInventoryService warehouseInventoryService,
            ApplicationContext applicationContext,
            @Lazy ClothingCompensationService clothingCompensationService) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
        this.positionAllowanceRepository = positionAllowanceRepository;
        this.warehouseInventoryService = warehouseInventoryService;
        this.applicationContext = applicationContext;
        this.clothingCompensationService = clothingCompensationService;
    }

    // Add method to get EmployeeService when needed
    private EmployeeService getEmployeeService() {
        return applicationContext.getBean(EmployeeService.class);
    }

    @Override
    public ClothingAssignmentDTO createAssignment(Long employeeId,
                                                  Long positionClothingAllowanceId,
                                                  ClothingType clothingType,
                                                  String size,
                                                  Integer quantity,
                                                  String notes) {

        // Validate employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony"));

        // Validate position allowance
        PositionClothingAllowance positionAllowance = positionAllowanceRepository.findById(positionClothingAllowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + positionClothingAllowanceId + " nie został znaleziony"));

        if (!positionAllowance.getActive()) {
            throw new BusinessException("Przydział odzieżowy dla stanowiska jest nieaktywny");
        }

        // Optional: validate that the given clothingType belongs to the positionClothingAllowance
        boolean typeAllowed = positionAllowance.getClothingItems().stream()
                .anyMatch(item -> item.getClothingType().getId().equals(clothingType.getId()));

        if (!typeAllowed) {
            throw new BusinessException("Typ odzieży nie należy do przydziału stanowiskowego");
        }

        // Create assignment
        ClothingAssignment assignment = new ClothingAssignment();
        assignment.setEmployee(employee);
        assignment.setPositionClothingAllowance(positionAllowance);
        assignment.setClothingType(clothingType); // ✅ tu przypisujemy konkretny typ odzieży

        // Use the employee's hire date as the assignment date
        // This ensures that entitlements are correctly calculated based on the hire date
        LocalDate assignmentDate = employee.getHireDate();
        assignment.setAssignmentDate(assignmentDate);

        // Get validity from matching clothing item
        int validityPeriod = positionAllowance.getClothingItems().stream()
                .filter(item -> item.getClothingType().getId().equals(clothingType.getId()))
                .map(PositionClothingItem::getValidityPeriod)
                .findFirst()
                .orElse(12);

        // Calculate expiry date based on the assignment date (hire date)
        assignment.setExpiryDate(assignmentDate.plusMonths(validityPeriod));
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setSize(size);
        assignment.setQuantity(quantity != null ? quantity : 1);
        assignment.setIssuedToEmployee(false);
        assignment.setEligibleForCompensation(false);
        assignment.setNotes(notes);

        ClothingAssignment saved = assignmentRepository.save(assignment);
        return ClothingAssignmentDTO.fromEntity(saved);
    }

    @Override
    public ClothingAssignmentDTO getAssignmentById(Long assignmentId) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));
        return ClothingAssignmentDTO.fromEntity(assignment);
    }

    @Override
    public List<ClothingAssignmentDTO> getAssignmentsByEmployeeId(Long employeeId) {
        // Validate employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony");
        }

        List<ClothingAssignment> assignments = assignmentRepository.findByEmployeeId(employeeId);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public List<ClothingAssignmentDTO> getAssignmentsByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status) {
        // Validate employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Pracownik o ID " + employeeId + " nie został znaleziony");
        }

        List<ClothingAssignment> assignments = assignmentRepository.findByEmployeeIdAndStatus(employeeId, status);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public List<ClothingAssignmentDTO> getAssignmentsByPositionClothingAllowanceId(Long positionClothingAllowanceId) {
        // Validate position clothing allowance exists
        if (!positionAllowanceRepository.existsById(positionClothingAllowanceId)) {
            throw new EntityNotFoundException("Przydział odzieżowy dla stanowiska o ID " + 
                    positionClothingAllowanceId + " nie został znaleziony");
        }

        List<ClothingAssignment> assignments = assignmentRepository.findByPositionClothingAllowanceId(positionClothingAllowanceId);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public List<ClothingAssignmentDTO> getAssignmentsByStatus(AssignmentStatus status) {
        List<ClothingAssignment> assignments = assignmentRepository.findByStatus(status);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public List<ClothingAssignmentDTO> getActiveAssignmentsOnDate(LocalDate date) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        List<ClothingAssignment> assignments = assignmentRepository.findActiveOnDate(checkDate);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public List<ClothingAssignmentDTO> getExpiredAssignments(LocalDate date) {
        LocalDate checkDate = date != null ? date : LocalDate.now();
        List<AssignmentStatus> nonExpiredStatuses = Arrays.asList(
                AssignmentStatus.PENDING, 
                AssignmentStatus.ASSIGNED, 
                AssignmentStatus.ISSUED
        );
        List<ClothingAssignment> assignments = assignmentRepository.findExpiredAssignments(checkDate, nonExpiredStatuses);
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public ClothingAssignmentDTO updateAssignmentStatus(Long assignmentId, AssignmentStatus status) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));

        // Validate status transition
        validateStatusTransition(assignment.getStatus(), status);

        assignment.setStatus(status);
        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Override
    public ClothingAssignmentDTO markAsIssued(Long assignmentId, LocalDate issuedDate) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));

        // Validate current status
        if (assignment.getStatus() != AssignmentStatus.PENDING && assignment.getStatus() != AssignmentStatus.ASSIGNED) {
            throw new BusinessException("Tylko przydziały o statusie PENDING lub ASSIGNED mogą być oznaczone jako wydane");
        }

        // Check if employee already has a valid item of the same type
        Long employeeId = assignment.getEmployee().getId();
        Long clothingTypeId = assignment.getClothingType().getId();
        LocalDate today = LocalDate.now();

        // Get all issued assignments for the employee
        List<ClothingAssignment> issuedAssignments = assignmentRepository.findByEmployeeIdAndIssuedToEmployeeTrue(employeeId);

        // Check if any of them are of the same type and still valid (not expired)
        boolean hasValidItem = issuedAssignments.stream()
                .anyMatch(a -> a.getClothingType().getId().equals(clothingTypeId) 
                        && a.getStatus() == AssignmentStatus.ISSUED
                        && a.getExpiryDate().isAfter(today));

        if (hasValidItem) {
            throw new BusinessException("Pracownik posiada już ważny przydział tego samego typu odzieży");
        }

        if (!warehouseInventoryService.isItemInStock(assignment.getClothingType(), assignment.getQuantity())) {
            throw new BusinessException("Brak wystarczającej ilości w magazynie");
        }

        warehouseInventoryService.decreaseStock(assignment.getClothingType(), assignment.getQuantity());

        LocalDate issueDate = issuedDate != null ? issuedDate : LocalDate.now();
        assignment.setIssuedToEmployee(true);
        assignment.setIssuedDate(issueDate);
        assignment.setStatus(AssignmentStatus.ISSUED);
        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Override
    public ClothingAssignmentDTO markAsEligibleForCompensation(Long assignmentId) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));

        // Validate current status
        if (assignment.getStatus() == AssignmentStatus.ISSUED || 
            assignment.getStatus() == AssignmentStatus.RETURNED || 
            assignment.getStatus() == AssignmentStatus.COMPENSATED) {
            throw new BusinessException("Przydziały o statusie ISSUED, RETURNED lub COMPENSATED nie mogą być oznaczone jako kwalifikujące się do ekwiwalentu");
        }

        assignment.setEligibleForCompensation(true);
        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Override
    public ClothingAssignmentDTO updateAssignmentNotes(Long assignmentId, String notes) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));

        assignment.setNotes(notes);
        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Override
    public ClothingAssignmentDTO cancelAssignment(Long assignmentId, String reason) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + assignmentId + " nie został znaleziony"));

        // Validate current status
        if (assignment.getStatus() == AssignmentStatus.ISSUED || 
            assignment.getStatus() == AssignmentStatus.RETURNED || 
            assignment.getStatus() == AssignmentStatus.COMPENSATED) {
            throw new BusinessException("Przydziały o statusie ISSUED, RETURNED lub COMPENSATED nie mogą być anulowane");
        }

        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setNotes(reason);
        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    @Override
    @Transactional
    public int updateExpiredAssignments() {
        LocalDate today = LocalDate.now();
        System.out.println("Running expiration task. Today: " + today);

        List<AssignmentStatus> nonExpiredStatuses = Arrays.asList(
                AssignmentStatus.PENDING, 
                AssignmentStatus.ASSIGNED, 
                AssignmentStatus.ISSUED
        );
        List<ClothingAssignment> expiredAssignments = assignmentRepository.findExpiredAssignments(today, nonExpiredStatuses);

        // Collect unique employee IDs from expired assignments
        Set<Long> affectedEmployeeIds = new HashSet<>();

        int count = 0;
        for (ClothingAssignment assignment : expiredAssignments) {
            assignment.setStatus(AssignmentStatus.EXPIRED);

            // If the assignment was never issued to the employee, mark it as eligible for compensation
            if (!Boolean.TRUE.equals(assignment.getIssuedToEmployee())) {
                assignment.setEligibleForCompensation(true);
                // Add employee ID to the set of affected employees
                affectedEmployeeIds.add(assignment.getEmployee().getId());
            }

            assignmentRepository.save(assignment);
            count++;
        }

        // After marking assignments as eligible for compensation, automatically create compensation records
        if (count > 0) {
            int compensationsCreated = clothingCompensationService.createCompensationsForEligibleAssignments();
            System.out.println("Created " + compensationsCreated + " compensation records for eligible assignments");

            // Create new assignments for the next period for each affected employee
            for (Long employeeId : affectedEmployeeIds) {
                int newAssignmentsCreated = getEmployeeService().ensureClothingAssignmentsForEmployee(employeeId);
                System.out.println("Created " + newAssignmentsCreated + " new assignments for employee ID " + employeeId);
            }
        }

        return count;
    }

    @Override
    public List<ClothingAssignmentDTO> getAssignmentsEligibleForCompensation() {
        List<ClothingAssignment> assignments = assignmentRepository.findByEligibleForCompensationTrue();
        return ClothingAssignmentDTO.fromEntities(assignments);
    }

    @Override
    public ClothingAssignmentDTO markAsCompensated(Long assignmentId) {
        ClothingAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        assignment.setStatus(AssignmentStatus.COMPENSATED);
        assignment.setEligibleForCompensation(false);

        return ClothingAssignmentDTO.fromEntity(assignmentRepository.save(assignment));
    }

    private void validateStatusTransition(AssignmentStatus currentStatus, AssignmentStatus newStatus) {
        // Implement status transition validation logic
        // For example, can't go from CANCELLED to PENDING
        if (currentStatus == AssignmentStatus.CANCELLED && newStatus != AssignmentStatus.CANCELLED) {
            throw new BusinessException("Nie można zmienić statusu anulowanego przydziału");
        }

        if (currentStatus == AssignmentStatus.COMPENSATED && newStatus != AssignmentStatus.COMPENSATED) {
            throw new BusinessException("Nie można zmienić statusu zrekompensowanego przydziału");
        }

        if (currentStatus == AssignmentStatus.RETURNED && 
            (newStatus == AssignmentStatus.PENDING || newStatus == AssignmentStatus.ASSIGNED || newStatus == AssignmentStatus.ISSUED)) {
            throw new BusinessException("Nie można zmienić statusu zwróconego przydziału na PENDING, ASSIGNED lub ISSUED");
        }
    }
}
