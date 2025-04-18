package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepartmentDTO {
    private Long id;

    @NotBlank(message = "Nazwa jest wymagana")
    @Size(min = 2, max = 50, message = "Nazwa musi mieć od 2 do 50 znaków")
    private String name;

    @NotBlank(message = "Kod jest wymagany")
    @Size(min = 2, max = 10, message = "Kod musi mieć od 2 do 10 znaków")
    private String code;

    private String description;

    public DepartmentDTO toDepartmentDTO() {
        return DepartmentDTO.builder()
                .id(this.id)
                .name(this.name)
                .code(this.code)
                .description(this.description)
                .build();
    }

    public static UpdateDepartmentDTO fromDepartmentDTO(DepartmentDTO departmentDTO) {
        return UpdateDepartmentDTO.builder()
                .id(departmentDTO.getId())
                .name(departmentDTO.getName())
                .code(departmentDTO.getCode())
                .description(departmentDTO.getDescription())
                .build();
    }
}


