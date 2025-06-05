package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a compensation for clothing that an employee didn't receive.
 */
@Entity
@Table(name = "clothing_compensations")
@Getter
@Setter
@NoArgsConstructor
public class ClothingCompensation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "clothing_assignment_id", nullable = false)
    private ClothingAssignment clothingAssignment;  // za jaki przydział

    @Column(nullable = false)
    private BigDecimal amount;  // kwota ekwiwalentu

    @Column(nullable = false)
    private LocalDate periodStart;  // początek okresu za który przysługuje ekwiwalent

    @Column(nullable = false)
    private LocalDate periodEnd;    // koniec okresu

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompensationStatus status = CompensationStatus.PENDING;  // status wypłaty

    @Column
    private LocalDate paymentDate;  // data wypłaty

    @Column
    private String notes;  // uwagi

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
