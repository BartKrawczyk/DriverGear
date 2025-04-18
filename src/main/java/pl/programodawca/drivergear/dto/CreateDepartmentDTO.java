package pl.programodawca.drivergear.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentDTO {
    @NotBlank(message = "Nazwa działu nie może być pusta")
    @Size(min = 2, max = 100, message = "Nazwa działu musi mieć od 2 do 100 znaków")
    private String name;

    @NotBlank(message = "Kod działu nie może być pusty")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Kod działu musi składać się z 2-10 wielkich liter lub cyfr")
    private String code;

    @Size(max = 255, message = "Opis nie może przekraczać 255 znaków")
    private String description;
}

