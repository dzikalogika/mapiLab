package app.model;

import app.model.variable.Monitor;
import app.model.variable.NumberVar;
import jorg.jorg.Jorg;
import suite.suite.Suite;


import static org.lwjgl.opengl.GL30.*;

public class ColorOutfit extends GLObject implements Outfit {

    public static final Shader shader = Jorg.withRecipe(Shader::form).read(Shader.class.getClassLoader().getResourceAsStream("jorg/colorShader.jorg"));

    final NumberVar red = NumberVar.create(0);
    final NumberVar green = NumberVar.create(0);
    final NumberVar blue = NumberVar.create(0);
    final NumberVar alpha = NumberVar.create(0);
    private int indicesLength = 0;
    private final int vertexGlid;
    private final int indicesGlid;

    public ColorOutfit() {
        super(glGenVertexArrays());

        vertexGlid = glGenBuffers();
        indicesGlid = glGenBuffers();
        indicesLength = 3;
        glBindVertexArray(getGlid());

        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferData(GL_ARRAY_BUFFER, new float[]{
                -0.5f, -0.5f, 0.0f, 0f, 1f, 1f, 0.5f,// left
                0.5f, -0.5f, 0.0f, 0f, 0f, 1f, 0.5f,// right
                0.0f,  0.5f, 0.0f,  0f, 0f, 0f, 0.5f// top
        }, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesGlid);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 28, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 28, 12);
        glEnableVertexAttribArray(1);

    }

    public Monitor getVertexMonitor() {
        return Monitor.compose(false, Suite.set(red).set(green).set(blue).set(alpha));
    }

    public int getDimension() {
        return 4;
    }

    public void updateVertex(float[] vertex) {
        float r = red.getFloat(), g = green.getFloat(), b = blue.getFloat(), a = alpha.getFloat();
        for(int i = 6;i < vertex.length;i += 7) {
            vertex[i] = a;
            vertex[i-1] = b;
            vertex[i-2] = g;
            vertex[i-3] = r;
        }
        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);
    }

    @Override
    public void updateIndices(int[] indices) {
        indicesLength = indices.length;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesGlid);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void print() {
        shader.use();
        glBindVertexArray(getGlid());
        glDrawElements(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, 0);
    }

    public NumberVar getRed() {
        return red;
    }

    public NumberVar getGreen() {
        return green;
    }

    public NumberVar getBlue() {
        return blue;
    }

    public NumberVar getAlpha() {
        return alpha;
    }
}
