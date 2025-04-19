package pl.programodawca.drivergear.exception;

public class PositionAlreadyExistsException extends RuntimeException {
    public PositionAlreadyExistsException(String message) {
        super(message);
    }
}
