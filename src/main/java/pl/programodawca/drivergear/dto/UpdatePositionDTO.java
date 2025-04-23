package pl.programodawca.drivergear.dto;

import lombok.*;
import pl.programodawca.drivergear.model.Position;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePositionDTO {
    private Long id;

    @NotNull(message = "Wybór działu jest wymagany")
    private Long departmentId;

    @NotBlank(message = "Nazwa stanowiska jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa stanowiska musi mieć od {min} do {max} znaków")
    private String name;

    @NotBlank(message = "Kod stanowiska jest wymagany")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Kod musi składać się z 2-10 wielkich liter lub cyfr")
    private String code;

    @Size(max = 500, message = "Opis nie może przekraczać {max} znaków")
    private String description;

    private boolean active;
}





