package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.repository.EmployeeRepository;
import pl.programodawca.drivergear.service.EmployeeService;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Employee not found with id: " + id));
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, Employee employee) {
        Employee existingEmployee = findById(id);
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setIdentificationNumber(employee.getIdentificationNumber());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setMonetaryEquivalent(employee.getMonetaryEquivalent());
        // Update other fields if necessary
        return employeeRepository.save(existingEmployee);
    }

    @Override
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}