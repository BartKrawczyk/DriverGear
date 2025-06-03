package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.ClothingCompensation;
import pl.programodawca.drivergear.model.CompensationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO for ClothingCompensation entity.
 * Represents a compensation for clothing that an employee didn't receive.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ClothingCompensationDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long clothingAssignmentId;
    private String clothingTypeName;
    private String positionName;
    private String departmentName;
    private BigDecimal amount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private CompensationStatus status;
    private LocalDate paymentDate;
    private String notes;

    public static ClothingCompensationDTO fromEntity(ClothingCompensation compensation) {
        if (compensation == null) {
            return null;
        }

        return ClothingCompensationDTO.builder()
                .id(compensation.getId())
                .employeeId(compensation.getEmployee().getId())
                .employeeName(compensation.getEmployee().getFullName())
                .clothingAssignmentId(compensation.getClothingAssignment().getId())
                .clothingTypeName(compensation.getClothingAssignment().getPositionClothingAllowance().getClothingItems().isEmpty() ? 
                        "Brak typu odzieży" : 
                        compensation.getClothingAssignment().getPositionClothingAllowance().getClothingItems().iterator().next().getClothingType().getName())
                .positionName(compensation.getClothingAssignment().getPositionClothingAllowance().getPosition().getName())
                .departmentName(compensation.getClothingAssignment().getPositionClothingAllowance().getDepartment().getName())
                .amount(compensation.getAmount())
                .periodStart(compensation.getPeriodStart())
                .periodEnd(compensation.getPeriodEnd())
                .status(compensation.getStatus())
                .paymentDate(compensation.getPaymentDate())
                .notes(compensation.getNotes())
                .build();
    }

    public static List<ClothingCompensationDTO> fromEntities(List<ClothingCompensation> compensations) {
        if (compensations == null) {
            return Collections.emptyList();
        }
        return compensations.stream()
                .map(ClothingCompensationDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
