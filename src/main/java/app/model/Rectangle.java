package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.*;
import org.joml.Vector2d;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;


public class Rectangle extends Composite {

    public static final Object OUTFIT = new Object();

    public static final Object $MOUSE_ENTER = new Object();
    public static final Object $MOUSE_LEAVE = new Object();
    public static final Object $MOUSE_PRESS = new Object();
    public static final Object $MOUSE_RELEASE = new Object();

    public static final Object $MOUSE_IN = new Object();


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

    Action $mouseEnter;
    Action $mouseLeave;
    Action $mousePress;
    Action $mouseRelease;

    public void init(Subject sketch) {

        window = sketch.get(Window.class).asExpected();
        parent = sketch.get(Composite.class).asExpected();

        String exp;

        var hParams = sketch.get(Side.LEFT, Side.RIGHT, Pos.HORIZONTAL_CENTER, Dim.WIDTH);
        if(hParams.size() > 2) throw new RuntimeException("Too many horizontal constraints given. Expected <= 2, given: " + hParams);

        exp = null;

        if( in(hParams, Side.LEFT, Side.RIGHT)) exp = "a, b";
        else if( in(hParams, Side.LEFT, Pos.HORIZONTAL_CENTER)) exp = "a, 2 * b - a";
        else if( in(hParams, Side.LEFT, Dim.WIDTH)) exp = "a, a + b";
        else if( in(hParams, Side.RIGHT, Pos.HORIZONTAL_CENTER)) exp = "a; 2 * b - a";
        else if( in(hParams, Side.RIGHT, Dim.WIDTH)) exp = "a - b, a";
        else if( in(hParams, Dim.WIDTH, Pos.HORIZONTAL_CENTER)) exp = "b - a / 2, b + a / 2";

        if(exp != null) fun( abcS(hParams.values()), exp, abc(left, right)).press(true);

        var vParams = sketch.get(Side.BOTTOM, Side.TOP, Pos.VERTICAL_CENTER, Dim.HEIGHT);
        if(vParams.size() > 2) throw new RuntimeException("Too many vertical constraints given. Expected <= 2, given: " + vParams);

        exp = null;

        if( in(vParams, Side.BOTTOM, Side.TOP)) exp = "a, b";
        else if( in(vParams, Side.BOTTOM, Pos.VERTICAL_CENTER)) exp = "a, 2 * b - a";
        else if( in(vParams, Side.BOTTOM, Dim.HEIGHT)) exp = "a, a + b";
        else if( in(vParams, Side.TOP, Pos.VERTICAL_CENTER)) exp = "a; 2 * b - a";
        else if( in(vParams, Side.TOP, Dim.HEIGHT)) exp = "a - b, a";
        else if( in(vParams, Dim.HEIGHT, Pos.VERTICAL_CENTER)) exp = "b - a / 2, b + a / 2";

        if(exp != null) fun( abcS(vParams.values()), exp, abc(bottom, top)).press(true);


        var outfitParam = sketch.get(OUTFIT);

        Object outfit = null;

        if(outfitParam.assigned(Outfit.class) || outfitParam.assigned(ValueProducer.class)) outfit = outfitParam.direct();
        else if(outfitParam.assigned(Subject.class)) outfit = Outfit.form(outfitParam.asExpected());

        if(outfit != null) Fun.assign(outfit, this.outfit);


        var faceParam = sketch.get("face");

        Object face = null;

        if(faceParam.assigned(Number.class) || faceParam.assigned(ValueProducer.class)) face = faceParam.direct();

        if(face != null) Fun.assign(face, this.face);

        for(Subject s : sketch.at(COMPONENTS)) {
            place(s.asGiven(Subject.class));
        }
    }

    public Rectangle() {

        instant(num(outfit), s -> {
            Outfit o = s.asExpected();
            vertexMonitor = Monitor.compose(true, Suite.set(top).set(left).set(bottom).set(right).
                    set(o.getVertexMonitor()));
            o.updateIndices(new int[]{0, 2, 1, 0, 3, 2});
        });

    }

    public NumberVar right() {
        return right;
    }

    public NumberVar top() {
        return top;
    }

    public NumberVar left() {
        return left;
    }

    public NumberVar bottom() {
        return bottom;
    }

