package app.model;

public interface Composite extends Host {

    default ColorText text() {
        return new ColorText(this);
    }

    default ColorRectangle rect() {
        return new ColorRectangle(this);
    }
}
