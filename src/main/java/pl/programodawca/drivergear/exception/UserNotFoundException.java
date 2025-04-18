package pl.programodawca.drivergear.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        super("Nie znaleziono użytkownika o ID: " + id);
    }
}
