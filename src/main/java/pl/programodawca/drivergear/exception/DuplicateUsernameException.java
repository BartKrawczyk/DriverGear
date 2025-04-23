package pl.programodawca.drivergear.exception;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("Użytkownik o nazwie " + username + " już istnieje");
    }
}

