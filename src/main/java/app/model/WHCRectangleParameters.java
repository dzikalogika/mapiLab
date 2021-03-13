package app.model;

public class WHCRectangleParameters implements RectangleParameters {

    float width;
    float height;

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public Point getLeftTopCorner(Point referencePoint, int windowWidth, int windowHeight) {
        return new Point(
                (2 * referencePoint.x - width) / windowWidth - 1,
                (2 * referencePoint.y + height) / windowHeight - 1,
                referencePoint.z
        );
    }

    @Override
    public Point getLeftBottomCorner(Point referencePoint, int windowWidth, int windowHeight) {
        return new Point(
                (2 * referencePoint.x - width) / windowWidth - 1,
                (2 * referencePoint.y - height) / windowHeight - 1,
                referencePoint.z
        );
    }

    @Override
    public Point getRightTopCorner(Point referencePoint, int windowWidth, int windowHeight) {
        return new Point(
                (2 * referencePoint.x + width) / windowWidth - 1,
                (2 * referencePoint.y + height) / windowHeight - 1,
                referencePoint.z
        );
    }

    @Override
    public Point geRightBottomCorner(Point referencePoint, int windowWidth, int windowHeight) {
        return new Point(
                (2 * referencePoint.x + width) / windowWidth - 1,
                (2 * referencePoint.y - height) / windowHeight - 1,
                referencePoint.z
        );
    }
}
