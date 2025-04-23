package pl.programodawca.drivergear.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.programodawca.drivergear.model.AppUser;
import pl.programodawca.drivergear.repository.AppUserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService_OLD implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService_OLD(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Pobierz użytkownika z bazy danych na podstawie nazwy użytkownika
        Optional<AppUser> optionalUser = appUserRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("AppUser not found with username: " + username);
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
