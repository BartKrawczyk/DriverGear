package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Metoda zwraca Optional, aby bezpiecznie obsługiwać brak wyników
    Optional<Role> findByName(String name);
}


