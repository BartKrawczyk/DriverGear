package pl.programodawca.drivergear.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clothing")
public class Clothing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String size;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer eligibilityPeriod;

    @Column(nullable = false, unique = true) // Barcode musi być unikalny
    private String barcode;

    @OneToMany(mappedBy = "clothing", cascade = CascadeType.ALL)
    private List<ClothingUsage> usageHistory;

    @OneToMany(mappedBy = "clothing", cascade = CascadeType.ALL)
    private List<ClothingEntitlement> entitlements;

    // Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEligibilityPeriod() {
        return eligibilityPeriod;
    }

    public void setEligibilityPeriod(Integer eligibilityPeriod) {
        this.eligibilityPeriod = eligibilityPeriod;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public List<ClothingUsage> getUsageHistory() {
        return usageHistory;
    }

    public void setUsageHistory(List<ClothingUsage> usageHistory) {
        this.usageHistory = usageHistory;
    }

    public List<ClothingEntitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<ClothingEntitlement> entitlements) {
        this.entitlements = entitlements;
    }
}