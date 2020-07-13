package app.model;

import app.model.variable.Monitor;
import app.model.variable.Var;
import org.joml.Vector2d;
import suite.suite.Subject;
import suite.suite.Suite;

public class Rectangle {

    private final Var<Vector2d> position;
    private final Var<Double> width;
    private final Var<Double> height;
    private final Var<Outfit> outfit;

    private final Monitor vertexMonitor;


    public static Rectangle form(Subject sub) {
        Var<Vector2d> position = Var.ofObjectFrom(sub, "position", Vector2d.class).orGiven(null);
        if(position == null) {
            Var<Double> x = Var.ofDoubleFrom(sub, "x").asExpected();
            Var<Double> y = Var.ofDoubleFrom(sub, "y").asExpected();
            position = Var.compose(Suite.set(x).set(y), s -> new Vector2d(s.asGiven(Double.class), s.recent().asExpected()));
        }
        Var<Double> w = Var.ofDoubleFrom(sub, "w").asExpected();
        Var<Double> h = Var.ofDoubleFrom(sub, "h").asExpected();
        Var<Outfit> outfit = Var.ofObjectFrom(sub, "outfit", Outfit.class).
                orDo(s -> Var.create(Outfit.form(s)));
        return new Rectangle(position, w, h, outfit);
    }

    public Rectangle(Var<Vector2d> position, Var<Double> width, Var<Double> height, Var<Outfit> outfit) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.outfit = outfit;

        outfit.get().updateIndices(new int[]{0, 2, 1, 0, 3, 2});
        vertexMonitor = Monitor.compose(true, Suite.set(position).set(this.width).set(this.height).
                set(outfit.get().getVertexMonitor()));
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

    public Var<Outfit> getOutfit() {
        return outfit;
    }

    public void print() {
        if(vertexMonitor.release()) {
            float[] v = new float[4 * 7];
            outfit.get().updateVertex(getVertex(v, 0, 7));
        }
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
