package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.programodawca.drivergear.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Zwraca listę wszystkich aktywnych działów
     */
    @Query("SELECT d FROM Department d WHERE d.active = true")
    List<Department> findAllActive();

    /**
     * Znajduje aktywny dział po jego ID
     * @param id ID działu
     * @return Optional zawierający dział jeśli istnieje i jest aktywny
     */
    @Query("SELECT d FROM Department d WHERE d.id = :id AND d.active = true")
    Optional<Department> findActiveById(@Param("id") Long id);

    /**
     * Sprawdza czy istnieje dział o podanym kodzie
     */
    boolean existsByCode(String code);

    /**
     * Sprawdza czy istnieje inny dział o podanym kodzie (używane przy edycji)
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Sprawdza czy dział ma aktywnych pracowników
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Employee e " +
            "WHERE e.position.department.id = :departmentId " +
            "AND e.active = true")
    boolean hasActiveEmployees(@Param("departmentId") Long departmentId);



    /**
     * Dezaktywuje dział (soft delete)
     */
    @Modifying
    @Transactional
    @Query("UPDATE Department d SET d.active = false WHERE d.id = :id")
    void softDelete(@Param("id") Long id);
}






