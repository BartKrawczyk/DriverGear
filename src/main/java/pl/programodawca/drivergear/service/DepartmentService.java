package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Department;

import java.util.List;

public interface DepartmentService {
    Department findById(Long id);
    List<Department> findAll();
    Department save(Department department);
    Department update(Long id, Department department);
    void delete(Long id);
}