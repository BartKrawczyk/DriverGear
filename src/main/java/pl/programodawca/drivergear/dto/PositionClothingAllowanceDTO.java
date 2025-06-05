package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.PositionClothingAllowance;
import pl.programodawca.drivergear.model.PositionClothingItem;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PositionClothingAllowanceDTO {
    private Long id;

    @NotNull(message = "Dział jest wymagany")
    private Long departmentId;
    private String departmentName;

    @NotNull(message = "Stanowisko jest wymagane")
    private Long positionId;
    private String positionName;

    @NotEmpty(message = "Lista elementów odzieży jest wymagana")
    @Valid
    private List<PositionClothingItemDTO> clothingItems = new ArrayList<>();

    private Boolean active = true;
    private String notes;
    private int assignmentsCount;

    public static PositionClothingAllowanceDTO fromEntity(PositionClothingAllowance allowance) {
        if (allowance == null) {
            return null;
        }

        List<PositionClothingItemDTO> clothingItemDTOs = allowance.getClothingItems().stream()
                .map(PositionClothingItemDTO::fromEntity)
                .collect(Collectors.toList());

        return PositionClothingAllowanceDTO.builder()
                .id(allowance.getId())
                .departmentId(allowance.getDepartment().getId())
                .departmentName(allowance.getDepartment().getName())
                .positionId(allowance.getPosition().getId())
                .positionName(allowance.getPosition().getName())
                .clothingItems(clothingItemDTOs)
                .active(allowance.getActive())
                .notes(allowance.getNotes())
                .assignmentsCount(allowance.getAssignments().size())
                .build();
    }

    public static List<PositionClothingAllowanceDTO> fromEntities(List<PositionClothingAllowance> allowances) {
        if (allowances == null) {
            return Collections.emptyList();
        }
        return allowances.stream()
                .map(PositionClothingAllowanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PositionClothingAllowance toEntity() {
        PositionClothingAllowance allowance = new PositionClothingAllowance();
        // Note: Department and Position must be set by the service layer
        allowance.setActive(this.active);
        allowance.setNotes(this.notes);
        return allowance;
    }
}
