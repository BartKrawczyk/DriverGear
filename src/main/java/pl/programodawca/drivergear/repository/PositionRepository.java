package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Position;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByDepartmentId(Long departmentId);
    List<Position> findByDepartmentIdAndActive(Long departmentId, boolean active);
    List<Position> findByActive(boolean active);

    // Istniejące metody dla name
    boolean existsByNameAndDepartmentId(String name, Long departmentId);
    boolean existsByNameAndDepartmentIdAndIdNot(String name, Long departmentId, Long id);

    // Nowe metody dla code
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<Position> findByCode(String code);
}







