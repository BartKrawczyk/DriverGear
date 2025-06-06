package pl.programodawca.drivergear.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByClothingAssignmentId(Long clothingAssignmentId);

    @Query("SELECT COUNT(c) FROM ClothingCompensation c WHERE c.clothingAssignment.id = :assignmentId")
    long countByClothingAssignmentId(@Param("assignmentId") Long assignmentId);



    @Query("SELECT COUNT(c) FROM ClothingCompensation c " +
            "WHERE c.employee.id = :employeeId " +
            "AND c.clothingAssignment.clothingType.id = :clothingTypeId " +
            "AND c.periodStart = :periodStart " +
            "AND c.periodEnd = :periodEnd")
    long countByUniqueAssignment(@Param("employeeId") Long employeeId,
                                 @Param("clothingTypeId") Long clothingTypeId,
                                 @Param("periodStart") LocalDate periodStart,
                                 @Param("periodEnd") LocalDate periodEnd);

    @Query("SELECT COUNT(c) FROM ClothingCompensation c " +
            "WHERE c.employee.id = :employeeId " +
            "AND c.clothingAssignment.clothingType.id = :clothingTypeId")
    long countByEmployeeAndClothingType(@Param("employeeId") Long employeeId,
                                        @Param("clothingTypeId") Long clothingTypeId);

    // Find all paid compensations with pagination and filtering
    @Query("SELECT c FROM ClothingCompensation c " +
           "WHERE c.status = pl.programodawca.drivergear.model.CompensationStatus.PAID " +
           "AND (:firstName IS NULL OR LOWER(c.employee.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
           "AND (:lastName IS NULL OR LOWER(c.employee.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
           "AND (:employeeNumber IS NULL OR c.employee.employeeNumber = :employeeNumber) " +
           "AND (:clothingTypeId IS NULL OR c.clothingAssignment.clothingType.id = :clothingTypeId) " +
           "AND (:startDate IS NULL OR c.paymentDate >= :startDate) " +
           "AND (:endDate IS NULL OR c.paymentDate <= :endDate) " +
           "ORDER BY c.paymentDate DESC")
    Page<ClothingCompensation> findPaidCompensationHistory(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("employeeNumber") String employeeNumber,
            @Param("clothingTypeId") Long clothingTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
