package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePositionDTO {
    @NotBlank(message = "Nazwa stanowiska jest wymagana")
    private String name;

    @NotBlank(message = "Kod stanowiska jest wymagany")
    private String code;

    private String description;

    @NotNull(message = "Dział jest wymagany")
    private Long departmentId;

    // Metoda pomocnicza do konwersji z PositionDTO
    public static UpdatePositionDTO fromPositionDTO(PositionDTO positionDTO) {
        return UpdatePositionDTO.builder()
                .name(positionDTO.getName())
                .code(positionDTO.getCode())
                .description(positionDTO.getDescription())
                .departmentId(positionDTO.getDepartmentId())
                .build();
    }
}

