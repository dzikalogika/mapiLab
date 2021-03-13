package app.model;

import suite.suite.Subject;

public interface PolygonOutfit {
    void setVertex(Point[] vertex);
    void setIndices(int[] indices);
    void print();

    static PolygonOutfit compose(Subject sub) {
        var colors = sub.get("red", "green", "blue", "alpha", "r", "g", "b", "a");
        ColorPolygonOutfit colorOutfit = new ColorPolygonOutfit();
//        colorOutfit.color = Color.compose(colors);
        return colorOutfit;
    }
}
