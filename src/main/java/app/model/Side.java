package app.model;

public enum Side {
    TOP, LEFT, BOTTOM, RIGHT;
    public static final Side T = TOP, L = LEFT, B = BOTTOM, R = RIGHT;

    @Override
    public String toString() {
        return "Side." + super.toString();
    }
}
