package pl.programodawca.drivergear.model;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Powiązanie z ubraniem
    @OneToOne
    @JoinColumn(name = "clothing_id", nullable = false)
    private Clothing clothing;

    private int quantity; // Ilość na magazynie

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Settery, gettery, konstruktor domyślny itp.
    public Storage() {}

    public Storage(Clothing clothing, int quantity) {
        this.clothing = clothing;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clothing getClothing() {
        return clothing;
    }

    public void setClothing(Clothing clothing) {
        this.clothing = clothing;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

