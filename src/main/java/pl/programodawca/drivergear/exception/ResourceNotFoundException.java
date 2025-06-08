package pl.programodawca.drivergear.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s nie został znaleziony dla %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

