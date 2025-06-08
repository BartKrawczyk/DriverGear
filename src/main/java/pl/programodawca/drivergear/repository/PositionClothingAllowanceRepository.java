package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.PositionClothingAllowance;

import java.util.List;

@Repository
public interface PositionClothingAllowanceRepository extends JpaRepository<PositionClothingAllowance, Long> {
    List<PositionClothingAllowance> findByPositionId(Long positionId);

    List<PositionClothingAllowance> findByDepartmentId(Long departmentId);

    List<PositionClothingAllowance> findByDepartmentIdAndPositionId(Long departmentId, Long positionId);

    List<PositionClothingAllowance> findByActiveTrue();
}
