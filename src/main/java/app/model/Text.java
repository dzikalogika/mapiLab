package app.model;

import app.model.util.TSuite;
import app.model.variable.*;
import org.joml.Matrix4f;
import suite.suite.Subject;
import suite.suite.Suite;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Text implements Printable {

    public static Text form(Subject sub) {
        Text text = new Text();
        Subject s;
        text.content.assign(sub.get("text"));
        text.left.assign(sub.get(Side.LEFT));
        if((s = sub.get(Pos.HORIZONTAL_CENTER)).settled()) {
            text.left.compose(TSuite.params(text.graphicModel, text.size, text.content, s.direct()), su -> {
                TextGraphic textGraphic = su.get(0).asExpected();
                float size = su.get(1).asFloat();
                String txt = su.get(2).asString();
                float x = su.get(3).asFloat();
                return x - textGraphic.getStringWidth(txt, size) / 2;
            });
        }
        if((s = sub.get(Pos.VERTICAL_CENTER)).settled()) {
            text.bottom.compose(TSuite.params(text.size, s.direct()), su -> {
                float size = su.get(0).asFloat();
                float y = su.get(1).asFloat();
                return y + size / 3;
            });
        }
        text.bottom.assign(sub.get(Side.BOTTOM));
        text.size.assign(sub.get(Dim.HEIGHT));
        text.redColor.assign(sub.get(Color.RED));
        text.greenColor.assign(sub.get(Color.GREEN));
        text.blueColor.assign(sub.get(Color.BLUE));
        text.alphaColor.assign(sub.get(Color.ALPHA));
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

    private final Subject weakParams = Suite.wonky();
    final Var<String> content = SimpleVar.emit("");
    final NumberVar left = NumberVar.emit(0);
    final NumberVar bottom = NumberVar.emit(300);
    final NumberVar size = NumberVar.emit(24);
    final NumberVar redColor = NumberVar.emit(0);
    final NumberVar greenColor = NumberVar.emit(0);
    final NumberVar blueColor = NumberVar.emit(0);
    final NumberVar alphaColor = NumberVar.emit(1);
    final Var<Shader> shader = SimpleVar.emit();
    final Var<TextGraphic> graphicModel = SimpleVar.emit();

    final NumberVar projectionWidth = NumberVar.emit(800);
    final NumberVar projectionHeight = NumberVar.emit(600);

    final Monitor projectionMonitor;
    final Monitor colorMonitor;

    public Text() {
        projectionMonitor = Monitor.compose(true, Suite.set(shader).set(projectionWidth).set(projectionHeight));
        colorMonitor = Monitor.compose(true, Suite.set(shader).set(redColor).set(greenColor).set(blueColor).set(alphaColor));
    }

    @Override
    public void print() {
        Shader sh = shader.get();

        sh.use();

        if(projectionMonitor.release()) {
            sh.set("projection", new Matrix4f().ortho2D(0f, projectionWidth.getFloat(), 0f, projectionHeight.getFloat()));
            TextGraphic textGraphic = graphicModel.get();
            System.out.println(textGraphic.getStringWidth(content.get(), size.getFloat()));
        }
        glActiveTexture(GL_TEXTURE0);
        if(colorMonitor.release()) {
            sh.set("textColor", redColor.getFloat(), greenColor.getFloat(), blueColor.getFloat());
        }

        TextGraphic textGraphic = graphicModel.get();
        textGraphic.render(content.get(), left.getFloat(), bottom.getFloat(),
                size.getFloat(), projectionHeight.getFloat());
    }

    public Var<String> getContent() {
        return content;
    }

    public NumberVar getLeft() {
        return left;
    }

    public NumberVar getBottom() {
        return bottom;
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

    public NumberVar getWidth() {
        Subject s;
        if((s = weakParams.get(Dim.WIDTH)).settled()) return s.asExpected();
        NumberVar w =  NumberVar.compound(TSuite.params(content, graphicModel, size), su -> {
            String c = su.get(0).asExpected();
            TextGraphic g = su.get(1).asExpected();
            float size = su.get(2).asFloat();
            return g.getStringWidth(c, size);
        });
        weakParams.set(Dim.WIDTH, w);
        return w;
    }
}
