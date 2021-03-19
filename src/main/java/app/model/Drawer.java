package app.model;

import suite.suite.Subject;

import static suite.suite.$uite.$;

public class Drawer {

    interface Drawable {
        void draw();
    }

    Window window;
    ColorRectangleDrawer colorRectangleDrawer;
    ColorTextDrawer colorTextDrawer;
    Subject $drawables;

    public Drawer(Window window, ColorRectangleDrawer colorRectangleDrawer, ColorTextDrawer colorTextDrawer) {
        this.window = window;
        this.colorRectangleDrawer = colorRectangleDrawer;
        this.colorTextDrawer = colorTextDrawer;
        this.$drawables = $();
    }

    public void draw() {
        colorRectangleDrawer.setWindowSize(window.getWidth(), window.getHeight());
        colorTextDrawer.setWindowSize(window.getWidth(), window.getHeight());
        for(var d : $drawables.eachIn().eachAs(Drawable.class)) {
            d.draw();
        }
    }

    public void set(ColorRectangle colorRectangle) {
        $drawables.put(colorRectangle, (Drawable) () -> colorRectangleDrawer.draw(colorRectangle));
    }

    public void set(ColorText colorText) {
        $drawables.put(colorText, (Drawable) () -> colorTextDrawer.draw(colorText, window.getHeight()));
    }
}
