package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_transactions")
@Getter
@Setter
@NoArgsConstructor
public class WarehouseTransaction {
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
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;  // ISSUE (wydanie) or RETURN (zwrot)

    @Column(nullable = false)
    private LocalDate transactionDate;  // data transakcji

    @Column(nullable = false)
    private Integer quantity;  // ilość wydanych/zwróconych sztuk

    @Column
    private String size;  // rozmiar wydanej/zwróconej odzieży

    @Column
    private String notes;  // dodatkowe uwagi

    @Column(nullable = false)
    private LocalDateTime createdAt;  // data utworzenia rekordu

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}