    public Var<Outfit> outfit() {
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

    public Var<Boolean> mouseIn() {
        return weakParams.getDone($MOUSE_IN, () -> SimpleVar.compound(num(window.mouse.getPosition(), window.width,
                 window.height, left, right, top, bottom), s -> {
            Vector2d mPos = s.asExpected();
            double w = s.get(1).asDouble(), h = s.get(2).asDouble();
            double x = 2. * mPos.x / w - 1., y = 1. - 2. * mPos.y / h;
            double l = s.get(3).asDouble(), r = s.get(4).asDouble(), t = s.get(5).asDouble(), b = s.get(6).asDouble();
            return x > l && x < r && y < t && y > b;
        })).asExpected();
    }

    public NumberVar width() {
        return weakParams.getDone(Dim.WIDTH, () -> NumberVar.expressed(Suite.add(right).add(left), Exp::sub)).asExpected();
    }

    public NumberVar height() {
        return weakParams.getDone(Dim.HEIGHT, () -> NumberVar.expressed(Suite.add(top).add(bottom), Exp::sub)).asExpected();
    }

    Subject textTransform(Subject sketch) {
        Subject r = Suite.set();
        for(var s : sketch) {
            var k = s.key().direct();
            if(k == Side.LEFT || k == Side.RIGHT || k == Pos.HORIZONTAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("a * (b + 1) / 2 + c",
                            window.getWidth(), left(), pixelParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            window.getWidth(), right(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("((r - l) * p / 100 + l) * w",
                            Suite.set("r", right()).set("l", left()).set("p", percentParcel.ware).set("w", window.getWidth())));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("((l - r) * p / 100 + r) * w",
                            Suite.set("r", right()).set("l", left()).set("p", percentParcel.ware).set("w", window.getWidth())));
                }
            } else if(k == Side.BOTTOM || k == Side.TOP || k == Pos.VERTICAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            window.getHeight(), top(), pixelParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a * (b + 1) / 2 + c",
                            window.getHeight(), top(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            bottom(), top(), percentParcel.ware, window.getHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            top(), bottom(), percentParcel.ware, window.getHeight()));
                }
            } else r.inset(s);
        }
        r.put("pw", window.getWidth()).put("ph", window.getHeight());

        return r;
    }

    Subject rectTransform(Subject sketch) {
        Subject r = Suite.set();
        for(var s : sketch) {
            var k = s.key().direct();
            if(k == Pos.HORIZONTAL_CENTER || k == Side.LEFT || k == Side.RIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.LEFT) r.set(k, NumberVar.expressed("a + b / c * 2",
                            left(), pixelParcel.ware, window.getWidth()));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a - b / c * 2",
                            right(), pixelParcel.ware, window.getWidth()));
                    else if(wb == Pos.HORIZONTAL_CENTER) r.set(k, NumberVar.expressed("(b - a) / 2 + a + c / d * 2",
                            left(), right(), pixelParcel.ware, window.getWidth()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.LEFT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            right(), left(), percentParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            left(), right(), percentParcel.ware));
                    else if(wb == Pos.HORIZONTAL_CENTER) r.set(k, NumberVar.expressed("w / 2 + a + w * c / 100; w = b - a",
                            left(), right(), percentParcel.ware));
                }
            } else if(k == Pos.VERTICAL_CENTER || k == Side.TOP || k == Side.BOTTOM) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.TOP) r.set(k, NumberVar.expressed("a - b / c * 2",
                            top(), pixelParcel.ware, window.getHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a + b / c * 2",
                            bottom(), pixelParcel.ware, window.getHeight()));
                    else if(wb == Pos.VERTICAL_CENTER) r.set(k, NumberVar.expressed("(b - a) / 2 + a + c / d * 2",
                            bottom(), top(), pixelParcel.ware, window.getHeight()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null)wb = k;
                    if(wb == Side.TOP) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            top(), bottom(), percentParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            bottom(), top(), percentParcel.ware));
                    else if(wb == Pos.VERTICAL_CENTER) r.set(k, NumberVar.expressed("w / 2 + a + w * c / 100; w = b - a",
                            bottom(), top(), percentParcel.ware));
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
                            right(), left(), percentParcel.ware));
                }
            } else if(k == Dim.HEIGHT) {
                if (s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if (wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("a / b * 2",
                            pixelParcel.ware, window.getHeight()));
                } else if (s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if (wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("(a - b) * c / 100",
                            top(), bottom(), percentParcel.ware));
                }
            } else r.inset(s);
        }
        r.put(Composite.class, this).put(Window.class, window);

        return r;
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

        public T outfit(Object var) {
            set(OUTFIT, var);
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
            return outfit(Outfit.sketch().color(red, green, blue));
        }

        public T place(Subject sketch) {
            into(COMPONENTS).add(sketch);
            return self();
        }
    }
}
