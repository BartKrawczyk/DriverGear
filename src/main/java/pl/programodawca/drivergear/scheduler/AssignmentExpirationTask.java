package pl.programodawca.drivergear.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.service.ClothingAssignmentService;

/**
 * Zaplanowane zadanie automatycznego oznaczania przeterminowanych przydziałów.
 * Leci codziennie o 1:00 AM.
 */
@Component
@RequiredArgsConstructor
public class AssignmentExpirationTask {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentExpirationTask.class);
    
    private final ClothingAssignmentService clothingAssignmentService;
    
    /**
     * Zaplanowany task leci dziennie o 1:00 AM do oznaczenia przeterminowanych przydziałów
     * Wywołuje metodę do updateu przeterminowanych przydziałów i loguje ilosć updatowanych przydziałów
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