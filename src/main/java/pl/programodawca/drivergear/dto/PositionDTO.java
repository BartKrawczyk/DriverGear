package pl.programodawca.drivergear.dto;

import lombok.*;
import pl.programodawca.drivergear.model.Position;

@Getter
@Setter
@NoArgsConstructor
public class PositionDTO {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Long departmentId;
    private String departmentName;

    public static PositionDTO fromEntity(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setDescription(position.getDescription());
        dto.setActive(position.isActive());
        dto.setDepartmentId(position.getDepartment().getId());
        dto.setDepartmentName(position.getDepartment().getName());
        return dto;
    }
}






