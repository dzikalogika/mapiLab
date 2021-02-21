package app.model;

import app.model.util.PixelParcel;
import app.model.variable.*;
import brackettree.reader.BracketTree;
import org.joml.Matrix4f;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.util.Sequence;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Text extends Component {

    public static final Object CONTENT = new Object();
    public static final Object SHADER = new Object();
    public static final Object GRAPHIC = new Object();

    private final Subject $weakParams = Suite.set();
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

    public void init(Subject $sub) {
        content.assign($sub.get(CONTENT));
        left.assign($sub.get(Side.LEFT));
        var $s = $sub.in(Pos.HORIZONTAL_CENTER).get();
        if($s.present()) {
            left.compose(num(graphicModel, size, content, $s.direct()), $ -> {
                TextGraphic textGraphic = $.in(0).asExpected();
                float size = $.in(1).get().asFloat();
                String txt = $.in(2).get().asString();
                float x = $.in(3).get().asFloat();
                return x - textGraphic.getStringWidth(txt, size) / 2;
            });
        }
        $s = $sub.in(Pos.VERTICAL_CENTER).get();
        if($s.present()) {
            bottom.compose(num(size, $s.direct()), $ -> {
                float size = $.in(0).get().asFloat();
                float y = $.in(1).get().asFloat();
                return y + size / 3;
            });
        }
        bottom.assign($sub.get(Side.BOTTOM));
        size.assign($sub.get(Dim.HEIGHT));
        redColor.assign($sub.get(Color.RED));
        greenColor.assign($sub.get(Color.GREEN));
        blueColor.assign($sub.get(Color.BLUE));
        alphaColor.assign($sub.get(Color.ALPHA));
        if(($s = $sub.get("pw")).present()) projectionWidth.assign($s);
        else throw new RuntimeException("Projection width (pw) param is obligatory");
        if(($s = $sub.get("ph")).present()) projectionHeight.assign($s);
        else throw new RuntimeException("Projection height (ph) param is obligatory");

        BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("jorg/textShader.jorg")).as(Shader.class);

        if(($s = $sub.get(SHADER)).present()) shader.assign($s);
        else shader.set(TextGraphic.defaultShader);
        if(($s = $sub.get(GRAPHIC)).present()) graphicModel.assign($s);
        else graphicModel.set(TextGraphic.getForSize(size.getDouble()));
    }

    public Text() {
        projectionMonitor = Monitor.compose(true, Suite.put(shader).put(projectionWidth).put(projectionHeight));
        colorMonitor = Monitor.compose(true, Suite.set().putAll(Sequence.of(
                shader, redColor, greenColor, blueColor, alphaColor)));
    }

    @Override
    public void print() {
        Shader sh = shader.get();

        sh.use();

        if(projectionMonitor.release()) {
//            sh.set("projection", new Matrix4f().ortho2D(0f, projectionWidth.getFloat(), 0f, projectionHeight.getFloat()));
            sh.set("projection", new Matrix4f().ortho2D(0f, 800f, 0f, 600f));
        }
        glActiveTexture(GL_TEXTURE0);
        if(colorMonitor.release()) {
            sh.set("textColor", redColor.getFloat(), greenColor.getFloat(), blueColor.getFloat());
        }

        TextGraphic textGraphic = graphicModel.get();
//        textGraphic.render(content.get(), left.getFloat(), bottom.getFloat(),
//                size.getFloat(), projectionHeight.getFloat());
        textGraphic.render(content.get(), 400, 400,
                24, 800);
    }

    public Var<String> content() {
        return content;
    }

    public NumberVar left() {
        return left;
    }

    public NumberVar bottom() {
        return bottom;
    }

    public NumberVar size() {
        return size;
    }

    public NumberVar redColor() {
        return redColor;
    }

    public NumberVar greenColor() {
        return greenColor;
    }

    public NumberVar blueColor() {
        return blueColor;
    }

    public NumberVar alphaColor() {
        return alphaColor;
    }

    public Var<TextGraphic> graphicModel() {
        return graphicModel;
    }

    public NumberVar width() {
        var $width = $weakParams.in(Dim.WIDTH).set();
        if($width.absent()) {
            $width.set(NumberVar.compound(num(content, graphicModel, size), $ -> {
                String c = $.in(0).asExpected();
                TextGraphic g = $.in(1).asExpected();
                float size = $.in(2).get().asFloat();
                return g.getStringWidth(c, size);
            }));
        }
        return $width.asExpected();
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
