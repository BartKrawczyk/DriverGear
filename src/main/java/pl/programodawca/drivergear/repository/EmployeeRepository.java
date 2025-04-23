package pl.programodawca.drivergear.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Employee;
import pl.programodawca.drivergear.model.Position;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Dla findAll(Pageable pageable) - dziedziczone z JpaRepository

    // Dla findAllByPosition
    Page<Employee> findAllByPosition(Position position, Pageable pageable);

    // Dla findAllByDepartment
    Page<Employee> findAllByPosition_Department_Id(Long departmentId, Pageable pageable);

    // Dla findById(Long id) - dziedziczone z JpaRepository

    // Dla create - save(Employee employee) - dziedziczone z JpaRepository

    // Dla update - save(Employee employee) - dziedziczone z JpaRepository

    // Dla delete - deleteById(Long id) - dziedziczone z JpaRepository
    // Dla delete - existsById(Long id) - dziedziczone z JpaRepository

    // Dla existsByPosition
    boolean existsByPosition_Id(Long positionId);

    // Dla countByPosition
    long countByPosition_Id(Long positionId);

    Page<Employee> findAllByActive(boolean active, Pageable pageable);

    List<Employee> findByPosition_Department_Id(Long departmentId);

}



