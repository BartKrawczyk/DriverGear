package pl.programodawca.drivergear.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import pl.programodawca.drivergear.model.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    private Long id;

    @NotBlank(message = "Nazwa użytkownika nie może być pusta")
    private String username;

    @NotBlank(message = "Hasło nie może być puste")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()]).{8,}$",
            message = "Hasło musi zawierać minimum 8 znaków, wielką literę, małą literę, cyfrę i znak specjalny"
    )
    private String password;

    @NotBlank(message = "Potwierdzenie hasła nie może być puste")
    private String confirmPassword;

    // Walidacja zgodności haseł
    @AssertTrue(message = "Hasła muszą być takie same")
    public boolean isPasswordsEqual() {
        return password != null && confirmPassword != null && password.equals(confirmPassword);
    }


    @NotBlank(message = "Imię nie może być puste")
    private String firstName;

    @NotBlank(message = "Nazwisko nie może być puste")
    private String lastName;

    private UserRole role = UserRole.USER;

    private boolean locked = false;

}



