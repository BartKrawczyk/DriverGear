package pl.programodawca.drivergear.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pl.programodawca.drivergear.model.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private UserRole role;
    private boolean locked;
}

