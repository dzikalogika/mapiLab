package app.model;

import app.model.variable.Var;
import jorg.jorg.Jorg;
import suite.suite.Suite;


import static org.lwjgl.opengl.GL30.*;

public class ColorOutfit extends GLObject implements Outfit {

    public static final Shader shader = Jorg.withRecipe(Shader::form).read(Shader.class.getClassLoader().getResourceAsStream("jorg/colorShader.jorg"));

    private final Var<Float> red;
    private final Var<Float> green;
    private final Var<Float> blue;
    private final Var<Float> alpha;
    private int indicesLength = 0;
    private final int vertexGlid;
    private final int indicesGlid;

    public ColorOutfit(float red, float green, float blue, float alpha) {
        this(Var.create(red), Var.create(green), Var.create(blue), Var.create(alpha));
    }

    public ColorOutfit(Var<Float> red, Var<Float> green, Var<Float> blue, Var<Float> alpha) {
        super(glGenVertexArrays());
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;

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

    public Var<Object> getVertexMonitor() {
        return Var.compose(false, Suite.set(red).set(green).set(blue).set(alpha));
    }

    public int getDimension() {
        return 4;
    }

    public void updateVertex(float[] vertex) {
        for(int i = 6;i < vertex.length;i += 7) {
            vertex[i] = alpha.get();
            vertex[i-1] = blue.get();
            vertex[i-2] = green.get();
            vertex[i-3] = red.get();
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

    public Var<Float> getRed() {
        return red;
    }

    public Var<Float> getGreen() {
        return green;
    }

    public Var<Float> getBlue() {
        return blue;
    }

    public Var<Float> getAlpha() {
        return alpha;
    }
}
