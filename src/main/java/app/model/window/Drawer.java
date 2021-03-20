package app.model.window;

import app.model.component.ColorRectangle;
import app.model.component.ColorText;
import app.model.component.ImageRectangle;
import app.model.trade.Agent;
import suite.suite.Subject;

import static suite.suite.$uite.$;

public class Drawer extends Agent {

    interface Drawable {
        void draw();
    }

    Window window;
    ColorRectangleDrawer colorRectangleDrawer;
    ColorTextDrawer colorTextDrawer;
    ImageRectangleDrawer imageRectangleDrawer;
    Subject $drawables;

    public Drawer(Window window) {
        super(window);
        this.window = window;
        this.colorRectangleDrawer = new ColorRectangleDrawer(null);
        this.colorTextDrawer = new ColorTextDrawer(this, null);
        this.imageRectangleDrawer = new ImageRectangleDrawer(this, null);
        this.$drawables = $();
    }

    public void draw() {
        colorRectangleDrawer.setWindowSize(window.getWidth(), window.getHeight());
        colorTextDrawer.setWindowSize(window.getWidth(), window.getHeight());
        imageRectangleDrawer.setWindowSize(window.getWidth(), window.getHeight());
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

    public void set(ImageRectangle imageRectangle) {
        $drawables.put(imageRectangle, (Drawable) () -> imageRectangleDrawer.draw(imageRectangle));
    }
}
