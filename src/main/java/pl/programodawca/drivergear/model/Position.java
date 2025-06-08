package pl.programodawca.drivergear.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "positions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "department_id"},
                name = "uk_position_name_department")
})
@Getter
@Setter
@NoArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "position")
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(mappedBy = "position")
    private Set<PositionClothingAllowance> standardAllowances = new HashSet<>();
}






