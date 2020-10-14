package app.model;

import app.model.input.Mouse;
import app.model.util.Generator;
import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.*;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import suite.sets.Sets;
import suite.suite.Sub;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Cascade;
import suite.suite.util.Fluid;


public class Rectangle extends Composite {

    public static final Object OUTFIT = new Object();

    public static final Object $MOUSE_ENTER = new Object();
    public static final Object $MOUSE_LEAVE = new Object();
    public static final Object $MOUSE_PRESS = new Object();
    public static final Object $MOUSE_RELEASE = new Object();

    public static final Object $MOUSE_IN = new Object();

    public static final Exp expA = Exp.compile("a, b");
    public static final Exp expB = Exp.compile("a, 2 * b - a");
    public static final Exp expC = Exp.compile("a, a + b");
    public static final Exp expD = Exp.compile("a, a - b");
    public static final Exp expE = Exp.compile("a - b / 2, a + b / 2");


    Window window;
    Composite parent;

    private final NumberVar right = NumberVar.emit(.2);
    private final NumberVar top = NumberVar.emit(.2);
    private final NumberVar left = NumberVar.emit(-.2);
    private final NumberVar bottom = NumberVar.emit(-.2);
    private final NumberVar face = NumberVar.emit(0.5);
    private final Var<Outfit> outfit = SimpleVar.emit();
    private final Subject weakParams = Suite.wonky();

    private Monitor vertexMonitor;
    Var<Boolean> mouseIn = SimpleVar.emit(false);
    Var<Boolean> mousePressed = SimpleVar.emit(false);

    Subject states = Suite.set();

    Action $mouseEnter;
    Action $mouseLeave;
    Action $mousePress;
    Action $mouseRelease;

    static boolean applyExp(Subject sub, Fluid in, Fluid out, Exp exp) {
        Subject s;
        if((s = Sets.insec(sub, in)).size() == 2) {
            BeltFun.express(Fluid.engage(Generator.alphas(), s.values()), out, exp).reduce(true);
            return false;
        }
        return true;
    }

    Subject applyExps(Subject sub) {
        Subject hors = Suite.add(left).add(right);
        Subject vers = Suite.add(bottom).add(top);

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
    }

    public static Rectangle form(Subject sub) {

        Rectangle rectangle = new Rectangle(sub);
        rectangle.window = sub.get(Window.class).asExpected();
        rectangle.parent = sub.get(Composite.class).asExpected();
        for(Subject s : sub.at(COMPONENTS)) {
            rectangle.place(s.key().direct(), s.asGiven(Subject.class));
        }

        rectangle.mouseIn.compose(num(rectangle.window.mouse.getPosition(), rectangle.window.width,
                rectangle.window.height, rectangle.left, rectangle.right, rectangle.top, rectangle.bottom), s -> {
            Vector2d mPos = s.asExpected();
            double w = s.get(1).asDouble(), h = s.get(2).asDouble();
            double x = 2. * mPos.x / w - 1., y = 1. - 2. * mPos.y / h;
            double l = s.get(3).asDouble(), r = s.get(4).asDouble(), t = s.get(5).asDouble(), b = s.get(6).asDouble();
            return x > l && x < r && y < t && y > b;
        });

        rectangle.mousePressed.compose(num(rectangle.mouseIn, rectangle.window.mouse.getButton(GLFW.GLFW_MOUSE_BUTTON_1).getState()), s -> {
            boolean mouseIn = s.asExpected();
            Mouse.ButtonEvent be = s.recent().orGiven(null);
            return mouseIn && be != null && be.getAction() == GLFW.GLFW_PRESS;
        });

        return rectangle;
    }

    public Rectangle(Subject sub) {
        this();
        Subject s;
        applyExps(sub);

        face.assign(sub.get("face"));
        if((s = sub.get(OUTFIT)).settled()) outfit.assign(s);
        else outfit.set(Outfit.form(sub));

        if((s = sub.get($MOUSE_ENTER)).settled()) $mouseEnter = s.asExpected();
        if((s = sub.get($MOUSE_LEAVE)).settled()) $mouseLeave = s.asExpected();
        intent(num(mouseIn.suppressIdentity()), s1 -> {
            if(s1.asGiven(Boolean.class)) mouseEnter();
            else mouseLeave();
        });
        if((s = sub.get($MOUSE_PRESS)).settled()) $mousePress = s.asExpected();
        if((s = sub.get($MOUSE_RELEASE)).settled()) $mouseRelease = s.asExpected();
        intent(num(mousePressed.suppressIdentity()), s1 -> {
            if(s1.asGiven(Boolean.class)) mousePress();
            else mouseRelease();
        });
    }

