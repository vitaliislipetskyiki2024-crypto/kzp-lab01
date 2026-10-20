package ua.lpnu.kzp;

public enum ScreeningKind {
    REGULAR("звичайний"),
    PREMIUM("преміум");

    private final String label;

    ScreeningKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}