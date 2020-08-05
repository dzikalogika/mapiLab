package app.model;

import app.model.variable.Monitor;
import app.model.variable.Var;
import suite.suite.Subject;

public interface Outfit {
    void updateVertex(float[] vertex);
    void updateIndices(int[] indices);
    Monitor getVertexMonitor();
    void print();

    static Outfit form(Subject sub) {
        ColorOutfit colorOutfit = new ColorOutfit();
        Subject s;
        if((s = sub.get("r")).settled()) colorOutfit.red.assign(s, true);
        if((s = sub.get("g")).settled()) colorOutfit.green.assign(s, true);
        if((s = sub.get("b")).settled()) colorOutfit.blue.assign(s, true);
        if((s = sub.get("a")).settled()) colorOutfit.alpha.assign(s, true);
        return colorOutfit;
    }
}
