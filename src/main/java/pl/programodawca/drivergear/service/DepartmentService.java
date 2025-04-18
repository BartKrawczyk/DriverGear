package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Department;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;

import java.util.List;

public interface DepartmentService {
    DepartmentDTO createDepartment(CreateDepartmentDTO dto);
    DepartmentDTO updateDepartment(Long id, CreateDepartmentDTO dto);
    DepartmentDTO getDepartmentById(Long id);
    List<DepartmentDTO> getAllDepartments();
    void deleteDepartment(Long id);
}
