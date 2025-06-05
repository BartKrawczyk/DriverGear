package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a standard clothing allowance for a specific department and position combination.
 * This defines what clothing items are standard for a position.
 */
@Entity
@Table(name = "position_clothing_allowances")
@Getter
@Setter
@NoArgsConstructor
public class PositionClothingAllowance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column
    private String notes;       // dodatkowe uwagi

    @Column(nullable = false)
    private Boolean active = true;  // czy przydział jest aktywny

    @OneToMany(mappedBy = "positionClothingAllowance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PositionClothingItem> clothingItems = new HashSet<>();

    @OneToMany(mappedBy = "positionClothingAllowance")
    private Set<ClothingAssignment> assignments = new HashSet<>();

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
