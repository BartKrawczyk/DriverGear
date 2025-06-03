package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clothing_sizes")
@Getter
@Setter
@NoArgsConstructor
public class ClothingSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "clothing_type_id", nullable = false)
    private ClothingType clothingType;

    @Column(nullable = false)
    private String size;  // rozmiar odzieży (S, M, L, XL, etc. lub numeryczny)

    @Column
    private String notes;  // dodatkowe uwagi dotyczące rozmiaru

    @Column(nullable = false)
    private boolean active = true;  // czy rozmiar jest aktualny
}