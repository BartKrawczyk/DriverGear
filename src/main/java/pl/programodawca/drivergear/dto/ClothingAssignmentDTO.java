package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.AssignmentStatus;
import pl.programodawca.drivergear.model.ClothingAssignment;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ClothingAssignmentDTO {
    private Long id;

    @NotNull(message = "Pracownik jest wymagany")
    private Long employeeId;
    private String employeeName;

    @NotNull(message = "Przydział odzieżowy jest wymagany")
    private Long positionClothingAllowanceId;
    private String clothingTypeName;
    private Long clothingTypeId;
    private String positionName;
    private String departmentName;

    private LocalDate assignmentDate;
    private LocalDate expiryDate;
    private AssignmentStatus status;
    private String size;
    private Integer quantity;
    private Boolean issuedToEmployee;
    private LocalDate issuedDate;
    private Boolean eligibleForCompensation;
    private String employeeNumber;
    private String notes;

    public static ClothingAssignmentDTO fromEntity(ClothingAssignment assignment) {
        if (assignment == null) {
            return null;
        }

        return ClothingAssignmentDTO.builder()
                .id(assignment.getId())
                .employeeId(assignment.getEmployee().getId())
                .employeeName(assignment.getEmployee().getFullName())
                .positionClothingAllowanceId(assignment.getPositionClothingAllowance().getId())
                // Get the clothing type name directly from the assignment's clothingType
                .clothingTypeName(assignment.getClothingType() != null ? 
                        assignment.getClothingType().getName() : 
                        "Brak typu odzieży")
                // Get the clothing type ID directly from the assignment's clothingType
                .clothingTypeId(assignment.getClothingType() != null ? 
                        assignment.getClothingType().getId() : 
                        null)
                .positionName(assignment.getPositionClothingAllowance().getPosition().getName())
                .departmentName(assignment.getPositionClothingAllowance().getDepartment().getName())
                .assignmentDate(assignment.getAssignmentDate())
                .expiryDate(assignment.getExpiryDate())
                .status(assignment.getStatus())
                .size(assignment.getSize())
                .quantity(assignment.getQuantity())
                .issuedToEmployee(assignment.getIssuedToEmployee())
                .issuedDate(assignment.getIssuedDate())
                .eligibleForCompensation(assignment.getEligibleForCompensation())
                .employeeNumber(assignment.getEmployee().getEmployeeNumber())
                .notes(assignment.getNotes())
                .build();
    }

    public static List<ClothingAssignmentDTO> fromEntities(List<ClothingAssignment> assignments) {
        if (assignments == null) {
            return Collections.emptyList();
        }
        return assignments.stream()
                .map(ClothingAssignmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ClothingAssignment toEntity() {
        ClothingAssignment assignment = new ClothingAssignment();
        // Note: Employee and PositionClothingAllowance must be set by the service layer
        assignment.setAssignmentDate(this.assignmentDate);
        assignment.setExpiryDate(this.expiryDate);
        assignment.setStatus(this.status);
        assignment.setSize(this.size);
        assignment.setQuantity(this.quantity);
        assignment.setIssuedToEmployee(this.issuedToEmployee);
        assignment.setIssuedDate(this.issuedDate);
        assignment.setEligibleForCompensation(this.eligibleForCompensation);
        assignment.setNotes(this.notes);
        return assignment;
    }

    /**
     * Calculates the validity period in months between the assignment date and expiry date.
     * 
     * @return The number of months between assignmentDate and expiryDate, or null if either date is null
     */
    public Integer getValidityMonths() {
        if (assignmentDate == null || expiryDate == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(assignmentDate, expiryDate);
    }
}
