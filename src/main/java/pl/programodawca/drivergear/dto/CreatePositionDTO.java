package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Position;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class CreatePositionDTO {
    @NotNull(message = "Wybór działu jest wymagany")
    private Long departmentId;

    @NotBlank(message = "Nazwa stanowiska jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa stanowiska musi mieć od {min} do {max} znaków")
    private String name;

    @Size(max = 500, message = "Opis nie może przekraczać {max} znaków")
    private String description;
}




