package pl.programodawca.drivergear.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.ClothingAssignment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClothingAssignmentRepository extends JpaRepository<ClothingAssignment, Long> {

    // Find assignments by employee ID
    List<ClothingAssignment> findByEmployeeId(Long employeeId);

    // Find assignments by employee ID and status
    List<ClothingAssignment> findByEmployeeIdAndStatus(Long employeeId, AssignmentStatus status);

    // Find assignments by position clothing allowance ID
    List<ClothingAssignment> findByPositionClothingAllowanceId(Long positionClothingAllowanceId);

    // Find assignments by status
    List<ClothingAssignment> findByStatus(AssignmentStatus status);

    // Find assignments that are active on a specific date
    @Query("SELECT a FROM ClothingAssignment a WHERE a.assignmentDate <= :date AND a.expiryDate >= :date")
    List<ClothingAssignment> findActiveOnDate(@Param("date") LocalDate date);

    // Find assignments that have expired but still have a non-expired status
    @Query("SELECT a FROM ClothingAssignment a WHERE a.expiryDate < :date AND a.status IN :statuses")
    List<ClothingAssignment> findExpiredAssignments(@Param("date") LocalDate date, @Param("statuses") List<AssignmentStatus> statuses);

    // Find assignments eligible for compensation
    List<ClothingAssignment> findByEligibleForCompensationTrue();

    // Find assignments by employee ID and position clothing allowance ID
    List<ClothingAssignment> findByEmployeeIdAndPositionClothingAllowanceId(Long employeeId, Long positionClothingAllowanceId);

    // Find assignments by employee ID that are not issued
    List<ClothingAssignment> findByEmployeeIdAndIssuedToEmployeeFalse(Long employeeId);

    // Find assignments by employee ID that are issued
    List<ClothingAssignment> findByEmployeeIdAndIssuedToEmployeeTrue(Long employeeId);

    // Find all issued assignments with pagination and filtering
    @Query("SELECT a FROM ClothingAssignment a " +
           "WHERE a.issuedToEmployee = true " +
           "AND (:firstName IS NULL OR LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
           "AND (:lastName IS NULL OR LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
           "AND (:employeeNumber IS NULL OR a.employee.employeeNumber = :employeeNumber) " +
           "AND (:clothingTypeId IS NULL OR a.clothingType.id = :clothingTypeId) " +
           "AND (:startDate IS NULL OR a.issuedDate >= :startDate) " +
           "AND (:endDate IS NULL OR a.issuedDate <= :endDate) " +
           "ORDER BY a.issuedDate DESC")
    Page<ClothingAssignment> findIssuedClothingHistory(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("employeeNumber") String employeeNumber,
            @Param("clothingTypeId") Long clothingTypeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
