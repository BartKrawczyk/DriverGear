package pl.programodawca.drivergear.dto;

import lombok.*;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Position;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Set<Long> positionIds = Collections.emptySet(); // inicjalizacja domyślną wartością
    private boolean active;

    public static DepartmentDTO fromEntity(Department department) {
        if (department == null) {
            throw new IllegalArgumentException("Department nie może być null");
        }

        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setCode(department.getCode());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setActive(department.isActive());
        dto.setPositionIds(department.getPositions().stream()
                .map(Position::getId)
                .collect(Collectors.toSet()));
        return dto;
    }
}





