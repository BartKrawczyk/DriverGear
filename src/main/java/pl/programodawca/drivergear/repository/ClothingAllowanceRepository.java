package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.model.ClothingAllowance;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClothingAllowanceRepository extends JpaRepository<ClothingAllowance, Long> {
    List<ClothingAllowance> findByEmployeeId(Long employeeId);
    List<ClothingAllowance> findByEmployeeIdAndStatus(Long employeeId, AllowanceStatus status);
    List<ClothingAllowance> findByPositionId(Long positionId);
    List<ClothingAllowance> findByStatus(AllowanceStatus status);

    @Query("SELECT a FROM ClothingAllowance a WHERE a.startDate <= :date AND a.endDate >= :date")
    List<ClothingAllowance> findActiveOnDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM ClothingAllowance a WHERE a.endDate < :date AND a.status = :status")
    List<ClothingAllowance> findExpiredAllowances(@Param("date") LocalDate date, @Param("status") AllowanceStatus status);
}
