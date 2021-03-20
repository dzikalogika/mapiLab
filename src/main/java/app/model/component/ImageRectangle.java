package app.model.component;

import app.model.Point;
import app.model.graphic.LoadedImage;
import app.model.image.Image;
import app.model.image.ImageManager;
import app.model.trade.Component;
import app.model.trade.Host;
import app.model.var.Source;
import app.model.var.Var;
import app.model.var.Vars;

public class ImageRectangle extends Component {

    Var<Number> width;
    Var<Number> height;
    Var<Point> position;
    Var<Image> image;

    public ImageRectangle(Host host) {
        super(host);
        image = Vars.get();
        width = Vars.get(Number.class).preserve(() -> {
            Image img = image.get();
            if(img == null) return 0;
            return order(ImageManager.class).getImage(img).getWidth();
        }, image);
        height = Vars.get(Number.class).preserve(() -> {
            Image img = image.get();
            if(img == null) return 0;
            return order(ImageManager.class).getImage(img).getHeight();
        }, image);
        position = Vars.set(new Point(400, 300));
    }

    public ImageRectangle setWidth(Number width) {
        this.width.set(width);
        return this;
    }

    public Var<Number> width() {
        return width;
    }

    public ImageRectangle setHeight(Number height) {
        this.height.set(height);
        return this;
    }

    public Var<Number> height() {
        return height;
    }

    public ImageRectangle setPosition(Point position) {
        this.position.set(position);
        return this;
    }

    public ImageRectangle setPosition(Number x, Number y) {
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

    public Image getImage() {
        return image.get();
    }

    public ImageRectangle setImage(Image image) {
        this.image.set(image);
        return this;
    }

    public Var<Image> image() {
        return image;
    }

    public boolean contains(Source<Point> pointSource) {
        return pointSource.present() && contains(pointSource.get());
    }

    public boolean contains(Point point) {
        Point position = getPosition();
        float width = getWidth();
        float hd = point.getY() - position.getY();
        if(Math.abs(hd) > width / 2) return false;
        float height = getHeight();
        float vd = point.getX() - position.getX();
        return Math.abs(vd) <= height / 2;
    }
}
