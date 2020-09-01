package app.model;

import app.model.variable.Monitor;
import app.model.variable.NumberVar;
import app.model.variable.Var;
import org.joml.Matrix4f;
import suite.suite.Subject;
import suite.suite.Suite;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Text {

    public static Text form(Subject sub) {
        Text text = new Text();
        text.content.assign(sub.get("text"));
        text.xPosition.assign(sub.get("x"));
        text.yPosition.assign(sub.get("y"));
        text.size.assign(sub.get("s"));
        text.redColor.assign(sub.get("r"));
        text.greenColor.assign(sub.get("g"));
        text.blueColor.assign(sub.get("b"));
        text.alphaColor.assign(sub.get("a"));
        Subject s;
        if((s = sub.get("pw")).settled()) text.projectionWidth.assign(s);
        else throw new RuntimeException("Projection width (pw) param is obligatory");
        if((s = sub.get("ph")).settled()) text.projectionHeight.assign(s);
        else throw new RuntimeException("Projection height (ph) param is obligatory");

        if((s = sub.get("shader")).settled()) text.shader.assign(s);
        else text.shader.set(TextGraphic.defaultShader);
        if((s = sub.get("graphic")).settled()) text.graphicModel.assign(s);
        else text.graphicModel.set(TextGraphic.getForSize(text.size.getDouble()));
        return text;
    }

    final Var<String> content = Var.create("");
    final NumberVar xPosition = NumberVar.create(0);
    final NumberVar yPosition = NumberVar.create(0);
    final NumberVar size = NumberVar.create(24);
    final NumberVar redColor = NumberVar.create(0);
    final NumberVar greenColor = NumberVar.create(0);
    final NumberVar blueColor = NumberVar.create(0);
    final NumberVar alphaColor = NumberVar.create(1);
    final Var<Shader> shader = Var.create();
    final Var<TextGraphic> graphicModel = Var.create();

    final NumberVar projectionWidth = NumberVar.create(800);
    final NumberVar projectionHeight = NumberVar.create(600);

    final Monitor projectionMonitor;
    final Monitor colorMonitor;

    public Text() {
        projectionMonitor = Monitor.compose(true, Suite.set(shader).set(projectionWidth).set(projectionHeight));
        colorMonitor = Monitor.compose(true, Suite.set(shader).set(redColor).set(greenColor).set(blueColor).set(alphaColor));
    }

    public void render() {
        Shader sh = shader.get();

        sh.use();

        if(projectionMonitor.release()) {
            sh.set("projection", new Matrix4f().ortho2D(0f, projectionWidth.getFloat(), 0f, projectionHeight.getFloat()));
        }
        glActiveTexture(GL_TEXTURE0);
        if(colorMonitor.release()) {
            sh.set("textColor", redColor.getFloat(), greenColor.getFloat(), blueColor.getFloat());
        }

        TextGraphic textGraphic = graphicModel.get();
        textGraphic.render(content.get(), xPosition.getFloat(), yPosition.getFloat(),
                size.getFloat(), projectionHeight.getFloat());
    }

    public Var<String> getContent() {
        return content;
    }

    public NumberVar getxPosition() {
        return xPosition;
    }

    public NumberVar getyPosition() {
        return yPosition;
    }

    public NumberVar getSize() {
        return size;
    }

    public NumberVar getRedColor() {
        return redColor;
    }

    public NumberVar getGreenColor() {
        return greenColor;
    }

    public NumberVar getBlueColor() {
        return blueColor;
    }

    public NumberVar getAlphaColor() {
        return alphaColor;
    }

    public Var<TextGraphic> getGraphicModel() {
        return graphicModel;
    }
}
