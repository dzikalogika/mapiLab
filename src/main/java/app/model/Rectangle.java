package app.model;

import app.model.util.Keys;
import app.model.variable.*;
import suite.sets.Sets;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;


public class Rectangle extends Playground {

    public static final Exp expA = Exp.compile("a, b");
    public static final Exp expB = Exp.compile("a, 2 * b - a");
    public static final Exp expC = Exp.compile("a, a + b");
    public static final Exp expD = Exp.compile("a, a - b");
    public static final Exp expE = Exp.compile("a - b / 2, a + b / 2");



    private final NumberVar right = NumberVar.create(.2);
    private final NumberVar top = NumberVar.create(.2);
    private final NumberVar left = NumberVar.create(-.2);
    private final NumberVar bottom = NumberVar.create(-.2);
    private final Var<Outfit> outfit = Var.create();
    private final Subject weakParams = Suite.wonky();

    private Monitor vertexMonitor;

    static class Generator {

        static AlphaGenerator alpha() {
            return new AlphaGenerator();
        }
    }

    static class AlphaGenerator implements Action {
        char alpha = 'a';

        @Override
        public Subject play(Subject subject) {
            return Suite.set("" + alpha++, subject.direct());
        }
    }

    public static boolean applyExp(Subject sub, Fluid in, Fluid out, Exp exp) {
        Subject s;
        if((s = Sets.insec(sub, in)).size() == 2) {
            BeltFun.express(s.map(Keys.alpha()), out, exp).reduce(true);
            return false;
        }
        return true;
    }


    public static Rectangle form(Subject sub) {

        Rectangle rect = new Rectangle();

        Subject s;
        Subject hors = Suite.add(rect.left).add(rect.right);
        Subject vers = Suite.add(rect.bottom).add(rect.top);

        if(applyExp(sub, Suite.set("lx").set("rx"), hors, expA) &&
                applyExp(sub, Suite.set("lx").set("x"), hors, expB) &&
                applyExp(sub, Suite.set("lx").set("w"), hors, expC) &&
                applyExp(sub, Suite.set("rx").set("x"), hors.reverse(), expB) &&
                applyExp(sub, Suite.set("rx").set("w"), hors.reverse(), expD) &&
                applyExp(sub, Suite.set("x").set("w"), hors, expE));

        if(applyExp(sub, Suite.set("by").set("ty"), vers, expA) &&
                applyExp(sub, Suite.set("by").set("y"), vers, expB) &&
                applyExp(sub, Suite.set("by").set("h"), vers, expC) &&
                applyExp(sub, Suite.set("ty").set("y"), vers.reverse(), expB) &&
                applyExp(sub, Suite.set("ty").set("h"), vers.reverse(), expD) &&
                applyExp(sub, Suite.set("y").set("h"), vers, expE));

        if((s = sub.get("outfit")).settled()) rect.outfit.assign(s);
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

    public NumberVar getWidth() {
        return weakParams.getDone("w", () -> NumberVar.compose(Suite.add(right).add(left), Exp::sub)).asExpected();
    }

    public NumberVar getHeight() {
        return weakParams.getDone("h", () -> NumberVar.compose(Suite.add(top).add(bottom), Exp::sub)).asExpected();
    }
}
