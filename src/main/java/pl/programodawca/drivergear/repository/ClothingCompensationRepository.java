package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.ClothingCompensation;
import pl.programodawca.drivergear.model.CompensationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClothingCompensationRepository extends JpaRepository<ClothingCompensation, Long> {
    List<ClothingCompensation> findByEmployeeIdAndStatus(Long employeeId, CompensationStatus status);

    List<ClothingCompensation> findByStatusAndPaymentDateBetween(
            CompensationStatus status,
            LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT SUM(c.amount) FROM ClothingCompensation c WHERE c.employee.id = :employeeId AND c.status = :status")
    BigDecimal sumCompensationsByEmployeeAndStatus(Long employeeId, CompensationStatus status);

    List<ClothingCompensation> findByEmployeeId(Long employeeId);

    @Query("SELECT c FROM ClothingCompensation c WHERE c.clothingAssignment.positionClothingAllowance.department.id = :departmentId")
    List<ClothingCompensation> findByDepartmentId(Long departmentId);

    List<ClothingCompensation> findByClothingAssignmentId(Long assignmentId);

    @Query("SELECT c FROM ClothingCompensation c WHERE c.clothingAssignment.positionClothingAllowance.position.id = :positionId")
    List<ClothingCompensation> findByPositionId(Long positionId);
}
