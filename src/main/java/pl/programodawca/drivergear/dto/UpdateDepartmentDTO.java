package pl.programodawca.drivergear.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import pl.programodawca.drivergear.model.Department;

@Data
public class UpdateDepartmentDTO {
    @NotNull(message = "ID działu jest wymagane")
    private Long id;

    @NotBlank(message = "Kod działu jest wymagany")
    private String code;

    @NotBlank(message = "Nazwa działu jest wymagana")
    private String name;

    private String description;

    private boolean active;

    public void updateEntity(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department nie może być null");
        }
        department.setCode(code);
        department.setName(name);
        department.setDescription(description);
        department.setActive(active);
    }

    public static UpdateDepartmentDTO fromEntity(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department nie może być null");
        }

        UpdateDepartmentDTO dto = new UpdateDepartmentDTO();
        dto.setId(department.getId());
        dto.setCode(department.getCode());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setActive(department.isActive());
        return dto;
    }

    public static UpdateDepartmentDTO fromDepartmentDTO(DepartmentDTO departmentDTO) {
        if (departmentDTO == null) {
            throw new IllegalArgumentException("DepartmentDTO nie może być null");
        }

        UpdateDepartmentDTO dto = new UpdateDepartmentDTO();
        dto.setId(departmentDTO.getId());
        dto.setCode(departmentDTO.getCode());
        dto.setName(departmentDTO.getName());
        dto.setDescription(departmentDTO.getDescription());
        dto.setActive(departmentDTO.isActive());
        return dto;
    }
}



