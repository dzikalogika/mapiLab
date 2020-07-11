package app.model;

import app.model.variable.Var;
import org.joml.Vector2d;
import suite.suite.Subject;
import suite.suite.Suite;

public class Rectangle {

    private Var<Vector2d> position;
    private Var<Double> width;
    private Var<Double> height;
    private Var<ColorOutfit> outfit;
    private Var<Shader> shader;

    private Var<Object> vertexMonitor;


    public static Rectangle form(Subject sub) {
        double x = Suite.from(sub).get("x", Number.class, Number::doubleValue).asExpected();
        double y = Suite.from(sub).get("y", Number.class, Number::doubleValue).asExpected();
        double w = Suite.from(sub).get("w", Number.class, Number::doubleValue).asExpected();
        double h = Suite.from(sub).get("h", Number.class, Number::doubleValue).asExpected();
        return new Rectangle(x, y, w, h, new ColorOutfit(0f, 1f, 1f, 0.5f));
    }

    public Rectangle(double positionX, double positionY, double width, double height, ColorOutfit outfit) {
        this.position = Var.create(new Vector2d(positionX, positionY));
        this.width = Var.create(width);
        this.height = Var.create(height);
        this.outfit = Var.create(outfit);

        outfit.updateIndices(new int[]{0, 2, 1, 0, 3, 2});
        vertexMonitor = Var.compose(Suite.set(position).set(this.width).set(this.height).set(outfit.getVertexMonitor()), s -> {
            System.out.println("yo");
            float[] v = new float[4 * 7];
            this.outfit.get().updateVertex(getVertex(v, 0, 7));
            return Suite.set();
        });
    }

    public Var<Vector2d> getPosition() {
        return position;
    }

    public Var<Double> getWidth() {
        return width;
    }

    public Var<Double> getHeight() {
        return height;
    }

    public Var<ColorOutfit> getOutfit() {
        return outfit;
    }

    public void print() {
        vertexMonitor.get();
        outfit.get().print();
    }

    public float[] getVertex(float[] collector, int offset, int stride) {
        int i = offset;
        float x = (float)position.get().x, y = (float)position.get().y,
                w = width.get().floatValue(), h = height.get().floatValue();
        collector[i++] = x;
        collector[i] = y;
        i += stride - 1;
        collector[i++] = x;
        collector[i] = y + h;
        i += stride - 1;
        collector[i++] = x + w;
        collector[i] = y + h;
        i += stride - 1;
        collector[i++] = x + w;
        collector[i] = y;
        return collector;
    }
}
