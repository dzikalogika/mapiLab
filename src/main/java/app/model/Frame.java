package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.NumberVar;
import suite.suite.Subject;
import suite.suite.Suite;

public interface Frame {

    Object COMPONENTS = new Object();

    static InterFrame form(Subject sub) {
        InterFrame frame = new InterFrame();
        frame.parent = sub.get(Frame.class).asExpected();
        frame.rect = sub.get(Rectangle.class).asExpected();

        return frame;
    }

    NumberVar windowWidth();
    NumberVar windowHeight();

    void append(Object key, Printable component);
    default void append(Object key, Subject sketch) {
        Class<?> subtype = sketch.get(AbstractSketch.MODEL).orGiven(null);
        if(subtype == Rectangle.class) append(key, rect(sketch));
        if(subtype == Text.class) append(key, text(sketch));
        if(subtype == InterFrame.class) append(key, frame(sketch));
    }
    default void append(Subject sketch) {
        append(sketch, sketch);
    }

    default PixelParcel px(Object pixels) {
        return px(pixels, null);
    }
    default PixelParcel px(Object pixels, Object base) {
        return new PixelParcel(pixels, base);
    }
    default PercentParcel pc(Object percents) {
        return pc(percents, null);
    }
    default PercentParcel pc(Object percents, Object base) {
        return new PercentParcel(percents, base);
    }

    Text text(Subject sub);
    Rectangle rect(Subject sub);
    InterFrame frame(Subject sub);
}
