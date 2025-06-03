package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a clothing item in a position clothing allowance.
 * This allows a position clothing allowance to have multiple clothing items.
 */
@Entity
@Table(name = "position_clothing_items")
@Getter
@Setter
@NoArgsConstructor
public class PositionClothingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "position_clothing_allowance_id", nullable = false)
    private PositionClothingAllowance positionClothingAllowance;

    @ManyToOne
    @JoinColumn(name = "clothing_type_id", nullable = false)
    private ClothingType clothingType;

    @Column(nullable = false)
    private Integer quantity;    // ilość sztuk

    @Column(nullable = false)
    private Integer validityPeriod;  // okres ważności w miesiącach

    @Column(nullable = false)
    private Boolean mandatory;   // czy element jest obowiązkowy

    @Column
    private String notes;       // dodatkowe uwagi

    // These fields are for informational purposes only and are always derived from the associated ClothingType
    @Transient
    private String barcode;     // kod kreskowy dla elementu odzieży - derived from ClothingType

    @Transient
    private BigDecimal compensationAmount;  // kwota ekwiwalentu za element - derived from ClothingType

    public String getBarcode() {
        return this.clothingType != null ? this.clothingType.getBarcode() : null;
    }

    public BigDecimal getCompensationAmount() {
        return this.clothingType != null ? this.clothingType.getCompensationValue() : BigDecimal.ZERO;
    }

    // These setters are kept for compatibility but don't actually store the values
    public void setBarcode(String barcode) {
        // No-op - barcode is always derived from ClothingType
    }

    public void setCompensationAmount(BigDecimal compensationAmount) {
        // No-op - compensationAmount is always derived from ClothingType
    }

    @Column(nullable = false)
    private Boolean active = true;  // czy przydział jest aktywny

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
