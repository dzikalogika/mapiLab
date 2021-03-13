package app.model;

public interface RectangleParameters {
    Point getLeftTopCorner(Point referencePoint, int windowWidth, int windowHeight);
    Point getLeftBottomCorner(Point referencePoint, int windowWidth, int windowHeight);
    Point getRightTopCorner(Point referencePoint, int windowWidth, int windowHeight);
    Point geRightBottomCorner(Point referencePoint, int windowWidth, int windowHeight);
    float getWidth();
    void setWidth(float width);
    float getHeight();
    void setHeight(float height);
}
