package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.model.Department;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;

import java.util.List;

public interface DepartmentService {

    DepartmentDTO getDepartmentById(Long id);

    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO findById(Long id);
    DepartmentDTO createDepartment(CreateDepartmentDTO createDepartmentDTO);  // zmiana parametru
    DepartmentDTO updateDepartment(Long id, UpdateDepartmentDTO updateDepartmentDTO);  // zmiana parametru
    void deleteDepartment(Long id);
}