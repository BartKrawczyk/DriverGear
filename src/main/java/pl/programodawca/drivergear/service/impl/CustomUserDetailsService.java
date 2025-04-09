package pl.programodawca.drivergear.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.User;
import pl.programodawca.drivergear.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Pobierz użytkownika z bazy danych na podstawie nazwy użytkownika
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        AppUser appUser = optionalUser.get();

        // Zwróć obiekt `UserDetails` rozpoznawany przez Spring Security
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword()) // Hasło powinno być zakodowane w bazie
                .authorities("USER") // Możesz tutaj przypisać role użytkownika
                .build();
    }
}
