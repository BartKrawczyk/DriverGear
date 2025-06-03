package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DTO representing the dynamic clothing entitlements for an employee.
 * Contains employee information and a list of clothing type entitlements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class EmployeeEntitlementDTO {
    private Long employeeId;
    private String employeeName;
    private List<ClothingTypeEntitlementDTO> clothingEntitlements;
    private boolean hasExpiredAssignments;

    // Calculate if there are any expired assignments
    public boolean isHasExpiredAssignments() {
        if (clothingEntitlements == null) {
            return false;
        }

        return clothingEntitlements.stream()
            .anyMatch(e -> e.getExpiredQuantity() > 0);
    }
}
