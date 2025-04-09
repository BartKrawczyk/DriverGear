package pl.programodawca.drivergear.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String identificationNumber;

    private String position;

    @Column(nullable = false)
    private Double monetaryEquivalent = 0.0;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ClothingUsage> history;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ClothingEntitlement> entitlements;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Double getMonetaryEquivalent() {
        return monetaryEquivalent;
    }

    public void setMonetaryEquivalent(Double monetaryEquivalent) {
        this.monetaryEquivalent = monetaryEquivalent;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<ClothingUsage> getHistory() {
        return history;
    }

    public void setHistory(List<ClothingUsage> history) {
        this.history = history;
    }

    public List<ClothingEntitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<ClothingEntitlement> entitlements) {
        this.entitlements = entitlements;
    }
}