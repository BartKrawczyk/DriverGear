package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import pl.programodawca.drivergear.model.Department;

@Data
public class CreateDepartmentDTO {
    @NotBlank(message = "Kod działu jest wymagany")
    private String code;

    @NotBlank(message = "Nazwa działu jest wymagana")
    private String name;

    private String description;

    public Department toEntity() {
        Department department = new Department();
        department.setCode(code);
        department.setName(name);
        department.setDescription(description);
        department.setActive(true);
        return department;
    }
}








