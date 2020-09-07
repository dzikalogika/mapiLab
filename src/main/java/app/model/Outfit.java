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
        colorOutfit.red.assign(sub.get(Color.RED));
        colorOutfit.green.assign(sub.get(Color.GREEN));
        colorOutfit.blue.assign(sub.get(Color.BLUE));
        colorOutfit.alpha.assign(sub.get(Color.ALPHA));
        return colorOutfit;
    }
}
