package app.model;

public enum Dim {
    WIDTH, HEIGHT;
    public static final Dim W = WIDTH, H = HEIGHT;

    @Override
    public String toString() {
        return "Dim." + super.toString();
    }
}
