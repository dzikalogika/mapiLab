package app.model;

import app.model.variable.Monitor;
import app.model.variable.NumberVar;
import suite.suite.Subject;
import suite.suite.Suite;

public class Color {
    public static final Object RED = new Object(), GREEN = new Object(), BLUE = new Object(), ALPHA = new Object();
    public static final Object R = RED, G = GREEN, B = BLUE, A = ALPHA;

    static Color form(Subject sub) {
        Color color = new Color();
        color.red.assign(sub.get(Color.RED));
        color.green.assign(sub.get(Color.GREEN));
        color.blue.assign(sub.get(Color.BLUE));
        color.alpha.assign(sub.get(Color.ALPHA));

        return color;
    }

    final NumberVar red = NumberVar.emit(0);
    final NumberVar green = NumberVar.emit(0);
    final NumberVar blue = NumberVar.emit(0);
    final NumberVar alpha = NumberVar.emit(1);

    public Monitor monitor() {
        return Monitor.compose(false, Suite.set(red).set(green).set(blue).set(alpha));
    }

    @Override
    public String toString() {
        return "Color." + super.toString();
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
            set(AbstractSketch.MODEL, Color.class);
        }

        public Color form() {
            return Color.form(this);
        }

        public T red(Object var) {
            set(RED, var);
            return self();
        }

        public T green(Object var) {
            set(GREEN, var);
            return self();
        }

        public T blue(Object var) {
            set(BLUE, var);
            return self();
        }

        public T alpha(Object var) {
            set(ALPHA, var);
            return self();
        }

        public T mix(Object red, Object green, Object blue) {
            return red(red).green(green).blue(blue);
        }

        public T mix(Object red, Object green, Object blue, Object alpha) {
            return mix(red, green, blue).alpha(alpha);
        }

//        public T code(Object code) {
//
//        }
    }
}
