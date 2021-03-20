package app.model.component;

import app.model.Color;
import app.model.Point;
import app.model.font.Font;
import app.model.font.FontManager;
import app.model.trade.Component;
import app.model.trade.Host;
import app.model.var.Source;
import app.model.var.PreservativeVar;
import app.model.var.Var;

import java.util.function.Supplier;

public class ColorText extends Component {

    public enum HorizontalReference {
        LEFT, CENTER, RIGHT
    }

    public enum VerticalReference {
        BOTTOM, CENTER, TOP
    }

    Var<String> text;
    Var<Point> position;
    Var<HorizontalReference> horizontalReference;
    Var<VerticalReference> verticalReference;
    Var<Color> color;
    Var<Number> size;
    Var<Font> font;

    PreservativeVar<Number> width;

    public ColorText(Host host) {
        super(host);
        text = new Var<>("");
        position = new Var<>(new Point(0,0));
        horizontalReference = new Var<>(HorizontalReference.CENTER);
        verticalReference = new Var<>(VerticalReference.CENTER);
        color = new Var<>(Color.mix(0,0,1));
        size = new Var<>(24);
        font = new Var<>(Font.TREBUC);

        width = new PreservativeVar<>(() -> order(FontManager.class).getFont(font.get()).getStringWidth(text.get(), size.get().floatValue()),
                text, size, font);
    }

    public String getText() {
        return text.get();
    }

    public ColorText setText(String text) {
        this.text.set(text);
        return this;
    }

    public Var<String> text() {
        return text;
    }

    public Point getPosition() {
        return position.get();
    }

    public ColorText setPosition(Point position) {
        this.position.set(position);
        return this;
    }

    public Var<Point> position() {
        return position;
    }

    public HorizontalReference getHReference() {
        return horizontalReference.get();
    }

    public ColorText setHReference(HorizontalReference reference) {
        this.horizontalReference.set(reference);
        return this;
    }

    public ColorText setHReference(Supplier<HorizontalReference> reference) {
        this.horizontalReference.let(reference);
        return this;
    }

    public VerticalReference getVReference() {
        return verticalReference.get();
    }

    public ColorText setVReference(VerticalReference reference) {
        this.verticalReference.set(reference);
        return this;
    }

    public ColorText setVReference(Supplier<VerticalReference> reference) {
        this.verticalReference.let(reference);
        return this;
    }

    public Color getColor() {
        return color.get();
    }

    public ColorText setColor(Color color) {
        this.color.set(color);
        return this;
    }

    public Var<Color> color() {
        return color;
    }

    public float getSize() {
        return size.get().floatValue();
    }

    public ColorText setSize(Number size) {
        this.size.set(size);
        return this;
    }

    public Var<Number> size() {
        return size;
    }

    public Font getFont() {
        return font.get();
    }

    public ColorText setFont(Font font) {
        this.font.set(font);
        return this;
    }

    public Var<Font> font() {
        return font;
    }

    public Source<Number> width() {
        return width;
    }

    public float getWidth() {
        return width.get().floatValue();
    }
}