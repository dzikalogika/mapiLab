package app.model;

public class Rectangle extends Polygon {
    RectangleParameters params = new WHCRectangleParameters();
    PolygonOutfit outfit = new ColorPolygonOutfit();

    Window window;

    public void setup() {
        outfit.setIndices(getIndices());
    }

    public void update() {

        print();
    }

    public void print() {

        outfit.setVertex(getVertex());
        outfit.print();
    }

    public double getWidth() {
        return params.getWidth();
    }

    public void setWidth(double width) {
//        params.setWidth(width);
    }

    public double getHeight() {
        return params.getHeight();
    }

    public void setHeight(double height) {
//        params.setHeight(height);
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    protected int[] getIndices() {
        return new int[]{0, 2, 1, 0, 3, 2};
    }

    @Override
    protected Point[] getVertex() {
        return new Point[]{
//                params.getLeftTopCorner(window.coordinator.getPosition(this), window.getWidth(), window.getHeight()),
//                params.getLeftBottomCorner(window.coordinator.getPosition(this), window.getWidth(), window.getHeight()),
//                params.geRightBottomCorner(window.coordinator.getPosition(this), window.getWidth(), window.getHeight()),
//                params.getRightTopCorner(window.coordinator.getPosition(this), window.getWidth(), window.getHeight())
        };
    }
}
