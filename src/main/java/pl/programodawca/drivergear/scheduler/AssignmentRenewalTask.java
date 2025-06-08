package pl.programodawca.drivergear.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.ClothingAssignment;
import pl.programodawca.drivergear.repository.ClothingAssignmentRepository;
import pl.programodawca.drivergear.service.EmployeeService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Zaplanowany task automatycznego odnawiania przydziałów po tym jak zostały skompensowane.
 * Leci dziennie 2:00 AM, po AssignmentExpirationTask.
 */
@Component
@RequiredArgsConstructor
public class AssignmentRenewalTask {
    
    private static final Logger logger = LoggerFactory.getLogger(AssignmentRenewalTask.class);
    
    private final ClothingAssignmentRepository assignmentRepository;
    private final EmployeeService employeeService;
    
    /**
     * Leci dziennie o 2:00 AM - odnawia skompensowane przydziały.
     * Znajduje wszyskie skompensowane przydziały i tworzy nowe dla pracowników.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void renewCompensatedAssignments() {
        logger.info("Starting scheduled task to renew compensated clothing assignments");
        
        try {
            // Znajdź wszystkie skompensowane przydziały
            List<ClothingAssignment> compensatedAssignments = 
                assignmentRepository.findByStatus(AssignmentStatus.COMPENSATED);
            
            // Użycie set by zapobiec procesowaniu tego samego pracownika po kilka razy
            Set<Long> processedEmployeeIds = new HashSet<>();
            int totalAssignmentsCreated = 0;
            
            for (ClothingAssignment assignment : compensatedAssignments) {
                Long employeeId = assignment.getEmployee().getId();
                
                // Pomiń jeśli już procesowaliśmy tego pracownika
                if (processedEmployeeIds.contains(employeeId)) {
                    continue;
                }
                
                // Utwórz nowy przydział dla pracownika
                int assignmentsCreated = employeeService.ensureClothingAssignmentsForEmployee(employeeId);
                totalAssignmentsCreated += assignmentsCreated;
                
                // Zaznacz tego pracownika jako przeprocesowanego
                processedEmployeeIds.add(employeeId);
                
                logger.info("Created {} new assignments for employee ID: {}", 
                    assignmentsCreated, employeeId);
            }
            
            logger.info("Successfully renewed assignments for {} employees, created {} new assignments", 
                processedEmployeeIds.size(), totalAssignmentsCreated);
        } catch (Exception e) {
            logger.error("Error occurred while renewing clothing assignments", e);
        }
    }
}