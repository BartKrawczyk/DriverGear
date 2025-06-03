package pl.programodawca.drivergear.model;

public enum CompensationStatus {
    PENDING("Oczekujący"),
    APPROVED("Zatwierdzony"),
    PAID("Wypłacony"),
    CANCELLED("Anulowany");

    private final String displayName;

    CompensationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
