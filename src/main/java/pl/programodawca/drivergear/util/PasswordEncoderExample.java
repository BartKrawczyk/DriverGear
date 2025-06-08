package pl.programodawca.drivergear.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderExample {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Zakodowane hasło: " + encodedPassword);
    }
}