    public Rectangle() {

        instant(Suite.set(outfit), s -> {
            Outfit o = s.asExpected();
            vertexMonitor = Monitor.compose(true, Suite.set(top).set(left).set(bottom).set(right).
                    set(o.getVertexMonitor()));
            o.updateIndices(new int[]{0, 2, 1, 0, 3, 2});
        });
    }

    public void loadState(Object state, Subject sketch) {

        Sub<Fun> functions = sub();

        Fun fun = null;
        String exp;

        var hParams = insec(sketch, Side.LEFT, Side.RIGHT, Pos.HORIZONTAL_CENTER, Dim.WIDTH);
        if(hParams.size() > 2) throw new RuntimeException("Too many horizontal constraints given. Expected <= 2, given: " + hParams);

        exp = null;

        if( in(hParams, Side.LEFT, Side.RIGHT)) exp = "a, b";
        else if( in(hParams, Side.LEFT, Pos.HORIZONTAL_CENTER)) exp = "a, 2 * b - a";
        else if( in(hParams, Side.LEFT, Dim.WIDTH)) exp = "a, a + b";
        else if( in(hParams, Side.RIGHT, Pos.HORIZONTAL_CENTER)) exp = "a; 2 * b - a";
        else if( in(hParams, Side.RIGHT, Dim.WIDTH)) exp = "a - b, a";
        else if( in(hParams, Dim.WIDTH, Pos.HORIZONTAL_CENTER)) exp = "b - a / 2, b + a / 2";

        if(exp != null) functions.set("H", fun( abcS(hParams), exp, abc(left, right)));


        var vParams = insec(sketch, Side.LEFT, Side.RIGHT, Pos.HORIZONTAL_CENTER, Dim.WIDTH);
        if(vParams.size() > 2) throw new RuntimeException("Too many vertical constraints given. Expected <= 2, given: " + vParams);

        exp = null;

        if( in(vParams, Side.BOTTOM, Side.TOP)) exp = "a, b";
        else if( in(vParams, Side.BOTTOM, Pos.VERTICAL_CENTER)) exp = "a, 2 * b - a";
        else if( in(vParams, Side.BOTTOM, Dim.HEIGHT)) exp = "a, a + b";
        else if( in(vParams, Side.TOP, Pos.VERTICAL_CENTER)) exp = "a; 2 * b - a";
        else if( in(vParams, Side.TOP, Dim.HEIGHT)) exp = "a - b, a";
        else if( in(vParams, Dim.HEIGHT, Pos.VERTICAL_CENTER)) exp = "b - a / 2, b + a / 2";

        if(exp != null) functions.set("H", fun( abcS(vParams), exp, abc(bottom, top)));

        if((s1 = face.assign(sketch.get("face"))).settled()) functions.set("face", s1.direct());

        if((s1 = outfit.assign(sketch.get(OUTFIT))).settled()) functions.set(OUTFIT, s1.direct());
        else if((s1 = outfit.assign(Suite.set(Outfit.form(sketch)))).settled()) functions.set(OUTFIT, s1.direct());

        states.set(state, functions);
    }

