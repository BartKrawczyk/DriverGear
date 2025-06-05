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
 * Scheduled task that automatically renews clothing assignments after they are compensated.
 * Runs daily at 2:00 AM, after the AssignmentExpirationTask.
 */
@Component
@RequiredArgsConstructor
public class AssignmentRenewalTask {
    
    private static final Logger logger = LoggerFactory.getLogger(AssignmentRenewalTask.class);
    
    private final ClothingAssignmentRepository assignmentRepository;
    private final EmployeeService employeeService;
    
    /**
     * Scheduled task that runs daily at 2:00 AM to renew compensated clothing assignments.
     * Finds all compensated assignments and creates new assignments for the employees.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void renewCompensatedAssignments() {
        logger.info("Starting scheduled task to renew compensated clothing assignments");
        
        try {
            // Find all compensated assignments
            List<ClothingAssignment> compensatedAssignments = 
                assignmentRepository.findByStatus(AssignmentStatus.COMPENSATED);
            
            // Use a Set to avoid processing the same employee multiple times
            Set<Long> processedEmployeeIds = new HashSet<>();
            int totalAssignmentsCreated = 0;
            
            for (ClothingAssignment assignment : compensatedAssignments) {
                Long employeeId = assignment.getEmployee().getId();
                
                // Skip if we've already processed this employee
                if (processedEmployeeIds.contains(employeeId)) {
                    continue;
                }
                
                // Create new assignments for the employee
                int assignmentsCreated = employeeService.ensureClothingAssignmentsForEmployee(employeeId);
                totalAssignmentsCreated += assignmentsCreated;
                
                // Mark this employee as processed
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