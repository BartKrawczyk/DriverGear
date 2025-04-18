package pl.programodawca.drivergear.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "identification_number", unique = true, nullable = false)
    private String identificationNumber;

    @Column(nullable = false)
    private String position;

    @Column(name = "monetary_equivalent", nullable = false)
    private Double monetaryEquivalent = 0.0;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothingUsage> history = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClothingEntitlement> entitlements = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    // Metody pomocnicze do zarządzania relacjami
    public void addClothingUsage(ClothingUsage usage) {
        history.add(usage);
        usage.setEmployee(this);
    }

    public void removeClothingUsage(ClothingUsage usage) {
        history.remove(usage);
        usage.setEmployee(null);
    }

    public void addClothingEntitlement(ClothingEntitlement entitlement) {
        entitlements.add(entitlement);
        entitlement.setEmployee(this);
    }

    public void removeClothingEntitlement(ClothingEntitlement entitlement) {
        entitlements.remove(entitlement);
        entitlement.setEmployee(null);
    }
}