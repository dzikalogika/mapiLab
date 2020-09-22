package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.Playground;
import suite.suite.Subject;
import suite.suite.Suite;

public abstract class Composite extends Component {
    public static Object COMPONENTS = new Object();

    final Subject components = Suite.set();

    @Override
    public void play() {
        components.values().filter(Playground.class).forEach(Playground::play);
        super.play();
    }

    public void place(Component component) {
        place(component, component);
    }

    public void place(Object key, Component component) {
        System.out.println(component);
        components.set(key, component);
    }

    public void place(Object key, Subject sketch) {
        Class<?> subtype = sketch.get(AbstractSketch.MODEL).orGiven(null);
//        if(subtype == Button.class) place(key, button(sketch));
        if(subtype == Rectangle.class) place(key, rect(sketch));
        if(subtype == Text.class) place(key, text(sketch));
    }
    public  void place(Subject sketch) {
        place(new Suite.AutoKey(), sketch);
    }

    public  PixelParcel px(Object pixels) {
        return px(pixels, null);
    }
    public  PixelParcel px(Object pixels, Object base) {
        return new PixelParcel(pixels, base);
    }
    public  PercentParcel pc(Object percents) {
        return pc(percents, null);
    }
    public  PercentParcel pc(Object percents, Object base) {
        return new PercentParcel(percents, base);
    }

    abstract Text text(Subject sub);
    abstract Rectangle rect(Subject sub);
//    Button button(Subject sub);
}
