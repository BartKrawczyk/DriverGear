package pl.programodawca.drivergear.service.impl;

import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.repository.DepartmentRepository;
import pl.programodawca.drivergear.service.DepartmentService;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Department not found with id: " + id));
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department update(Long id, Department department) {
        Department existingDepartment = findById(id);
        existingDepartment.setName(department.getName());
        return departmentRepository.save(existingDepartment);
    }

    @Override
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}