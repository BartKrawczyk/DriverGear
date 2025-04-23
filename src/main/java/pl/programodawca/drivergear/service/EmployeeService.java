package pl.programodawca.drivergear.service;

import pl.programodawca.drivergear.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeDTO> findAll(Pageable pageable);
    Page<EmployeeDTO> findAllByActive(boolean active, Pageable pageable);  // nowa metoda
    Page<EmployeeDTO> findAllByPosition(Long positionId, Pageable pageable);
    Page<EmployeeDTO> findAllByDepartment(Long departmentId, Pageable pageable);
    EmployeeDTO findById(Long id);
    EmployeeDTO create(EmployeeDTO employeeDTO);
    EmployeeDTO update(Long id, EmployeeDTO employeeDTO);
    void delete(Long id);
    boolean existsByPosition(Long positionId);
    long countByPosition(Long positionId);
}




