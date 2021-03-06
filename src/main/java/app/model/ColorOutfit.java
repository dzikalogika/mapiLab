package app.model;

import app.model.variable.Monitor;
import jorg.jorg.Jorg;


import static org.lwjgl.opengl.GL30.*;

public class ColorOutfit extends GLObject implements Outfit {

    public static final Shader defaultShader = Jorg.withRecipe(Shader::form).read(
            Shader.class.getClassLoader().getResourceAsStream("jorg/colorShader.jorg"));

    Color color;

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
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesGlid);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 28, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 28, 12);
        glEnableVertexAttribArray(1);

    }

    public Monitor getVertexMonitor() {
        return color.monitor();
    }

    public int getDimension() {
        return 4;
    }

    public void updateVertex(float[] vertex) {
        float r = color.red.getFloat(), g = color.green.getFloat(), b = color.blue.getFloat(), a = color.alpha.getFloat();
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
        defaultShader.use();
        glBindVertexArray(getGlid());
        glDrawElements(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, 0);
    }
}
