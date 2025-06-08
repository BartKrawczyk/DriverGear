package pl.programodawca.drivergear.model;

public enum StockLevel {
    LOW("Niski"),
    MEDIUM("Średni"),
    OK("Wystarczający");

    private final String displayName;

    StockLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public static StockLevel fromQuantity(int quantity) {
        if (quantity < 10) {
            return LOW;
        } else if (quantity < 25) {
            return MEDIUM;
        } else {
            return OK;
        }
    }
}