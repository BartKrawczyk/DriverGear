package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a clothing assignment to an individual employee based on their position's allowance.
 * This entity tracks when specific clothing items are assigned to employees.
 */
@Entity
@Table(name = "clothing_assignments")
@Getter
@Setter
@NoArgsConstructor
public class ClothingAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "position_clothing_allowance_id", nullable = false)
    private PositionClothingAllowance positionClothingAllowance;

    @Column(nullable = false)
    private LocalDate assignmentDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @Column
    private String size;

    @Column
    private Integer quantity = 1;

    @Column
    private Boolean issuedToEmployee = false;

    @Column
    private LocalDate issuedDate;

    @Column
    private Boolean eligibleForCompensation = false;

    @Column
    private String notes;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "clothing_type_id")
    private ClothingType clothingType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}