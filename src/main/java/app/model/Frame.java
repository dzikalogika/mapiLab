package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.NumberVar;
import suite.suite.Subject;

public interface Frame {

    static InterFrame form(Subject sub) {
        InterFrame frame = new InterFrame();
        frame.parent = sub.get(InterFrame.class).asExpected();
        frame.rect = sub.get(Rectangle.class).asExpected();

        return frame;
    }

    NumberVar windowWidth();
    NumberVar windowHeight();

    void append(Printable component);
    default void append(Subject sub) {
        Class<?> subtype = sub.get(Printable.class).orGiven(null);
        if(subtype == Rectangle.class) append(rect(sub));
        if(subtype == Text.class) append(text(sub));
        if(subtype == InterFrame.class) append(frame(sub));
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
