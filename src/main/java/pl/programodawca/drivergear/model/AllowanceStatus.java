package pl.programodawca.drivergear.model;

public enum AllowanceStatus {
    ACTIVE("Aktywny"),
    EXPIRED("Wygasły"),
    CANCELLED("Anulowany"),
    PENDING("Oczekujący");

    private final String displayName;

    AllowanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
