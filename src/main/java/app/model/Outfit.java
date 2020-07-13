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
        Var<Float> r = Var.ofFloatFrom(sub, "r").orGiven(Var.create(0f));
        Var<Float> g = Var.ofFloatFrom(sub, "g").orGiven(Var.create(0f));
        Var<Float> b = Var.ofFloatFrom(sub, "b").orGiven(Var.create(0f));
        Var<Float> a = Var.ofFloatFrom(sub, "a").orGiven(Var.create(1f));
        return new ColorOutfit(r, g, b, a);
    }
}