    void changeState(int[] state) {
        Subject s;
        if((s = states.get(state)).settled()) {
            Subject functions = s.asExpected();
            if((s = functions.get("H")))
        }
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
        components.values(Component.class).forEach(Component::print);
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

    public Text text(Subject sub) {
        Subject r = Suite.set();
        for(var s : sub) {
            var k = s.key().direct();
            if(k == Side.LEFT || k == Side.RIGHT || k == Pos.HORIZONTAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("a * (b + 1) / 2 + c",
                            window.getWidth(), getLeft(), pixelParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            window.getWidth(), getRight(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("((r - l) * p / 100 + l) * w",
                            Suite.set("r", getRight()).set("l", getLeft()).set("p", percentParcel.ware).set("w", window.getWidth())));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("((l - r) * p / 100 + r) * w",
                            Suite.set("r", getRight()).set("l", getLeft()).set("p", percentParcel.ware).set("w", window.getWidth())));
                }
            } else if(k == Side.BOTTOM || k == Side.TOP || k == Pos.VERTICAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            window.getHeight(), getTop(), pixelParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a * (b + 1) / 2 + c",
                            window.getHeight(), getTop(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            getBottom(), getTop(), percentParcel.ware, window.getHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            getTop(), getBottom(), percentParcel.ware, window.getHeight()));
                }
            } else r.inset(s);
        }
        r.put("pw", window.getWidth()).put("ph", window.getHeight());
        return Text.form(r);
    }

    public Rectangle rect(Subject sub) {
        Subject r = Suite.set();
        for(var s : sub) {
            var k = s.key().direct();
            if(k == Pos.HORIZONTAL_CENTER || k == Side.LEFT || k == Side.RIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.LEFT) r.set(k, NumberVar.expressed("a + b / c * 2",
                            getLeft(), pixelParcel.ware, window.getWidth()));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a - b / c * 2",
                            getRight(), pixelParcel.ware, window.getWidth()));
                    else if(wb == Pos.HORIZONTAL_CENTER) r.set(k, NumberVar.expressed("(b - a) / 2 + a + c / d * 2",
                            getLeft(), getRight(), pixelParcel.ware, window.getWidth()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.LEFT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            getRight(), getLeft(), percentParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            getLeft(), getRight(), percentParcel.ware));
                    else if(wb == Pos.HORIZONTAL_CENTER) r.set(k, NumberVar.expressed("w / 2 + a + w * c / 100; w = b - a",
                            getLeft(), getRight(), percentParcel.ware));
                }
            } else if(k == Pos.VERTICAL_CENTER || k == Side.TOP || k == Side.BOTTOM) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.TOP) r.set(k, NumberVar.expressed("a - b / c * 2",
                            getTop(), pixelParcel.ware, window.getHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a + b / c * 2",
                            getBottom(), pixelParcel.ware, window.getHeight()));
                    else if(wb == Pos.VERTICAL_CENTER) r.set(k, NumberVar.expressed("(b - a) / 2 + a + c / d * 2",
                            getBottom(), getTop(), pixelParcel.ware, window.getHeight()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.TOP) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            getTop(), getBottom(), percentParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            getBottom(), getTop(), percentParcel.ware));
                    else if(wb == Pos.VERTICAL_CENTER) r.set(k, NumberVar.expressed("w / 2 + a + w * c / 100; w = b - a",
                            getBottom(), getTop(), percentParcel.ware));
                }
            } else if(k == Dim.WIDTH) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed("a / b * 2",
                            pixelParcel.ware, window.getWidth()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed("(a - b) * c / 100",
                            getRight(), getLeft(), percentParcel.ware));
                }
            } else if(k == Dim.HEIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("a / b * 2",
                            pixelParcel.ware, window.getHeight()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("(a - b) * c / 100",
                            getTop(), getBottom(), percentParcel.ware));
                }
            } else r.inset(s);
        }
        r.put(Composite.class, this).put(Window.class, window);
        Rectangle rect = Rectangle.form(r);

        return rect;
    }

    public void mouseEnter() {
        if($mouseEnter != null) $mouseEnter.play(Suite.set(this));
    }

    public void mouseLeave() {
        if($mouseLeave != null) $mouseLeave.play(Suite.set(this));
    }

    public void mousePress() {
        if($mousePress != null) $mousePress.play(Suite.set(this));
    }

    public void mouseRelease() {
        if($mouseRelease != null) $mouseRelease.play(Suite.set(this));
    }

    //    @Override
