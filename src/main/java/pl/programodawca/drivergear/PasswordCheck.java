package pl.programodawca.drivergear;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        String rawPassword = "testpassword";
        String hashedPassword = "$2a$10$ftimIDkoY4NiIQoi.gFjkOMDNsO4b2Akt.lyrije0FT.B6tL4DMMa";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        boolean isMatching = passwordEncoder.matches(rawPassword, hashedPassword);
        System.out.println("Does the raw password match the hashed password? " + isMatching);
    }
}