package app.model;

import app.model.variable.Monitor;
import suite.suite.Subject;

public interface Outfit {
    void updateVertex(float[] vertex);
    void updateIndices(int[] indices);
    Monitor getVertexMonitor();
    void print();

    static Outfit form(Subject sub) {
        ColorOutfit colorOutfit = new ColorOutfit();
        colorOutfit.red.assign(sub.get("r"));
        colorOutfit.green.assign(sub.get("g"));
        colorOutfit.blue.assign(sub.get("b"));
        colorOutfit.alpha.assign(sub.get("a"));
        return colorOutfit;
    }
}
