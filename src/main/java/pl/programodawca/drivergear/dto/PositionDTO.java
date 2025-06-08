package pl.programodawca.drivergear.dto;

import lombok.*;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.dto.EmployeeDTO;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class PositionDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private Long departmentId;
    private String departmentName;
    private Set<EmployeeDTO> employees = new HashSet<>();

    public static PositionDTO fromEntity(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setCode(position.getCode());
        dto.setDescription(position.getDescription());
        dto.setActive(position.isActive());
        dto.setDepartmentId(position.getDepartment().getId());
        dto.setDepartmentName(position.getDepartment().getName());

        if (position.getEmployees() != null) {
            dto.setEmployees(position.getEmployees().stream()
                    .map(EmployeeDTO::fromEntity)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public boolean hasEmployees() {
        return employees != null && !employees.isEmpty();
    }
}