//    public Button button(Subject sub) {
//        sub.put(Frame.class, this);
//        sub.getDone(Rectangle.class, this::rect, sub);
//        return new Button(sub);
//    }

    public static Sketch<?> sketch(Subject s) {
        return new Sketch<>(s);
    }

    public static Sketch<?> sketch() {
        return new Sketch<>(Suite.set());
    }

    public static class Sketch<T extends Sketch<T>> extends AbstractSketch<T> {

        public Sketch(Subject s) {
            super(s);
            set(AbstractSketch.MODEL, Rectangle.class);
        }

        public T left(Object var) {
            set(Side.LEFT, new PixelParcel(var, Side.LEFT));
            return self();
        }

        public T right(Object var) {
            set(Side.RIGHT, new PixelParcel(var, Side.RIGHT));
            return self();
        }

        public T bottom(Object var) {
            set(Side.BOTTOM, new PixelParcel(var, Side.BOTTOM));
            return self();
        }

        public T top(Object var) {
            set(Side.TOP, new PixelParcel(var, Side.TOP));
            return self();
        }

        public T horizontalCenter(Object var) {
            set(Pos.HORIZONTAL_CENTER, new PixelParcel(var, Pos.HORIZONTAL_CENTER));
            return self();
        }

        public T verticalCenter(Object var) {
            set(Pos.VERTICAL_CENTER, new PixelParcel(var, Pos.VERTICAL_CENTER));
            return self();
        }

        public T width(Object var) {
            set(Dim.WIDTH, new PixelParcel(var, null));
            return self();
        }

        public T height(Object var) {
            set(Dim.HEIGHT, new PixelParcel(var, null));
            return self();
        }

        public T left(Object var, Unit unit) {
            set(Side.LEFT, unit.parcel(var));
            return self();
        }

        public T right(Object var, Unit unit) {
            set(Side.RIGHT, unit.parcel(var));
            return self();
        }

        public T bottom(Object var, Unit unit) {
            set(Side.BOTTOM, unit.parcel(var));
            return self();
        }

        public T top(Object var, Unit unit) {
            set(Side.TOP, unit.parcel(var));
            return self();
        }

        public T horizontalCenter(Object var, Unit unit) {
            set(Pos.HORIZONTAL_CENTER, unit.parcel(var));
            return self();
        }

        public T verticalCenter(Object var, Unit unit) {
            set(Pos.VERTICAL_CENTER, unit.parcel(var));
            return self();
        }

        public T width(Object var, Unit unit) {
            set(Dim.WIDTH, unit.parcel(var));
            return self();
        }

        public T height(Object var, Unit unit) {
            set(Dim.HEIGHT, unit.parcel(var));
            return self();
        }

        public T left(Object var, Unit unit, Side base) {
            set(Side.LEFT, unit.parcel(var, base));
            return self();
        }

        public T right(Object var, Unit unit, Side base) {
            set(Side.RIGHT, unit.parcel(var, base));
            return self();
        }

        public T bottom(Object var, Unit unit, Side base) {
            set(Side.BOTTOM, unit.parcel(var, base));
            return self();
        }

        public T top(Object var, Unit unit, Side base) {
            set(Side.TOP, unit.parcel(var, base));
            return self();
        }

        public T horizontalCenter(Object var, Unit unit, Side base) {
            set(Pos.HORIZONTAL_CENTER, unit.parcel(var, base));
            return self();
        }

        public T verticalCenter(Object var, Unit unit, Side base) {
            set(Pos.VERTICAL_CENTER, unit.parcel(var, base));
            return self();
        }

        public T redColor(Object var) {
            set(Color.RED, var);
            return self();
        }

        public T greenColor(Object var) {
            set(Color.GREEN, var);
            return self();
        }

        public T blueColor(Object var) {
            set(Color.BLUE, var);
            return self();
        }

        public T sides(Object left, Object right, Object top, Object bottom) {
            return left(left).right(right).top(top).bottom(bottom);
        }

        public T center(Object x, Object y) {
            return horizontalCenter(x).verticalCenter(y);
        }

        public T dim(Object width, Object height) {
            return width(width).height(height);
        }

        public T color(Object red, Object green, Object blue) {
            return redColor(red).greenColor(green).blueColor(blue);
        }

        public T place(Subject sketch) {
            into(COMPONENTS).add(sketch);
            return self();
        }
    }

}
