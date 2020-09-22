package app.model;

import suite.suite.Subject;

public class Button extends Rectangle {

    public static final Object TEXT = new Object();

    public Button(Subject sub) {
        super(sub);
    }

    @Override
    public void print() {
        super.print();
    }

    @Override
    public Text text(Subject sub) {
        sub.put(Pos.HORIZONTAL_CENTER, pc(50)).
                put(Pos.VERTICAL_CENTER, pc(50));
        return super.text(sub);
    }

    public static class Sketch<T extends Sketch<T>> extends Rectangle.Sketch<T> {

        public Sketch(Subject s) {
            super(s);
            set(AbstractSketch.MODEL, Button.class);
        }

//        public T onPress(Subject sketch) {
//            set(PRESS, sketch);
//            return self();
//        }
//
//        public T onRelease(Subject sketch) {
//            set(RELEASE, sketch);
//            return self();
//        }
    }
}
