package pl.programodawca.drivergear.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clothing_entitlement")
public class ClothingEntitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "clothing_id", nullable = false)
    private Clothing clothing;

    private LocalDate eligibleFrom;

    private LocalDate eligibleTo;

    @Column(name = "used_quantity", columnDefinition = "integer default 0")
    private Integer usedQuantity = 0;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Clothing getClothing() {
        return clothing;
    }

    public void setClothing(Clothing clothing) {
        this.clothing = clothing;
    }

    public LocalDate getEligibleFrom() {
        return eligibleFrom;
    }

    public void setEligibleFrom(LocalDate eligibleFrom) {
        this.eligibleFrom = eligibleFrom;
    }

    public LocalDate getEligibleTo() {
        return eligibleTo;
    }

    public void setEligibleTo(LocalDate eligibleTo) {
        this.eligibleTo = eligibleTo;
    }

    public int getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(int usedQuantity) {
        this.usedQuantity = usedQuantity;
    }
}
