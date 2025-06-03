package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clothing_types")
@Getter
@Setter
@NoArgsConstructor
public class ClothingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(unique = true)
    private String barcode;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal compensationValue;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer standardLifetimeMonths;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "clothingType")
    private Set<PositionClothingItem> clothingItems = new HashSet<>();
}
