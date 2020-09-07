package app.model;

import app.model.util.Generator;
import app.model.variable.*;
import suite.sets.Sets;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.util.Fluid;


public class Rectangle extends Playground implements Printable {

    public static final Exp expA = Exp.compile("a, b");
    public static final Exp expB = Exp.compile("a, 2 * b - a");
    public static final Exp expC = Exp.compile("a, a + b");
    public static final Exp expD = Exp.compile("a, a - b");
    public static final Exp expE = Exp.compile("a - b / 2, a + b / 2");



    private final NumberVar right = NumberVar.emit(.2);
    private final NumberVar top = NumberVar.emit(.2);
    private final NumberVar left = NumberVar.emit(-.2);
    private final NumberVar bottom = NumberVar.emit(-.2);
    private final NumberVar face = NumberVar.emit(0.5);
    private final Var<Outfit> outfit = SimpleVar.emit();
    private final Subject weakParams = Suite.wonky();

    private Monitor vertexMonitor;

    public static boolean applyExp(Subject sub, Fluid in, Fluid out, Exp exp) {
        Subject s;
        if((s = Sets.insec(sub, in)).size() == 2) {
            BeltFun.express(Fluid.engage(Generator.alpha(), s.values()), out, exp).reduce(true);
            return false;
        }
        return true;
    }


    public static Rectangle form(Subject sub) {

        Rectangle rect = new Rectangle();

        Subject s;
        Subject hors = Suite.add(rect.left).add(rect.right);
        Subject vers = Suite.add(rect.bottom).add(rect.top);

        if(applyExp(sub, Suite.set(Side.LEFT).set(Side.RIGHT), hors, expA) &&
                applyExp(sub, Suite.set(Side.LEFT).set(Pos.HORIZONTAL_CENTER), hors, expB) &&
                applyExp(sub, Suite.set(Side.LEFT).set(Dim.WIDTH), hors, expC) &&
                applyExp(sub, Suite.set(Side.RIGHT).set(Pos.HORIZONTAL_CENTER), hors.reverse(), expB) &&
                applyExp(sub, Suite.set(Side.RIGHT).set(Dim.WIDTH), hors.reverse(), expD) &&
                applyExp(sub, Suite.set(Pos.HORIZONTAL_CENTER).set(Dim.WIDTH), hors, expE));

        if(applyExp(sub, Suite.set(Side.BOTTOM).set(Side.TOP), vers, expA) &&
                applyExp(sub, Suite.set(Side.BOTTOM).set(Pos.VERTICAL_CENTER), vers, expB) &&
                applyExp(sub, Suite.set(Side.BOTTOM).set(Dim.HEIGHT), vers, expC) &&
                applyExp(sub, Suite.set(Side.TOP).set(Pos.VERTICAL_CENTER), vers.reverse(), expB) &&
                applyExp(sub, Suite.set(Side.TOP).set(Dim.HEIGHT), vers.reverse(), expD) &&
                applyExp(sub, Suite.set(Pos.VERTICAL_CENTER).set(Dim.HEIGHT), vers, expE));

        rect.face.assign(sub.get("face"));
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

    @Override
    public void print() {
        if(vertexMonitor.release()) {
            float[] v = new float[4 * 7];
            outfit.get().updateVertex(getVertex(v, 0, 7));
        }
        outfit.get().print();
    }

    public float[] getVertex(float[] collector, int offset, int stride) {
        int i = offset;
        float x0 = left.getFloat(), x1 = right.getFloat(), y0 = top.getFloat(), y1 = bottom.getFloat(), z = face.getFloat();
        collector[i++] = x1;
        collector[i++] = y0;
        collector[i] = z;
        i += stride - 2;
        collector[i++] = x1;
        collector[i++] = y1;
        collector[i] = z;
        i += stride - 2;
        collector[i++] = x0;
        collector[i++] = y1;
        collector[i] = z;
        i += stride - 2;
        collector[i++] = x0;
        collector[i++] = y0;
        collector[i] = z;
        return collector;
    }

    public NumberVar getWidth() {
        return weakParams.getDone(Dim.WIDTH, () -> NumberVar.expressed(Suite.add(right).add(left), Exp::sub)).asExpected();
    }

    public NumberVar getHeight() {
        return weakParams.getDone(Dim.HEIGHT, () -> NumberVar.expressed(Suite.add(top).add(bottom), Exp::sub)).asExpected();
    }
}
