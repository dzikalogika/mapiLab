package app.model;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.Playground;
import suite.suite.Subject;
import suite.suite.Suite;

public abstract class Composite extends Component {
    public static Object COMPONENTS = new Object();
    public static Object INSTANCE = new Object();

    final Subject $components = Suite.set();

    @Override
    public void play() {
        $components.eachAs(Playground.class).forEach(Playground::play);
        super.play();
    }

    public void place(Component component) {
        $components.set(component);
    }

    public void place(Subject sketch) {

        var $instance = sketch.in(INSTANCE).get();
        if($instance.present()) {
            if($instance.is(Rectangle.class)) {
                Rectangle rect = $instance.asExpected();
                rect.init(rectTransform(sketch));
                place(rect);
            } else if($instance.is(Text.class)) {
                Text text = $instance.asExpected();
                text.init(textTransform(sketch));
                place(text);
            }
        } else {
            Class<?> modelClass = sketch.in(AbstractSketch.MODEL).asExpected();
            if(modelClass.equals(Rectangle.class)) {
                Rectangle rect = new Rectangle();
                rect.init(rectTransform(sketch));
                place(rect);
            } else if(modelClass.equals(Text.class)) {
                Text text = new Text();
                text.init(textTransform(sketch));
                place(text);
            }
        }
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

    public Text.Sketch<?> text() {
        return Text.sketch();
    }

    public Text.Sketch<?> text(Text instance) {
        return Text.sketch().set(INSTANCE, instance);
    }

    public Rectangle.Sketch<?> rect() {
        return Rectangle.sketch();
    }

    public Rectangle.Sketch<?> rect(Rectangle instance) {
        return Rectangle.sketch().set(INSTANCE, instance);
    }

    abstract Subject rectTransform(Subject sketch);
    abstract Subject textTransform(Subject sketch);
//    Button button(Subject sub);
}
