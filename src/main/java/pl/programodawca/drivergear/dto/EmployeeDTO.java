package pl.programodawca.drivergear.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.programodawca.drivergear.model.Employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.programodawca.drivergear.model.Employee;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private String departmentName;
    private boolean departmentActive;

    public static EmployeeDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());

        if (employee.getPosition() != null) {
            dto.setPositionId(employee.getPosition().getId());
            dto.setPositionName(employee.getPosition().getName());
            dto.setPositionActive(employee.getPosition().isActive());

            if (employee.getPosition().getDepartment() != null) {
                dto.setDepartmentName(employee.getPosition().getDepartment().getName());
                dto.setDepartmentActive(employee.getPosition().getDepartment().isActive());
            }
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
        // Position będzie ustawiana w serwisie
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}



