package app.model.trade;

import app.model.component.ColorRectangle;
import app.model.component.ColorText;
import app.model.component.ImageRectangle;

public interface Composite extends Host {

    default ColorText text() {
        return new ColorText(this);
    }

    default ColorRectangle rect() {
        return new ColorRectangle(this);
    }

    default ImageRectangle image() {
        return new ImageRectangle(this);
    }
}
