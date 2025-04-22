package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.EmployeeDTO;
import pl.programodawca.drivergear.model.Employee;

import java.util.List;

public interface EmployeeService {
    Employee findById(Long id);
    List<Employee> findAll();
    Employee save(Employee employee);
    Employee update(Long id, Employee employee);
    void delete(Long id);
    List<EmployeeDTO> getAllActiveEmployees();
}

