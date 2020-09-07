package app.model;

public enum Color {
    RED, GREEN, BLUE, ALPHA;
    public static final Color R = RED, G = GREEN, B = BLUE, A = ALPHA;

    @Override
    public String toString() {
        return "Color." + super.toString();
    }
}
