package pl.programodawca.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {

    public static void main(String[] args) {
        // Tworzymy obiekt BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Podaj hasło, które chcesz zakodować
        String rawPassword = "testpassword";

        // Haszowanie hasła
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Wyświetlenie zakodowanego hasła
        System.out.println("Zakodowane hasło: " + encodedPassword);
    }
}
