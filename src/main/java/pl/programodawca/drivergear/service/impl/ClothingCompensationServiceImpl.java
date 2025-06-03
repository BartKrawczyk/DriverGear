package pl.programodawca.drivergear.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.ClothingAllowance;
import pl.programodawca.drivergear.model.ClothingAssignment;
import pl.programodawca.drivergear.model.ClothingCompensation;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.CompensationStatus;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.model.PositionClothingAllowance;
import pl.programodawca.drivergear.repository.*;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;
import pl.programodawca.drivergear.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClothingCompensationServiceImpl implements ClothingCompensationService {
    private final ClothingCompensationRepository compensationRepository;
    private final PositionClothingAllowanceRepository positionAllowanceRepository;
    private final ClothingAllowanceRepository allowanceRepository;
    private final ClothingAssignmentRepository assignmentRepository;
    private final ApplicationContext applicationContext;

    public ClothingCompensationServiceImpl(
            ClothingCompensationRepository compensationRepository,
            PositionClothingAllowanceRepository positionAllowanceRepository,
            ClothingAllowanceRepository allowanceRepository,
            ClothingAssignmentRepository assignmentRepository,
            ApplicationContext applicationContext) {
        this.compensationRepository = compensationRepository;
        this.positionAllowanceRepository = positionAllowanceRepository;
        this.allowanceRepository = allowanceRepository;
        this.assignmentRepository = assignmentRepository;
        this.applicationContext = applicationContext;
    }

    // Add methods to get services when needed
    private ClothingAssignmentService getClothingAssignmentService() {
        return applicationContext.getBean(ClothingAssignmentService.class);
    }

    private EmployeeService getEmployeeService() {
        return applicationContext.getBean(EmployeeService.class);
    }

    @Override
    public ClothingCompensationDTO calculateAndCreateCompensation(Long employeeId, Long allowanceId) {
        // Sprawdzenie czy przydział istnieje
        ClothingAllowance allowance = allowanceRepository.findById(allowanceId)
                .orElseThrow(() -> new EntityNotFoundException("Przydział odzieżowy o ID " + allowanceId + " nie został znaleziony"));

        // Sprawdzenie czy ID pracownika zgadza się z przydziałem
        if (!employeeId.equals(allowance.getEmployee().getId())) {
            throw new BusinessException("Przydział odzieżowy nie należy do wskazanego pracownika");
        }

        // Sprawdzenie uprawnień do ekwiwalentu
        if (!isEligibleForCompensation(allowance)) {
            throw new BusinessException("Pracownik nie kwalifikuje się do ekwiwalentu za ten przydział");
        }

        // Obliczenie kwoty ekwiwalentu
        BigDecimal compensationAmount = calculateCompensationAmount(allowance);

        // Utworzenie nowego ekwiwalentu
        ClothingCompensation compensation = new ClothingCompensation();
        compensation.setEmployee(allowance.getEmployee());

        // Find or create a clothing assignment for this employee and allowance
        List<ClothingAssignment> assignments = assignmentRepository.findByEmployeeId(allowance.getEmployee().getId());
        ClothingAssignment assignment = assignments.stream()
                .filter(a -> a.getPositionClothingAllowance().getPosition().getId().equals(allowance.getPosition().getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono przydziału odzieży dla pracownika"));

        compensation.setClothingAssignment(assignment);
        compensation.setAmount(compensationAmount);
        compensation.setPeriodStart(allowance.getStartDate());
        compensation.setPeriodEnd(allowance.getEndDate());
        compensation.setStatus(CompensationStatus.PENDING);

        // Zapisanie i konwersja na DTO
        ClothingCompensation savedCompensation = compensationRepository.save(compensation);

        // Mark the assignment as compensated
        getClothingAssignmentService().markAsCompensated(assignment.getId());

        return ClothingCompensationDTO.fromEntity(savedCompensation);
    }


    @Override
    public ClothingCompensationDTO approveCompensation(Long compensationId) {
        ClothingCompensation compensation = compensationRepository.findById(compensationId)
                .orElseThrow(() -> new EntityNotFoundException("Ekwiwalent nie znaleziony"));

        if (compensation.getStatus() != CompensationStatus.PENDING) {
            throw new BusinessException("Tylko oczekujące ekwiwalenty mogą być zatwierdzone");
        }

        compensation.setStatus(CompensationStatus.APPROVED);
        return ClothingCompensationDTO.fromEntity(compensationRepository.save(compensation));
    }

    @Override
    public ClothingCompensationDTO markAsPaid(Long compensationId) {
        ClothingCompensation compensation = compensationRepository.findById(compensationId)
                .orElseThrow(() -> new EntityNotFoundException("Ekwiwalent nie znaleziony"));

        // Allow both PENDING and APPROVED compensations to be marked as paid
        if (compensation.getStatus() != CompensationStatus.PENDING && compensation.getStatus() != CompensationStatus.APPROVED) {
            throw new BusinessException("Tylko oczekujące lub zatwierdzone ekwiwalenty mogą być oznaczone jako wypłacone");
        }

        compensation.setStatus(CompensationStatus.PAID);
        compensation.setPaymentDate(LocalDate.now());

        // Create new assignments for the next period
        Long employeeId = compensation.getEmployee().getId();
        getEmployeeService().ensureClothingAssignmentsForEmployee(employeeId);

        return ClothingCompensationDTO.fromEntity(compensationRepository.save(compensation));
    }

    @Override
    public List<ClothingCompensationDTO> getPendingCompensations(Long employeeId) {
        List<ClothingCompensation> compensations = compensationRepository.findByEmployeeIdAndStatus(employeeId, CompensationStatus.PENDING);
        return ClothingCompensationDTO.fromEntities(compensations);
    }

    @Override
    public BigDecimal calculateTotalPendingCompensations(Long employeeId) {
        BigDecimal result = compensationRepository.sumCompensationsByEmployeeAndStatus(
                employeeId, CompensationStatus.PENDING);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public ClothingCompensationDTO cancelCompensation(Long compensationId, String reason) {
        ClothingCompensation compensation = compensationRepository.findById(compensationId)
                .orElseThrow(() -> new EntityNotFoundException("Ekwiwalent nie znaleziony"));

        if (compensation.getStatus() == CompensationStatus.PAID) {
            throw new BusinessException("Nie można anulować wypłaconego ekwiwalentu");
        }

        compensation.setStatus(CompensationStatus.CANCELLED);
        compensation.setNotes(reason);
        return ClothingCompensationDTO.fromEntity(compensationRepository.save(compensation));
    }

    @Override
    public List<ClothingCompensationDTO> getEmployeeCompensationHistory(Long employeeId) {
        List<ClothingCompensation> compensations = compensationRepository.findByEmployeeId(employeeId);
        return ClothingCompensationDTO.fromEntities(compensations);
    }

    private boolean isEligibleForCompensation(ClothingAllowance allowance) {
        // Logika sprawdzająca czy pracownik kwalifikuje się do ekwiwalentu
        return true; // Uproszczone dla przykładu
    }

    private BigDecimal calculateCompensationAmount(ClothingAllowance allowance) {
        // Logika obliczania kwoty ekwiwalentu
        return allowance.getPosition().getStandardAllowances().stream()
                .flatMap(positionAllowance -> positionAllowance.getClothingItems().stream())
                .map(item -> item.getCompensationAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<ClothingCompensationDTO> getCompensationsByAssignmentId(Long assignmentId) {
        List<ClothingCompensation> compensations = compensationRepository.findByClothingAssignmentId(assignmentId);
        return ClothingCompensationDTO.fromEntities(compensations);
    }

    @Override
    public List<ClothingCompensationDTO> getCompensationsByPositionId(Long positionId) {
        List<ClothingCompensation> compensations = compensationRepository.findByPositionId(positionId);
        return ClothingCompensationDTO.fromEntities(compensations);
    }

    @Override
    public List<ClothingCompensationDTO> getCompensationsByDepartmentId(Long departmentId) {
        List<ClothingCompensation> compensations = compensationRepository.findByDepartmentId(departmentId);
        return ClothingCompensationDTO.fromEntities(compensations);
    }

    @Override
    @Transactional
    public Map<String, Object> markAllPendingAsPaid(Long employeeId) {
        List<ClothingCompensation> pendingCompensations = compensationRepository.findByEmployeeIdAndStatus(employeeId, CompensationStatus.PENDING);

        if (pendingCompensations.isEmpty()) {
            return Map.of(
                "count", 0,
                "totalAmount", BigDecimal.ZERO
            );
        }

        int count = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        LocalDate now = LocalDate.now();

        for (ClothingCompensation compensation : pendingCompensations) {
            compensation.setStatus(CompensationStatus.PAID);
            compensation.setPaymentDate(now);
            compensationRepository.save(compensation);
            totalAmount = totalAmount.add(compensation.getAmount());
            count++;
        }

        // Create new assignments for the next period - only need to do this once
        getEmployeeService().ensureClothingAssignmentsForEmployee(employeeId);

        // Log the number and total amount of compensations paid
        System.out.println("Marked " + count + " compensations as paid for employee ID " + employeeId + 
                " with total amount " + totalAmount);

        return Map.of(
            "count", count,
            "totalAmount", totalAmount
        );
    }

    @Override
    @Transactional
    public int createCompensationsForEligibleAssignments() {
        // Get all assignments eligible for compensation
        List<ClothingAssignmentDTO> eligibleAssignments = getClothingAssignmentService().getAssignmentsEligibleForCompensation();

        int createdCount = 0;

        for (ClothingAssignmentDTO assignmentDTO : eligibleAssignments) {
            try {
                // Get the assignment entity
                ClothingAssignment assignment = assignmentRepository.findById(assignmentDTO.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

                // Create a new compensation
                ClothingCompensation compensation = new ClothingCompensation();
                compensation.setEmployee(assignment.getEmployee());
                compensation.setClothingAssignment(assignment);

                // Calculate compensation amount based on the position clothing allowance
                BigDecimal compensationAmount = assignment.getPositionClothingAllowance().getClothingItems().stream()
                        .map(item -> item.getCompensationAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                compensation.setAmount(compensationAmount);

                // Set period start and end dates from the assignment
                compensation.setPeriodStart(assignment.getAssignmentDate());
                compensation.setPeriodEnd(assignment.getExpiryDate());
                compensation.setStatus(CompensationStatus.PENDING);

                // Save the compensation
                compensationRepository.save(compensation);

                // Mark the assignment as compensated
                getClothingAssignmentService().markAsCompensated(assignment.getId());

                createdCount++;
            } catch (Exception e) {
                // Log the error but continue processing other assignments
                // In a real application, you might want to use a logger instead of System.err
                System.err.println("Error creating compensation for assignment " + assignmentDTO.getId() + ": " + e.getMessage());
            }
        }

        return createdCount;
    }
}
