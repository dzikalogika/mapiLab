package app.model;

import app.model.variable.Var;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import suite.suite.Subject;
import suite.suite.Suite;

public class Text {

    public static Text form(Subject sub) {
        String content = Suite.from(sub).get("text").asExpected();
        float positionX = Suite.from(sub).get("x", Number.class, Number::floatValue).asExpected();
        float positionY = Suite.from(sub).get("y", Number.class, Number::floatValue).asExpected();
        float colorR = Suite.from(sub).get("r", Float.class, Float::floatValue).
                or("r", Integer.class, i -> i / 255f).
                or("color", Vector3f.class, v -> v.x).
                or("color", String.class, s -> Integer.parseInt(s, 0, 2, 16) / 255f).orGiven(0f);
        float colorG = Suite.from(sub).get("g", Float.class, Float::floatValue).
                or("g", Integer.class, i -> i / 255f).
                or("color", Vector3f.class, v -> v.y).
                or("color", String.class, s -> Integer.parseInt(s, 2, 4, 16) / 255f).orGiven(0f);
        float colorB = Suite.from(sub).get("b", Float.class, Float::floatValue).
                or("b", Integer.class, i -> i / 255f).
                or("color", Vector3f.class, v -> v.z).
                or("color", String.class, s -> Integer.parseInt(s, 4, 6, 16) / 255f).orGiven(0f);
        double size = Suite.from(sub).get("size", Number.class, Number::doubleValue).orGiven(24.0);
        TextGraphic graphicModel = Suite.from(sub).get("graphicModel").orGiven(TextGraphic.getForSize(size));
        return new Text(content, new Vector2f(positionX, positionY), size, new Vector3f(colorR, colorG, colorB), graphicModel);
    }

    Var<String> content;
    Var<Vector2f> position;
    Var<Double> size;
    Var<Vector3f> color;
    Var<TextGraphic> graphicModel;

    public Text(String content, Vector2f position, double size, Vector3f color, TextGraphic graphicModel) {
        this.content = new Var<>(content);
        this.position = new Var<>(position);
        this.size = new Var<>(size);
        this.color = new Var<>(color);
        this.graphicModel = new Var<>(graphicModel);

        this.graphicModel.recipe(Suite.set(this.size), s -> Suite.set(TextGraphic.getForSize(s.asExpected())));
    }

    public void render() {
        graphicModel.get().render(content.get(), position.get().x, position.get().y, size.get() / graphicModel.get().getSize(), color.get());
    }

    public Var<String> getContent() {
        return content;
    }

    public Var<Vector2f> getPosition() {
        return position;
    }

    public Var<Double> getSize() {
        return size;
    }

    public Var<Vector3f> getColor() {
        return color;
    }

    public Var<TextGraphic> getGraphicModel() {
        return graphicModel;
    }
}
