package app.model;


import app.model.input.Source;
import app.model.input.Var;

import java.util.function.Supplier;

public class ColorRectangle {

    Var<Number> width;
    Var<Number> height;
    Var<Point> position;
    Var<Color> color;

    public ColorRectangle() {
        width = new Var<>(100);
        height = new Var<>(100);
        position = new Var<>(new Point(400, 300));
        color = new Var<>(Color.PURE_GREEN);
    }

    public ColorRectangle setWidth(Number width) {
        this.width.set(width);
        return this;
    }

    public Var<Number> width() {
        return width;
    }

    public ColorRectangle setHeight(Number height) {
        this.height.set(height);
        return this;
    }

    public ColorRectangle setPosition(Point position) {
        this.position.set(position);
        return this;
    }

    public ColorRectangle setPosition(Number x, Number y) {
        this.position.set(new Point(x, y));
        return this;
    }

    public float getWidth() {
        return width.get().floatValue();
    }

    public float getHeight() {
        return height.get().floatValue();
    }

    public Point getPosition() {
        return position.get();
    }

    public Color getColor() {
        return color.get();
    }

    public ColorRectangle setColor(Color color) {
        this.color.set(color);
        return this;
    }

    public boolean contains(Source<Point> pointSource) {
        return pointSource.present() && contains(pointSource.get());
    }

    public boolean contains(Point point) {
        Point position = getPosition();
        float width = getWidth();
        float hd = point.y - position.y;
        if(Math.abs(hd) > width / 2) return false;
        float height = getHeight();
        float vd = point.x - position.x;
        return Math.abs(vd) <= height / 2;
    }
}
