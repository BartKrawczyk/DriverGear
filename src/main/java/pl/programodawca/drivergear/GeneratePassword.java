package pl.programodawca.drivergear;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {
    public static void main(String[] args) {
        String rawPassword = "testpassword";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("New hashed password: " + hashedPassword);
    }
}