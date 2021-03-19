package app.model;

import brackettree.reader.BracketTree;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.util.Cascade;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;

public class ColorTextDrawer {

    private static final Subject $sized = Suite.set();

    public static ColorTextDrawer getForSize(double size) {
        int s = ((int)Math.min(Math.ceil(Math.abs(size)), 191.) + 32) / 32;
        var $tg = $sized.in(s).set();
        if($tg.absent()) {
            $tg.set(ColorTextDrawer.generate(Suite.put("size", s * 32)));
        }
        return $tg.asExpected();
    }

    private final Font font;
    private final Subject $chars = Suite.set();
    private final int vao;
    private final int vbo;
    private final int size;
    private Shader shader;

    public static ColorTextDrawer generate(Subject $sub) {
        String fontPath = $sub.in("font").orGiven("ttf/trebuc.ttf");
        int fontSize = $sub.in("size").orGiven(24);
        Font font = new Font(fontPath);

        return new ColorTextDrawer(font, fontSize);
    }

    public ColorTextDrawer(Font font, int fontSize) {
        this.shader = BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("jorg/textShader.tree")).as(Shader.class);

        this.font = font;
        this.size = fontSize;
        int bitmapWidth = font.getBitmapWidth();
        int bitmapHeight = font.getBitmapHeight();

        // Polskie krzaki
        $chars.alter(font.bake(' ', '~', size));
        $chars.alter(font.bake(211, 211, size));
        $chars.alter(font.bake(243, 243, size));
        $chars.alter(font.bake(260, 263, size));
        $chars.alter(font.bake(280, 281, size));
        $chars.alter(font.bake(321, 324, size));
        $chars.alter(font.bake(346, 347, size));
        $chars.alter(font.bake(377, 380, size));

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

    public int getSize() {
        return size;
    }

    public void setWindowSize(int width, int height) {
        shader.use();
        shader.set("windowSize", width, height);
    }

    public void draw(ColorText text, float windowHeight) {

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
            case RIGHT -> position.x - font.getStringWidth(txt, s);
            case CENTER -> position.x - font.getStringWidth(txt, s) / 2;
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
    }

    private float scale(float rel, float ref, float scale) {
        return (rel - ref) * scale + ref;
    }
}