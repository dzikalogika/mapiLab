package app.model;

import app.model.variable.Monitor;
import suite.suite.Subject;
import suite.suite.Suite;

public interface Outfit {
    void updateVertex(float[] vertex);
    void updateIndices(int[] indices);
    Monitor getVertexMonitor();
    void print();

    static Outfit form(Subject sub) {
        var colors = sub.get(Color.RED, Color.GREEN, Color.BLUE, Color.ALPHA);
//        if(colors.settled()) {
            ColorOutfit colorOutfit = new ColorOutfit();
            colorOutfit.color = Color.form(colors);
            return colorOutfit;
//        }
    }

    static Sketch<?> sketch(Subject s) {
        return new Sketch<>(s);
    }

    static Sketch<?> sketch() {
        return new Sketch<>(Suite.set());
    }

    class Sketch<T extends Sketch<T>> extends AbstractSketch<T> {

        public Sketch(Subject s) {
            super(s);
            set(AbstractSketch.MODEL, Outfit.class);
        }

        public T red(Object var) {
            set(Color.RED, var);
            return self();
        }

        public T green(Object var) {
            set(Color.GREEN, var);
            return self();
        }

        public T blue(Object var) {
            set(Color.BLUE, var);
            return self();
        }

        public T alpha(Object var) {
            set(Color.ALPHA, var);
            return self();
        }

        public T color(Object red, Object green, Object blue) {
            return red(red).green(green).blue(blue);
        }

        public T color(Object red, Object green, Object blue, Object alpha) {
            return color(red, green, blue).alpha(alpha);
        }
    }
}
