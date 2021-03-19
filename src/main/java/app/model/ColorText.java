package app.model;

import app.model.input.Var;

import java.util.function.Supplier;

public class ColorText {

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

    public ColorText() {
        text = new Var<>("");
        position = new Var<>(new Point(0,0));
        horizontalReference = new Var<>(HorizontalReference.CENTER);
        verticalReference = new Var<>(VerticalReference.CENTER);
        color = new Var<>(Color.mix(0,0,1));
        size = new Var<>(24);
    }

    public String getText() {
        return text.get();
    }

    public ColorText setText(String text) {
        this.text.set(text);
        return this;
    }

    public ColorText setText(Supplier<String> text) {
        this.text.draft(text);
        return this;
    }

    public Point getPosition() {
        return position.get();
    }

    public ColorText setPosition(Point position) {
        this.position.set(position);
        return this;
    }

    public ColorText setPosition(Supplier<Point> position) {
        this.position.draft(position);
        return this;
    }

    public HorizontalReference getHReference() {
        return horizontalReference.get();
    }

    public ColorText setHReference(HorizontalReference reference) {
        this.horizontalReference.set(reference);
        return this;
    }

    public ColorText setHReference(Supplier<HorizontalReference> reference) {
        this.horizontalReference.draft(reference);
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
        this.verticalReference.draft(reference);
        return this;
    }

    public Color getColor() {
        return color.get();
    }

    public ColorText setColor(Color color) {
        this.color.set(color);
        return this;
    }

    public ColorText setColor(Supplier<Color> color) {
        this.color.draft(color);
        return this;
    }

    public float getSize() {
        return size.get().floatValue();
    }

    public ColorText setSize(Number size) {
        this.size.set(size);
        return this;
    }

    public ColorText setSize(Supplier<Number> size) {
        this.size.draft(size);
        return this;
    }

    public Supplier<Float> width() {
        return () -> 10f;
    }
}