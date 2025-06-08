package pl.programodawca.drivergear.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    @NotBlank(message = "Aktualne hasło nie może być puste")
    private String currentPassword;

    @NotBlank(message = "Nowe hasło nie może być puste")
    @Size(min = 6, message = "Nowe hasło musi mieć minimum 6 znaków")
    private String newPassword;

    @NotBlank(message = "Potwierdzenie hasła nie może być puste")
    private String confirmPassword;
}

