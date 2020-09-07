package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.NumberVar;
import suite.suite.Subject;
import suite.suite.Suite;

public class InterFrame implements Frame, Printable{

    Rectangle rect;
    Frame parent;
    final Subject components;

    public static InterFrame form(Subject sub) {
        InterFrame frame = new InterFrame();
        frame.parent = sub.get(Frame.class).asExpected();
        frame.rect = sub.get(Rectangle.class).asExpected();
        for(Subject s : sub.at("components").values(Subject.class)) {
            frame.append(s);
        }

        System.out.println(frame.components);
        return frame;
    }

    public InterFrame() {
        components = Suite.set();
    }

    public NumberVar windowWidth() {
        return parent.windowWidth();
    }

    public NumberVar windowHeight() {
        return parent.windowHeight();
    }

    @Override
    public void print() {
        rect.print();
        components.values(Printable.class).forEach(Printable::print);
    }

    public void append(Printable component) {
        components.set(component);
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
                            windowWidth(), rect.getLeft(), pixelParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            windowWidth(), rect.getRight(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("((r - l) * p / 100 + l) * w",
                            Suite.set("r", rect.getRight()).set("l", rect.getLeft()).set("p", percentParcel.ware).set("w", windowWidth())));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("((l - r) * p / 100 + r) * w",
                            Suite.set("r", rect.getRight()).set("l", rect.getLeft()).set("p", percentParcel.ware).set("w", windowWidth())));
                }
            } else if(k == Side.BOTTOM || k == Side.TOP || k == Pos.VERTICAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("a * (b + 1) / 2 - c",
                            windowHeight(), rect.getTop(), pixelParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a * (b + 1) / 2 + c",
                            windowHeight(), rect.getTop(), pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            rect.getBottom(), rect.getTop(), percentParcel.ware, windowHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("((a - b) * c / 100 + b) * d",
                            rect.getTop(), rect.getBottom(), percentParcel.ware, windowHeight()));
                }
            } else r.inset(s);
        }
        r.put("pw", windowWidth()).put("ph", windowHeight());
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
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("a + b / c * 2",
                            rect.getLeft(), pixelParcel.ware, windowWidth()));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("a - b / c * 2",
                            rect.getRight(), pixelParcel.ware, windowWidth()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            rect.getRight(), rect.getLeft(), percentParcel.ware));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            rect.getLeft(), rect.getRight(), percentParcel.ware));
                }
            } else if(k == Pos.VERTICAL_CENTER || k == Side.TOP || k == Side.BOTTOM) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("a - b / c * 2",
                            rect.getTop(), pixelParcel.ware, windowHeight()));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("a + b / c * 2",
                            rect.getBottom(), pixelParcel.ware, windowHeight()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            rect.getTop(), rect.getBottom(), percentParcel.ware));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed("(a - b) * c / 100 + b",
                            rect.getBottom(), rect.getTop(), percentParcel.ware));
                }
            } else if(k == Dim.WIDTH) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed("a / b * 2",
                            pixelParcel.ware, windowWidth()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed("(a - b) * c / 100",
                            rect.getRight(), rect.getLeft(), percentParcel.ware));
                }
            } else if(k == Dim.HEIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("a / b * 2",
                            pixelParcel.ware, windowHeight()));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed("(a - b) * c / 100",
                            rect.getTop(), rect.getBottom(), percentParcel.ware));
                }
            } else r.inset(s);
        }
        return Rectangle.form(r);
    }

    public InterFrame frame(Subject sub) {
        sub.put(Frame.class, this);
        sub.getDone(Rectangle.class, this::rect, sub);
        return InterFrame.form(sub);
    }
}
