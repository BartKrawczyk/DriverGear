package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Long departmentId;
    private String departmentName;
}


