package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.programodawca.drivergear.model.Position;

import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {
    boolean existsByNameAndDepartmentId(String name, Long departmentId);
    boolean existsByCodeAndDepartmentId(String code, Long departmentId);
    boolean existsByNameAndDepartmentIdAndIdNot(String name, Long departmentId, Long id);
    boolean existsByCodeAndDepartmentIdAndIdNot(String code, Long departmentId, Long id);
}


