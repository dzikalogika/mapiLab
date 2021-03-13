package app.model;

public class Color {

    public static final Color PURE_GREEN = new Color(0, 1, 0);

    float red;
    float green;
    float blue;
    float alpha;

    public static Color mix(float red, float green, float blue, float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        return String.format("@[Color]red[%s]green[%s]blue[%s]", red, green,blue);
    }

}
