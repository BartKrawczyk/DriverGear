package pl.programodawca.drivergear.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.programodawca.drivergear.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); // Metoda wyszukiwania użytkownika po nazwie użytkownika
}


