package pl.programodawca.drivergear.model;

/**
 * Represents the status of a clothing assignment to an employee.
 */
public enum AssignmentStatus {
    PENDING("Oczekujący"),
    ASSIGNED("Przydzielony"),
    ISSUED("Wydany"),
    RETURNED("Zwrócony"),
    EXPIRED("Wygasły"),
    COMPENSATED("Zrekompensowany"),
    CANCELLED("Anulowany");

    private final String displayName;

    AssignmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}