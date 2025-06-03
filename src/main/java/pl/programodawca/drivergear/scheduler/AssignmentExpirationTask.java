package pl.programodawca.drivergear.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.service.ClothingAssignmentService;

/**
 * Scheduled task that automatically marks expired clothing assignments.
 * Runs daily at 1:00 AM.
 */
@Component
@RequiredArgsConstructor
public class AssignmentExpirationTask {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentExpirationTask.class);
    
    private final ClothingAssignmentService clothingAssignmentService;
    
    /**
     * Scheduled task that runs daily at 1:00 AM to mark expired clothing assignments.
     * Calls the service method to update expired assignments and logs the number of updated assignments.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void markExpiredAssignments() {
        logger.info("Starting scheduled task to mark expired clothing assignments");
        
        try {
            int updatedCount = clothingAssignmentService.updateExpiredAssignments();
            logger.info("Successfully marked {} expired clothing assignments", updatedCount);
        } catch (Exception e) {
            logger.error("Error occurred while marking expired clothing assignments", e);
        }
    }
}