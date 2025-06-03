package pl.programodawca.drivergear.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.repository.EmployeeRepository;
import pl.programodawca.drivergear.repository.PositionRepository;
import pl.programodawca.drivergear.service.ClothingAllowanceService;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.PositionClothingAllowanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionClothingAllowanceService positionClothingAllowanceService;
    private final ClothingAssignmentService clothingAssignmentService;
    private final ClothingAllowanceService clothingAllowanceService;

    @Override
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        return employeeRepository.findAll(pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public Page<EmployeeDTO> findAllByActive(boolean active, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        return employeeRepository.findAllByActive(active, pageable)
                .map(EmployeeDTO::fromEntity);
    }


    @Override
    public Page<EmployeeDTO> findAllByPosition(Long positionId, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", positionId));
        return employeeRepository.findAllByPosition(position, pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public Page<EmployeeDTO> findAllByDepartment(Long departmentId, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        return employeeRepository.findAllByPosition_Department_Id(departmentId, pageable)
                .map(EmployeeDTO::fromEntity);
    }

    @Override
    public EmployeeDTO findById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", id));
    }

    @Override
    public EmployeeDTO create(EmployeeDTO employeeDTO) {
        Position position = positionRepository.findById(employeeDTO.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", employeeDTO.getPositionId()));

        Employee employee = employeeDTO.toEntity();
        employee.setPosition(position);

        // Set department if departmentId is provided
        if (employeeDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dział", "id", employeeDTO.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee savedEmployee = employeeRepository.save(employee);

        // Automatically assign clothing allowances based on position
        assignClothingAllowanceBasedOnPosition(savedEmployee, null);

        return EmployeeDTO.fromEntity(savedEmployee);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", id));

        // Store the previous position ID to check if it has changed
        Long previousPositionId = employee.getPosition().getId();

        Position position = positionRepository.findById(employeeDTO.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", employeeDTO.getPositionId()));

        employeeDTO.updateEntity(employee);
        employee.setPosition(position);

        // Set department if departmentId is provided
        if (employeeDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dział", "id", employeeDTO.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        // Automatically assign clothing allowances based on position if it has changed
        assignClothingAllowanceBasedOnPosition(updatedEmployee, previousPositionId);

        return EmployeeDTO.fromEntity(updatedEmployee);
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pracownik", "id", id);
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public boolean existsByPosition(Long positionId) {
        return employeeRepository.existsByPosition_Id(positionId);
    }

    @Override
    public long countByPosition(Long positionId) {
        return employeeRepository.countByPosition_Id(positionId);
    }

    @Override
    public Page<EmployeeDTO> findByFilters(String firstName, String lastName, String employeeNumber, 
                                         Long departmentId, Long positionId, Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        // Start with all active employees
        Page<Employee> employeesPage = employeeRepository.findAllByActive(true, pageable);

        // Apply department filter if provided
        if (departmentId != null) {
            employeesPage = employeeRepository.findAllByPosition_Department_Id(departmentId, pageable);
        }

        // Apply position filter if provided
        if (positionId != null) {
            Position position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Stanowisko", "id", positionId));
            employeesPage = employeeRepository.findAllByPosition(position, pageable);
        }

        // Convert to DTOs
        Page<EmployeeDTO> employeeDTOPage = employeesPage.map(EmployeeDTO::fromEntity);

        // Apply additional filters in memory (for firstName, lastName, employeeNumber)
        // These filters are applied after pagination, which is not ideal for large datasets
        // but is simpler to implement for this example
        if (firstName != null && !firstName.isEmpty() || 
            lastName != null && !lastName.isEmpty() || 
            employeeNumber != null && !employeeNumber.isEmpty()) {

            List<EmployeeDTO> filteredContent = employeeDTOPage.getContent().stream()
                .filter(emp -> firstName == null || firstName.isEmpty() || 
                               emp.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                .filter(emp -> lastName == null || lastName.isEmpty() || 
                               emp.getLastName().toLowerCase().contains(lastName.toLowerCase()))
                .filter(emp -> employeeNumber == null || employeeNumber.isEmpty() || 
                               emp.getEmployeeNumber().toLowerCase().contains(employeeNumber.toLowerCase()))
                .collect(Collectors.toList());

            // Create a new page with the filtered content
            // Note: This approach loses the original pagination information
            return new org.springframework.data.domain.PageImpl<>(filteredContent, pageable, filteredContent.size());
        }

        return employeeDTOPage;
    }

    /**
     * Automatically assigns clothing allowances to an employee based on their position.
     * If the position has multiple allowances, all of them will be assigned.
     * 
     * @param employee The employee to assign allowances to
     * @param previousPositionId The previous position ID (if updating), or null if creating a new employee
     */
    private void assignClothingAllowanceBasedOnPosition(Employee employee, Long previousPositionId) {
        if (previousPositionId != null && previousPositionId.equals(employee.getPosition().getId())) {
            return;
        }

        List<PositionClothingAllowanceDTO> positionAllowances =
                positionClothingAllowanceService.getPositionAllowancesByPositionId(employee.getPosition().getId());

        if (positionAllowances.isEmpty()) {
            logger.info("No clothing allowances found for position ID: {}. No allowances created for employee ID: {}",
                    employee.getPosition().getId(), employee.getId());
            return;
        }

        if (previousPositionId != null) {
            List<ClothingAssignmentDTO> existingAssignments =
                    clothingAssignmentService.getAssignmentsByEmployeeId(employee.getId());

            for (ClothingAssignmentDTO assignment : existingAssignments) {
                Long allowancePositionId = positionClothingAllowanceService
                        .getPositionAllowanceById(assignment.getPositionClothingAllowanceId())
                        .getPositionId();

                if (allowancePositionId.equals(previousPositionId)) {
                    clothingAssignmentService.cancelAssignment(
                            assignment.getId(),
                            "Position changed from ID: " + previousPositionId + " to ID: " + employee.getPosition().getId()
                    );
                    logger.info("Cancelled clothing assignment ID: {} due to position change for employee ID: {}",
                            assignment.getId(), employee.getId());
                }
            }
        }

        for (PositionClothingAllowanceDTO positionAllowance : positionAllowances) {
            try {
                List<ClothingAllowanceDTO> existingAllowances =
                        clothingAllowanceService.getAllowancesByEmployeeIdAndStatus(employee.getId(), AllowanceStatus.ACTIVE);

                boolean duplicateExists = existingAllowances.stream()
                        .anyMatch(existing -> existing.getPositionId().equals(employee.getPosition().getId()));

                if (duplicateExists) {
                    logger.info("Skipping creation of duplicate clothing allowance for employee ID: {} and position ID: {}",
                            employee.getId(), employee.getPosition().getId());
                    continue;
                }

                int validityPeriod = 12;
                if (!positionAllowance.getClothingItems().isEmpty()) {
                    validityPeriod = positionAllowance.getClothingItems().stream()
                            .mapToInt(PositionClothingItemDTO::getValidityPeriod)
                            .max()
                            .orElse(12);
                }

                // Use the employee's hire date as the start date for the clothing allowance
                // This ensures that entitlements are correctly calculated based on the hire date
                LocalDate startDate = employee.getHireDate();
                LocalDate endDate = startDate.plusMonths(validityPeriod);

                ClothingAllowanceDTO allowance = clothingAllowanceService.createAllowance(
                        employee.getId(),
                        employee.getPosition().getId(),
                        startDate,
                        endDate,
                        "Automatically assigned based on position"
                );

                logger.info("Created clothing allowance ID: {} for employee ID: {} based on position ID: {}",
                        allowance.getId(), employee.getId(), employee.getPosition().getId());

                // Get existing assignments for this employee
                List<ClothingAssignmentDTO> existingAssignments = 
                    clothingAssignmentService.getAssignmentsByEmployeeId(employee.getId());

                // 🔁 Pętla po elementach odzieży
                for (PositionClothingItemDTO clothingItem : positionAllowance.getClothingItems()) {
                    // Check if assignments already exist for this clothing type
                    int existingQuantity = existingAssignments.stream()
                        .filter(a ->
                                a.getClothingTypeId() != null &&
                                a.getClothingTypeId().equals(clothingItem.getClothingTypeId()) &&
                                a.getPositionClothingAllowanceId().equals(positionAllowance.getId()) &&
                                (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ASSIGNED)
                        )
                        .mapToInt(ClothingAssignmentDTO::getQuantity)
                        .sum();

                    int quantityToCreate = (clothingItem.getQuantity() != null ? clothingItem.getQuantity() : 1) - existingQuantity;

                    if (quantityToCreate <= 0) {
                        logger.info("Employee ID: {} already has {} assignments for clothing type ID: {} - not creating more",
                                employee.getId(), existingQuantity, clothingItem.getClothingTypeId());
                        continue;
                    }

                    // Create a ClothingType object with just the ID
                    ClothingType clothingType = new ClothingType();
                    clothingType.setId(clothingItem.getClothingTypeId());

                    ClothingAssignmentDTO assignment = clothingAssignmentService.createAssignment(
                            employee.getId(),
                            positionAllowance.getId(),
                            clothingType,
                            null,
                            quantityToCreate,
                            "Automatically assigned based on position - " + clothingItem.getClothingTypeName()
                    );

                    logger.info("Created clothing assignment ID: {} for employee ID: {} based on clothing type: {}",
                            assignment.getId(), employee.getId(), clothingItem.getClothingTypeName());

                    // Add the newly created assignment to the list of existing assignments
                    // so it's considered in subsequent iterations
                    existingAssignments.add(assignment);
                }

            } catch (Exception e) {
                logger.error("Failed to create clothing allowance/assignment for employee ID: {} and position ID: {}: {}",
                        employee.getId(), employee.getPosition().getId(), e.getMessage());
            }
        }
    }

    @Override
    public int ensureClothingAssignmentsForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", employeeId));

        List<PositionClothingAllowanceDTO> positionAllowances =
                positionClothingAllowanceService.getPositionAllowancesByPositionId(employee.getPosition().getId());

        if (positionAllowances.isEmpty()) {
            logger.info("Brak przydziałów odzieży dla stanowiska ID: {}. Nie utworzono przydziałów dla pracownika ID: {}",
                    employee.getPosition().getId(), employee.getId());
            return 0;
        }

        List<ClothingAssignmentDTO> existingAssignments =
                clothingAssignmentService.getAssignmentsByEmployeeId(employee.getId());

        logger.info("Znaleziono {} istniejących przydziałów odzieży dla pracownika ID: {}",
                existingAssignments.size(), employee.getId());

        int assignmentsCreated = 0;

        for (PositionClothingAllowanceDTO positionAllowance : positionAllowances) {
            for (PositionClothingItemDTO clothingItem : positionAllowance.getClothingItems()) {

                int existingQuantity = existingAssignments.stream()
                        .filter(a ->
                                a.getClothingTypeId() != null &&
                                        a.getClothingTypeId().equals(clothingItem.getClothingTypeId()) &&
                                        a.getPositionClothingAllowanceId().equals(positionAllowance.getId()) &&
                                        (a.getStatus() == AssignmentStatus.PENDING || a.getStatus() == AssignmentStatus.ASSIGNED)
                        )
                        .mapToInt(ClothingAssignmentDTO::getQuantity)
                        .sum();

                int quantityToCreate = clothingItem.getQuantity() - existingQuantity;

                if (quantityToCreate <= 0) {
                    logger.info("Pracownik ID: {} ma już {} przydziałów odzieży typu ID: {} — nie tworzę nowych.",
                            employee.getId(), existingQuantity, clothingItem.getClothingTypeId());
                    continue;
                }

                // Pobierz encję ClothingType na podstawie ID
                ClothingType clothingType = new ClothingType();
                clothingType.setId(clothingItem.getClothingTypeId());

                try {
                    ClothingAssignmentDTO assignment = clothingAssignmentService.createAssignment(
                            employee.getId(),
                            positionAllowance.getId(),
                            clothingType,
                            null, // size może być uzupełniony później
                            quantityToCreate,
                            "Automatically assigned based on position - " + clothingItem.getClothingTypeName()
                    );

                    logger.info("Utworzono przydział odzieży ID: {} dla pracownika ID: {}, typ odzieży ID: {}, ilość: {}",
                            assignment.getId(), employee.getId(), clothingItem.getClothingTypeId(), quantityToCreate);

                    // Add the newly created assignment to the list of existing assignments
                    // so it's considered in subsequent iterations
                    existingAssignments.add(assignment);

                    assignmentsCreated++;

                } catch (Exception e) {
                    logger.error("Błąd przy tworzeniu przydziału dla pracownika ID: {} i typu odzieży ID: {}: {}",
                            employee.getId(), clothingItem.getClothingTypeId(), e.getMessage());
                }
            }
        }

        logger.info("Łącznie utworzono {} nowych przydziałów dla pracownika ID: {}", assignmentsCreated, employee.getId());
        return assignmentsCreated;
    }


//    @Override
//    public int ensureClothingAssignmentsForEmployee(Long employeeId) {
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new ResourceNotFoundException("Pracownik", "id", employeeId));
//
//        // Get allowances for the employee's position
//        List<PositionClothingAllowanceDTO> positionAllowances =
//            positionClothingAllowanceService.getPositionAllowancesByPositionId(employee.getPosition().getId());
//
//        if (positionAllowances.isEmpty()) {
//            logger.info("No clothing allowances found for position ID: {}. No assignments created for employee ID: {}",
//                employee.getPosition().getId(), employee.getId());
//            return 0;
//        }
//
//        // Get ALL existing assignments for the employee regardless of status
//        List<ClothingAssignmentDTO> existingAssignments =
//            clothingAssignmentService.getAssignmentsByEmployeeId(employee.getId());
//
//        logger.info("Found {} existing assignments for employee ID: {}",
//            existingAssignments.size(), employee.getId());
//
//        int assignmentsCreated = 0;
//
//        // Create assignments for each position allowance
//        for (PositionClothingAllowanceDTO positionAllowance : positionAllowances) {
//            try {
//                // Create a new clothing allowance for the employee if one doesn't exist
//                List<ClothingAllowanceDTO> existingAllowances =
//                    clothingAllowanceService.getAllowancesByEmployeeIdAndStatus(employee.getId(), AllowanceStatus.ACTIVE);
//
//                boolean allowanceExists = existingAllowances.stream()
//                    .anyMatch(existing -> existing.getPositionId().equals(employee.getPosition().getId()));
//
//                if (!allowanceExists) {
//                    // Calculate validity period (end date) based on position clothing items
//                    int validityPeriod = 12; // Default to 12 months if no items
//                    if (!positionAllowance.getClothingItems().isEmpty()) {
//                        // Use the maximum validity period from all items
//                        validityPeriod = positionAllowance.getClothingItems().stream()
//                            .mapToInt(item -> item.getValidityPeriod())
//                            .max()
//                            .orElse(12);
//                    }
//
//                    // Create a new clothing allowance for the employee
//                    LocalDate startDate = LocalDate.now();
//                    LocalDate endDate = startDate.plusMonths(validityPeriod);
//
//                    ClothingAllowanceDTO allowance = clothingAllowanceService.createAllowance(
//                        employee.getId(),
//                        employee.getPosition().getId(),
//                        startDate,
//                        endDate,
//                        "Automatically assigned based on position"
//                    );
//
//                    logger.info("Created clothing allowance ID: {} for employee ID: {} based on position ID: {}",
//                        allowance.getId(), employee.getId(), employee.getPosition().getId());
//                }
//
//                // Create a clothing assignment for each clothing item in the position allowance
//                if (positionAllowance.getClothingItems().isEmpty()) {
//                    logger.info("No clothing items found for position allowance ID: {}. No assignments created.",
//                        positionAllowance.getId());
//                    continue;
//                }
//
//                for (PositionClothingItemDTO clothingItem : positionAllowance.getClothingItems()) {
//                    // Check if an assignment already exists for this clothing type with an active status (PENDING or ASSIGNED)
//                    boolean assignmentExists = existingAssignments.stream()
//                        .anyMatch(existing ->
//                            existing.getClothingTypeId() != null &&
//                            existing.getClothingTypeId().equals(clothingItem.getClothingTypeId()) &&
//                            existing.getPositionClothingAllowanceId().equals(positionAllowance.getId()) &&
//                            (existing.getStatus() == AssignmentStatus.PENDING ||
//                             existing.getStatus() == AssignmentStatus.ASSIGNED)
//                        );
//
//                    if (assignmentExists) {
//                        logger.info("Active assignment already exists for employee ID: {}, position allowance ID: {}, and clothing type: {} (ID: {}). Skipping creation.",
//                            employee.getId(), positionAllowance.getId(), clothingItem.getClothingTypeName(), clothingItem.getClothingTypeId());
//                        continue;
//                    }
//
//                    // Create a new clothing assignment for the employee and this specific clothing item
//                    ClothingAssignmentDTO assignment = clothingAssignmentService.createAssignment(
//                        employee.getId(),
//                        positionAllowance.getId(),
//                        null, // size - will be determined later
//                        clothingItem.getQuantity(), // use the quantity from the clothing item
//                        "Automatically assigned based on position - " + clothingItem.getClothingTypeName()
//                    );
//
//                    logger.info("Created clothing assignment ID: {} for employee ID: {}, position allowance ID: {}, and clothing type: {} (ID: {})",
//                        assignment.getId(), employee.getId(), positionAllowance.getId(), clothingItem.getClothingTypeName(), clothingItem.getClothingTypeId());
//
//                    assignmentsCreated++;
//                }
//            } catch (Exception e) {
//                logger.error("Failed to create clothing assignment for employee ID: {} and position allowance ID: {}: {}",
//                    employee.getId(), positionAllowance.getId(), e.getMessage());
//            }
//        }
//
//        logger.info("Total new assignments created for employee ID: {}: {}", employee.getId(), assignmentsCreated);
//        return assignmentsCreated;
//    }
}
