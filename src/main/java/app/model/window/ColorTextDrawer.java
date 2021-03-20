package app.model.window;

import app.model.Color;
import app.model.component.ColorText;
import app.model.Point;
import app.model.font.BackedFont;
import app.model.font.CharacterTexture;
import app.model.font.FontManager;
import app.model.graphic.Shader;
import app.model.trade.Component;
import app.model.trade.Host;
import brackettree.reader.BracketTree;
import org.lwjgl.stb.STBTTAlignedQuad;
import suite.suite.util.Cascade;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;

public class ColorTextDrawer extends Component {

    private final int vao;
    private final int vbo;
    private final Shader shader;

    public ColorTextDrawer(Host host, Shader shader) {
        super(host);

        this.shader = shader != null ? shader : BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("forest/textShader.tree")).as(Shader.class);

        vbo = glGenBuffers();
        vao = glGenVertexArrays();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 32, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 32, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 32, 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 16);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 2, GL_FLOAT, false, 32, 24);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }

    public void setWindowSize(int width, int height) {
        shader.use();
        shader.set("windowSize", width, height);
    }

    public void draw(ColorText text, int windowHeight) {

        BackedFont font = order(FontManager.class).getFont(text.getFont(), text.getSize());

        float fontSize = font.getSize();
        int bitmapWidth = font.getLoadedFont().getBitmapWidth();
        int bitmapHeight = font.getLoadedFont().getBitmapHeight();

        String txt = text.getText();
        Point position = text.getPosition();
        Color color = text.getColor();

        var hRef = text.getHReference();
        var vRef = text.getVReference();
        float textSize = text.getSize();

        shader.use();
        shader.set("textColor", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        glActiveTexture(GL_TEXTURE0);

        float scale = textSize / fontSize;
        float[] X = new float[1];
        float[] Y = new float[1];
        X[0] = switch (hRef) {
            case RIGHT -> position.getX() - text.getWidth();
            case CENTER -> position.getX() - text.getWidth() / 2;
            case LEFT -> position.getX();
        };
        Y[0] = switch (vRef) {
            case TOP -> position.getY() + fontSize;
            case CENTER -> position.getY() + fontSize / 2f;
            case BOTTOM -> position.getY();
        };

        glBindVertexArray(vao);

        STBTTAlignedQuad quad = STBTTAlignedQuad.create();

        Cascade<Integer> codePoints = new Cascade<>(txt.codePoints().iterator());

        for(int codePoint : codePoints) {
            CharacterTexture charTex = font.getCharacterTexture(codePoint);
            float xRef = X[0];
            float yRef = Y[0];

            stbtt_GetBakedQuad(charTex.getBuffer(), bitmapWidth, bitmapHeight, charTex.getBufferOffset(),
                    X, Y, quad, true);

            float x0 = scale(quad.x0(), xRef, scale);
            float x1 = scale(quad.x1(), xRef, scale);
            float y0 = windowHeight - scale(quad.y0(), yRef, scale);
            float y1 = windowHeight - scale(quad.y1(), yRef, scale);
            X[0] = scale(X[0], xRef, scale);

            float[] vertices = new float[] {
                    x0, y0, x1, y1,
                    quad.s0(), quad.t0(), quad.s1(), quad.t1(),
            };

            glBindTexture(GL_TEXTURE_2D, charTex.getTextureGlid());
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_POINTS, 0, 1);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /*public void draw(ColorText text, float windowHeight) {

        shader.use();

        String txt = text.getText();
        Color color = text.getColor();
        Point position = text.getPosition();
        var hRef = text.getHReference();
        var vRef = text.getVReference();
        float s = text.getSize();

        shader.set("textColor", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        glActiveTexture(GL_TEXTURE0);

        float scale = s / this.size;
        float[] X = new float[1];
        float[] Y = new float[1];
        X[0] = switch (hRef) {
            case RIGHT -> position.x - text.getWidth();
            case CENTER -> position.x - text.getWidth() / 2;
            case LEFT -> position.x;
        };
        Y[0] = switch (vRef) {
            case TOP -> position.y + size;
            case CENTER -> position.y + size / 2f;
            case BOTTOM -> position.y;
        };

        glBindVertexArray(vao);

        STBTTAlignedQuad quad = STBTTAlignedQuad.create();

        Cascade<Integer> codePoints = new Cascade<>(txt.codePoints().iterator());

        for(int codePoint : codePoints) {
            var $char = $chars.in(codePoint).get();
            if($char.absent()) {
                $chars.put(font.bake(codePoint, codePoint, size));
            }
            CharacterTexture charTex = $chars.in(codePoint).asExpected();
            float xRef = X[0];
            float yRef = Y[0];

            stbtt_GetBakedQuad(charTex.getBuffer(), font.getBitmapWidth(), font.getBitmapHeight(), charTex.getBufferOffset(),
                    X, Y, quad, true);

            float x0 = scale(quad.x0(), xRef, scale);
            float x1 = scale(quad.x1(), xRef, scale);
            float y0 = windowHeight - scale(quad.y0(), yRef, scale);
            float y1 = windowHeight - scale(quad.y1(), yRef, scale);
            X[0] = scale(X[0], xRef, scale);

            float[] vertices = new float[] {
                    x0, y0, x1, y1,
                    quad.s0(), quad.t0(), quad.s1(), quad.t1(),
            };

            glBindTexture(GL_TEXTURE_2D, charTex.getTextureGlid());
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_POINTS, 0, 1);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }*/

    private float scale(float rel, float ref, float scale) {
        return (rel - ref) * scale + ref;
    }
}