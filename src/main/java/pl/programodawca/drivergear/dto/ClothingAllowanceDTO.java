package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.model.ClothingAllowance;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ClothingAllowanceDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long positionId;
    private String positionName;
    private LocalDate startDate;
    private LocalDate endDate;
    private AllowanceStatus status;
    private String notes;
    private int compensationsCount;

    public static ClothingAllowanceDTO fromEntity(ClothingAllowance allowance) {
        if (allowance == null) {
            return null;
        }

        return ClothingAllowanceDTO.builder()
                .id(allowance.getId())
                .employeeId(allowance.getEmployee().getId())
                .employeeName(allowance.getEmployee().getFullName())
                .positionId(allowance.getPosition().getId())
                .positionName(allowance.getPosition().getName())
                .startDate(allowance.getStartDate())
                .endDate(allowance.getEndDate())
                .status(allowance.getStatus())
                .notes(allowance.getNotes())
                .compensationsCount(0)
                .build();
    }

    public static List<ClothingAllowanceDTO> fromEntities(List<ClothingAllowance> allowances) {
        if (allowances == null) {
            return Collections.emptyList();
        }
        return allowances.stream()
                .map(ClothingAllowanceDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
