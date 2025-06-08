package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.AppUser;


import java.util.List;
import java.util.Optional;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Szuka użytkowników, których username zawiera podany ciąg znaków (ignorując wielkość liter)
    List<AppUser> findByUsernameContainingIgnoreCase(String username);

    // Szuka użytkownika o dokładnym username
    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);
}



