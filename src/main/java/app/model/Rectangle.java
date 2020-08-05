package app.model;

import app.model.variable.*;
import suite.sets.Sets;
import suite.suite.Subject;
import suite.suite.Suite;


public class Rectangle extends Playground {

    private final NumberVar right = NumberVar.create(.2);
    private final NumberVar top = NumberVar.create(.2);
    private final NumberVar left = NumberVar.create(-.2);
    private final NumberVar bottom = NumberVar.create(-.2);
    private final Var<Outfit> outfit = Var.create();

    private Monitor vertexMonitor;


    public static Rectangle form(Subject sub) {

        Rectangle rect = new Rectangle();
        Subject x = sub.get("x"), y = sub.get("y"), w = sub.get("w"), h = sub.get("h");
        if(x.settled() && w.settled()) {
            Fun.express(Sets.union(x, w), Suite.set("lx", rect.left).set("rx", rect.right),
                    "lx = x - w / 2, rx = x + w / 2").reduce(true);
        }
        if(y.settled() && h.settled()) {
            Fun.express(Sets.union(y, h), Suite.set("ty", rect.top).set("by", rect.bottom),
                    "ty = y + h / 2, by = y - h / 2").reduce(true);
        }
        Subject s;
        if((s = sub.get("lx")).settled()) rect.left.assign(s, true);
        if((s = sub.get("rx")).settled()) rect.right.assign(s, true);
        if((s = sub.get("ty")).settled()) rect.top.assign(s, true);
        if((s = sub.get("by")).settled()) rect.bottom.assign(s, true);

        if((s = sub.get("outfit")).settled()) rect.outfit.assign(s, true);
        else rect.outfit.set(Outfit.form(sub));

        return rect;
    }

    public Rectangle() {

        instant(Suite.set(outfit), s -> {
            Outfit o = s.asExpected();
            vertexMonitor = Monitor.compose(true, Suite.set(top).set(left).set(bottom).set(right).
                    set(o.getVertexMonitor()));
            o.updateIndices(new int[]{0, 2, 1, 0, 3, 2});
        });
    }

    public NumberVar getRight() {
        return right;
    }

    public NumberVar getTop() {
        return top;
    }

    public NumberVar getLeft() {
        return left;
    }

    public NumberVar getBottom() {
        return bottom;
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
        float x0 = left.getFloat(), x1 = right.getFloat(), y0 = top.getFloat(), y1 = bottom.getFloat();
        collector[i++] = x1;
        collector[i] = y0;
        i += stride - 1;
        collector[i++] = x1;
        collector[i] = y1;
        i += stride - 1;
        collector[i++] = x0;
        collector[i] = y1;
        i += stride - 1;
        collector[i++] = x0;
        collector[i] = y0;
        return collector;
    }
}
