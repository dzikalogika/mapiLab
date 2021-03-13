package app.model;

import brackettree.reader.BracketTree;


import java.util.Arrays;

import static org.lwjgl.opengl.GL30.*;

public class ColorPolygonOutfit implements PolygonOutfit {

    int glid;
    Color color;
    Shader shader;

    private int indicesLength = 0;
    private final int vertexGlid;
    private final int indicesGlid;

    public ColorPolygonOutfit() {
        glid = glGenVertexArrays();

        shader = BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("jorg/colorShader.jorg")).as(Shader.class);

        vertexGlid = glGenBuffers();
        indicesGlid = glGenBuffers();
        indicesLength = 3;
        glBindVertexArray(glid);

        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesGlid);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 28, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 28, 12);
        glEnableVertexAttribArray(1);

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDimension() {
        return 4;
    }

    public void setVertex(Point[] vertex) {
        float r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        float[] v = new float[7 * vertex.length];
        for(int i = 0;i < vertex.length;) {
            var i7 = i * 7 + 6;
            v[i7] = a;
            v[i7-1] = b;
            v[i7-2] = g;
            v[i7-3] = r;
            v[i7-4] = vertex[i].z;
            v[i7-5] = vertex[i].y;
            v[i7-6] = vertex[i].x;
            ++i;
        }
        System.out.println(Arrays.toString(v));
        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferData(GL_ARRAY_BUFFER, v, GL_STATIC_DRAW);
    }

    @Override
    public void setIndices(int[] indices) {
        indicesLength = indices.length;
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesGlid);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void print() {
        shader.use();
        glBindVertexArray(glid);
        glDrawElements(GL_TRIANGLES, indicesLength, GL_UNSIGNED_INT, 0);
    }
}
