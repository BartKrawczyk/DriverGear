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
    private Long id;  // dodane pole id

    @NotNull(message = "Wybór działu jest wymagany")
    private Long departmentId;

    @NotBlank(message = "Nazwa stanowiska jest wymagana")
    @Size(min = 3, max = 100, message = "Nazwa stanowiska musi mieć od {min} do {max} znaków")
    private String name;

    @NotBlank(message = "Kod stanowiska jest wymagany")
    @Pattern(regexp = "^[A-Z0-9_]{2,10}$",
            message = "Kod musi składać się z 2-10 znaków\nDozwolone znaki: wielkie litery, cyfry i podkreślenie (_)")
    private String code;

    @Size(max = 500, message = "Opis nie może przekraczać {max} znaków")
    private String description;

    public Position toEntity(Department department) {
        Position position = new Position();
        position.setId(this.id);        // dodane ustawienie id
        position.setDepartment(department);
        position.setName(this.name);
        position.setCode(this.code);
        position.setDescription(this.description);
        position.setActive(true);
        return position;
    }

    public static CreatePositionDTO fromEntity(Position position) {
        CreatePositionDTO dto = new CreatePositionDTO();
        dto.setId(position.getId());    // dodane mapowanie id
        dto.setDepartmentId(position.getDepartment().getId());
        dto.setName(position.getName());
        dto.setCode(position.getCode());
        dto.setDescription(position.getDescription());
        return dto;
    }
}





