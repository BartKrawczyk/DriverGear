package pl.programodawca.drivergear.model;

public enum Gender {
    MALE("Mężczyzna"),
    FEMALE("Kobieta");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

