package app.old;

import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import suite.suite.Subject;
import suite.suite.Suite;
import vars.vars.Playground;

public abstract class Composite extends Component {
    public static Object COMPONENTS = new Object();
    public static Object INSTANCE = new Object();

    final Subject $components = Suite.set();

    @Override
    public void play() {
        $components.eachAs(Playground.class).forEach(Playground::play);
        super.play();
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

    abstract Subject rectTransform(Subject sketch);
    abstract Subject textTransform(Subject sketch);
//    Button button(Subject sub);
}
