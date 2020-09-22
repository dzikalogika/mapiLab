package app.model;

import app.model.util.PixelParcel;
import app.model.util.TSuite;
import app.model.variable.*;
import org.joml.Matrix4f;
import suite.suite.Subject;
import suite.suite.Suite;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Text extends Component {

    public static final Object CONTENT = new Object();
    public static final Object SHADER = new Object();
    public static final Object GRAPHIC = new Object();

    public static Text form(Subject sub) {
        return new Text(sub);
    }

    private final Subject weakParams = Suite.wonky();
    final Var<String> content = SimpleVar.emit("");
    final NumberVar left = NumberVar.emit(0);
    final NumberVar bottom = NumberVar.emit(300);
    final NumberVar size = NumberVar.emit(24);
    final NumberVar redColor = NumberVar.emit(0);
    final NumberVar greenColor = NumberVar.emit(0);
    final NumberVar blueColor = NumberVar.emit(0);
    final NumberVar alphaColor = NumberVar.emit(1);
    final Var<Shader> shader = SimpleVar.emit();
    final Var<TextGraphic> graphicModel = SimpleVar.emit();

    final NumberVar projectionWidth = NumberVar.emit(800);
    final NumberVar projectionHeight = NumberVar.emit(600);

    final Monitor projectionMonitor;
    final Monitor colorMonitor;

    public Text(Subject sub) {
        this();
        Subject s;
        content.assign(sub.get(CONTENT));
        left.assign(sub.get(Side.LEFT));
        if((s = sub.get(Pos.HORIZONTAL_CENTER)).settled()) {
            left.compose(TSuite.params(graphicModel, size, content, s.direct()), su -> {
                TextGraphic textGraphic = su.get(0).asExpected();
                float size = su.get(1).asFloat();
                String txt = su.get(2).asString();
                float x = su.get(3).asFloat();
                return x - textGraphic.getStringWidth(txt, size) / 2;
            });
        }
        if((s = sub.get(Pos.VERTICAL_CENTER)).settled()) {
            bottom.compose(TSuite.params(size, s.direct()), su -> {
                float size = su.get(0).asFloat();
                float y = su.get(1).asFloat();
                return y + size / 3;
            });
        }
        bottom.assign(sub.get(Side.BOTTOM));
        size.assign(sub.get(Dim.HEIGHT));
        redColor.assign(sub.get(Color.RED));
        greenColor.assign(sub.get(Color.GREEN));
        blueColor.assign(sub.get(Color.BLUE));
        alphaColor.assign(sub.get(Color.ALPHA));
        if((s = sub.get("pw")).settled()) projectionWidth.assign(s);
        else throw new RuntimeException("Projection width (pw) param is obligatory");
        if((s = sub.get("ph")).settled()) projectionHeight.assign(s);
        else throw new RuntimeException("Projection height (ph) param is obligatory");

        if((s = sub.get(SHADER)).settled()) shader.assign(s);
        else shader.set(TextGraphic.defaultShader);
        if((s = sub.get(GRAPHIC)).settled()) graphicModel.assign(s);
        else graphicModel.set(TextGraphic.getForSize(size.getDouble()));
    }

    public Text() {
        projectionMonitor = Monitor.compose(true, Suite.set(shader).set(projectionWidth).set(projectionHeight));
        colorMonitor = Monitor.compose(true, Suite.set(shader).set(redColor).set(greenColor).set(blueColor).set(alphaColor));
    }

    @Override
    public void print() {
        Shader sh = shader.get();

        sh.use();

        if(projectionMonitor.release()) {
            sh.set("projection", new Matrix4f().ortho2D(0f, projectionWidth.getFloat(), 0f, projectionHeight.getFloat()));
            TextGraphic textGraphic = graphicModel.get();
            System.out.println(textGraphic.getStringWidth(content.get(), size.getFloat()));
        }
        glActiveTexture(GL_TEXTURE0);
        if(colorMonitor.release()) {
            sh.set("textColor", redColor.getFloat(), greenColor.getFloat(), blueColor.getFloat());
        }

        TextGraphic textGraphic = graphicModel.get();
        textGraphic.render(content.get(), left.getFloat(), bottom.getFloat(),
                size.getFloat(), projectionHeight.getFloat());
    }

    public Var<String> getContent() {
        return content;
    }

    public NumberVar getLeft() {
        return left;
    }

    public NumberVar getBottom() {
        return bottom;
    }

    public NumberVar getSize() {
        return size;
    }

    public NumberVar getRedColor() {
        return redColor;
    }

    public NumberVar getGreenColor() {
        return greenColor;
    }

    public NumberVar getBlueColor() {
        return blueColor;
    }

    public NumberVar getAlphaColor() {
        return alphaColor;
    }

    public Var<TextGraphic> getGraphicModel() {
        return graphicModel;
    }

    public NumberVar getWidth() {
        Subject s;
        if((s = weakParams.get(Dim.WIDTH)).settled()) return s.asExpected();
        NumberVar w =  NumberVar.compound(TSuite.params(content, graphicModel, size), su -> {
            String c = su.get(0).asExpected();
            TextGraphic g = su.get(1).asExpected();
            float size = su.get(2).asFloat();
            return g.getStringWidth(c, size);
        });
        weakParams.set(Dim.WIDTH, w);
        return w;
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
            set(AbstractSketch.MODEL, Text.class);
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
            set(Dim.HEIGHT, /*new PixelParcel(*/var/*, null)*/);
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

        public T redColor(Object var) {
            set(Color.RED, var);
            return self();
        }

        public T greenColor(Object var) {
            set(Color.GREEN, var);
            return self();
        }

        public T blueColor(Object var) {
            set(Color.BLUE, var);
            return self();
        }

        public T content(Object var) {
            set(Text.CONTENT, var);
            return self();
        }
    }
}
