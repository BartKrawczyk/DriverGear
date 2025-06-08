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

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(nullable = false)
    private boolean active = true;  // domyślnie true przy tworzeniu

    // Inne potrzebne pola, np. dane kontaktowe, data zatrudnienia itp.
    @Column(name = "employee_number", unique = true, nullable = false)
    @Pattern(regexp = "^[A-Z]{2}\\d{5}$", message = "Numer pracownika musi być w formacie XX00000")
    private String employeeNumber;  // numer służbowy pracownika

    @Column(nullable = false)
    private LocalDate hireDate;    // data zatrudnienia

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;  // dział

    @Column
    @Pattern(regexp = "^(\\+?\\d{9,13})?$", message = "Nieprawidłowy format numeru telefonu")
    private String phoneNumber;    // numer telefonu służbowego

    @Column
    @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Nieprawidłowy format adresu email")
    private String email;         // email służbowy

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;        // płeć (istotne przy przydziale odzieży)

    @OneToMany(mappedBy = "employee")
    private Set<ClothingSize> clothingSizes = new HashSet<>();  // preferowane rozmiary różnych typów odzieży

    @OneToMany(mappedBy = "employee")
    private Set<ClothingAllowance> clothingAllowances = new HashSet<>();  // przydziały odzieży

    @OneToMany(mappedBy = "employee")
    private Set<WarehouseTransaction> warehouseTransactions = new HashSet<>();  // historia wydań/zwrotów

    @Column(nullable = false)
    private LocalDateTime createdAt;    // data utworzenia rekordu

    @Column
    private LocalDateTime updatedAt;    // data ostatniej modyfikacji

    @Column
    private LocalDate deactivationDate; // data dezaktywacji (jeśli pracownik nieaktywny)

    @OneToMany(mappedBy = "employee")
    private Set<ClothingCompensation> clothingCompensations = new HashSet<>(); // ekwiwalent

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Set<PositionClothingAllowance> getStandardAllowances() {
        return position != null ? position.getStandardAllowances() : new HashSet<>();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
