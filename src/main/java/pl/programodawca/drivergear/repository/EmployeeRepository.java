package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.model.Employee;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByPositionId(Long positionId);
    int countByPositionId(Long positionId);
    List<Employee> findByActiveTrue();
    int countByDepartmentId(Long departmentId);

    int countByDepartment(Department department);
}
