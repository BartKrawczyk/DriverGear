package pl.programodawca.drivergear.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "Imię jest wymagane")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private String lastName;

    @NotNull(message = "Stanowisko jest wymagane")
    private Long positionId;
    private String positionName;
    private boolean positionActive;

    private Long departmentId;
    private String departmentName;
    private boolean departmentActive;

    @NotBlank(message = "Numer pracownika jest wymagany")
    @Pattern(regexp = "^[A-Z]{2}\\d{5}$", message = "Numer pracownika musi być w formacie XX00000")
    private String employeeNumber;

    @NotNull(message = "Data zatrudnienia jest wymagana")
    private LocalDate hireDate;

    @Pattern(regexp = "^(\\+?\\d{9,13})?$", message = "Nieprawidłowy format numeru telefonu")
    private String phoneNumber;

    @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Nieprawidłowy format adresu email")
    private String email;

    private Gender gender;

    private boolean active = true;

    private LocalDate deactivationDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static EmployeeDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmployeeNumber(employee.getEmployeeNumber());
        dto.setHireDate(employee.getHireDate());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setEmail(employee.getEmail());
        dto.setGender(employee.getGender());
        dto.setActive(employee.isActive());
        dto.setDeactivationDate(employee.getDeactivationDate());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());

        if (employee.getPosition() != null) {
            dto.setPositionId(employee.getPosition().getId());
            dto.setPositionName(employee.getPosition().getName());
            dto.setPositionActive(employee.getPosition().isActive());

            if (employee.getPosition().getDepartment() != null) {
                dto.setDepartmentId(employee.getPosition().getDepartment().getId());
                dto.setDepartmentName(employee.getPosition().getDepartment().getName());
                dto.setDepartmentActive(employee.getPosition().getDepartment().isActive());
            }
        }

        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getName());
            dto.setDepartmentActive(employee.getDepartment().isActive());
        }

        return dto;
    }

    public static List<EmployeeDTO> fromEntities(Collection<Employee> employees) {
        if (employees == null) {
            return List.of();
        }
        return employees.stream()
                .map(EmployeeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Employee toEntity() {
        Employee employee = new Employee();
        updateEntity(employee);
        return employee;
    }

    public void updateEntity(Employee employee) {
        employee.setFirstName(this.firstName);
        employee.setLastName(this.lastName);
        employee.setEmployeeNumber(this.employeeNumber);
        employee.setHireDate(this.hireDate);
        employee.setPhoneNumber(this.phoneNumber);
        employee.setEmail(this.email);
        employee.setGender(this.gender);
        employee.setActive(this.active);
        employee.setDeactivationDate(this.deactivationDate);
        // Position będzie ustawiana w serwisie
        // Department będzie ustawiony w serwisie jeśli potrzebne
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
