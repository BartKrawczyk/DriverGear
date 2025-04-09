package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Przykład: znajdź pracowników po nazwisku
    List<Employee> findByLastName(String lastName);

    // Przykład: znajdź pracowników po stanowisku
    List<Employee> findByPosition(String position);

    // Przykład: znajdź aktywnych pracowników
    List<Employee> findByIsActive(boolean isActive);
}