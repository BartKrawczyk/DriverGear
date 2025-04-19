package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePositionDTO {
    @NotBlank(message = "Nazwa stanowiska jest wymagana")
    private String name;

    @NotBlank(message = "Kod stanowiska jest wymagany")
    @Pattern(regexp = "^[A-Z0-9_]{2,10}$", message = "Kod musi składać się z 2-10 wielkich liter, cyfr lub podkreślnika")
    private String code;

    private String description;

    @NotNull(message = "Dział jest wymagany")
    private Long departmentId;
}

