package app.model;

import app.model.variable.Var;
import suite.suite.Subject;

public class Text {

    public static Text form(Subject sub) {
        Var<String> content = Var.from(sub, "text", String.class).asExpected();
        Var<Float> x = Var.floatFrom(sub, "x").asExpected();
        Var<Float> y = Var.floatFrom(sub, "y").asExpected();
        Var<Double> s = Var.doubleFrom(sub, "s").orGiven(new Var<>(24.0, false));
        Var<Float> r = Var.floatFrom(sub, "r").orGiven(new Var<>(0f, false));
        Var<Float> g = Var.floatFrom(sub, "g").orGiven(new Var<>(0f, false));
        Var<Float> b = Var.floatFrom(sub, "b").orGiven(new Var<>(0f, false));
        Var<Float> a = Var.floatFrom(sub, "a").orGiven(new Var<>(0f, false));
        Var<TextGraphic> graphicModel = Var.from(sub, "graphic", TextGraphic.class).
                orGiven(new Var<>(TextGraphic.getForSize(s.get()), false));
        return new Text(content, x, y, s, r, g, b, a, graphicModel);
    }

    Var<String> content;
    Var<Float> xPosition;
    Var<Float> yPosition;
    Var<Double> size;
    Var<Float> redColor;
    Var<Float> greenColor;
    Var<Float> blueColor;
    Var<Float> alphaColor;
    Var<TextGraphic> graphicModel;

    public Text(Var<String> content, Var<Float> xPosition, Var<Float> yPosition, Var<Double> size, Var<Float> redColor,
                Var<Float> greenColor, Var<Float> blueColor, Var<Float> alphaColor, Var<TextGraphic> graphicModel) {
        this.content = content;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.size = size;
        this.redColor = redColor;
        this.greenColor = greenColor;
        this.blueColor = blueColor;
        this.alphaColor = alphaColor;
        this.graphicModel = graphicModel;
    }

    public void render() {
        graphicModel.get().render(content.get(), xPosition.get(), yPosition.get(), size.get() / graphicModel.get().getSize(),
                redColor.get(), greenColor.get(), blueColor.get(), alphaColor.get());
    }

    public Var<String> getContent() {
        return content;
    }

    public Var<Float> getXPosition() {
        return xPosition;
    }

    public Var<Float> getYPosition() {
        return yPosition;
    }

    public Var<Double> getSize() {
        return size;
    }

    public Var<Float> getRedColor() {
        return redColor;
    }

    public Var<Float> getGreenColor() {
        return greenColor;
    }

    public Var<Float> getBlueColor() {
        return blueColor;
    }

    public Var<Float> getAlphaColor() {
        return alphaColor;
    }

    public Var<TextGraphic> getGraphicModel() {
        return graphicModel;
    }
}
