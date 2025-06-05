package pl.programodawca.drivergear.service.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.*;
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

    @PersistenceContext
    private EntityManager entityManager;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, timeout = 30)
    public Map<String, Object> markAllPendingAsPaid(Long employeeId) {
        // Register transaction synchronization to ensure changes are visible
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    // This code runs after the transaction is committed
                    System.out.println("[DEBUG_LOG] Transaction committed for markAllPendingAsPaid");
                }
            }
        );

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

            // Mark the associated assignment as compensated
            if (compensation.getClothingAssignment() != null) {
                getClothingAssignmentService().markAsCompensated(compensation.getClothingAssignment().getId());
            }

            totalAmount = totalAmount.add(compensation.getAmount());
            count++;
        }

        // Create new assignments for the next period - only need to do this once
        getEmployeeService().ensureClothingAssignmentsForEmployee(employeeId);

        // Log the number and total amount of compensations paid
        System.out.println("Marked " + count + " compensations as paid for employee ID " + employeeId + 
                " with total amount " + totalAmount);

        // Force flush and clear the persistence context to ensure changes are visible
        if (entityManager != null) {
            entityManager.flush();
            entityManager.clear();
            System.out.println("[DEBUG_LOG] Flushed and cleared persistence context");
        }

        return Map.of(
            "count", count,
            "totalAmount", totalAmount
        );
    }

    @Override
    @Transactional
    public int createCompensationsForEligibleAssignments() {
        LocalDate today = LocalDate.now();

        // Pobierz wszystkie przeterminowane i niekompensowane przydziały
        List<ClothingAssignment> assignments = assignmentRepository.findAll().stream()
                .filter(a -> a.getExpiryDate() != null)
                .filter(a -> a.getExpiryDate().isBefore(today))
                .filter(a -> a.getStatus() != AssignmentStatus.COMPENSATED)
                .filter(a -> !Boolean.TRUE.equals(a.getIssuedToEmployee()))
                .filter(a -> Boolean.TRUE.equals(a.getEligibleForCompensation()))
                .collect(Collectors.toList());

        int createdCount = 0;

        for (ClothingAssignment assignment : assignments) {
            try {
                // Utwórz kompensację
                ClothingCompensation compensation = new ClothingCompensation();
                compensation.setEmployee(assignment.getEmployee());
                compensation.setClothingAssignment(assignment);

                // Oblicz kwotę – z allowance przypisanego do przydziału
                BigDecimal compensationAmount = assignment.getPositionClothingAllowance().getClothingItems().stream()
                        .filter(item -> item.getClothingType().getId().equals(assignment.getClothingType().getId()))
                        .map(item -> item.getCompensationAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                compensation.setAmount(compensationAmount);
                compensation.setPeriodStart(assignment.getAssignmentDate());
                compensation.setPeriodEnd(assignment.getExpiryDate());
                compensation.setStatus(CompensationStatus.PENDING);

                compensationRepository.save(compensation);

                // Oznacz przydział jako COMPENSATED
                assignment.setStatus(AssignmentStatus.COMPENSATED);
                assignmentRepository.save(assignment);

                createdCount++;
            } catch (Exception e) {
                System.err.println("Error creating compensation for assignment ID=" + assignment.getId() + ": " + e.getMessage());
            }
        }

        return createdCount;
    }
}
