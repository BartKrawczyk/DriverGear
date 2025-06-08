package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.programodawca.drivergear.service.ClothingAssignmentService;
import pl.programodawca.drivergear.service.ClothingCompensationService;

import java.util.Map;

/**
 * Development-only controller for triggering maintenance operations.
 * This controller is only available in the "dev" profile and should not be used in production.
 */
@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile("dev")
public class DevToolsController {

    private final ClothingAssignmentService clothingAssignmentService;
    private final ClothingCompensationService clothingCompensationService;

    /**
     * Manually triggers the process that marks expired clothing assignments.
     * This endpoint simulates what the scheduled task would do in production.
     * 
     * @return A response containing the number of expired assignments that were updated
     */
    @PostMapping("/expire-assignments")
    public ResponseEntity<Map<String, Object>> expireAssignments() {
        int updatedCount = clothingAssignmentService.updateExpiredAssignments();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Expired assignments updated: " + updatedCount,
            "updatedCount", updatedCount
        ));
    }

    /**
     * Manually triggers the process that creates compensation records for eligible assignments.
     * This endpoint finds all assignments marked as eligible for compensation and creates
     * a compensation record for each one.
     * 
     * @return A response containing the number of compensation records created
     */
    @PostMapping("/create-compensations")
    public ResponseEntity<Map<String, Object>> createCompensations() {
        int createdCount = clothingCompensationService.createCompensationsForEligibleAssignments();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Compensation records created: " + createdCount,
            "createdCount", createdCount
        ));
    }
}
