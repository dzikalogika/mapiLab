package app.model.window;

import app.model.Color;
import app.model.component.ColorRectangle;
import app.model.Point;
import app.model.graphic.Shader;
import brackettree.reader.BracketTree;

import static org.lwjgl.opengl.GL30.*;

public class ColorRectangleDrawer {

    int glid;
    Shader shader;

    private final int vertexGlid;

    public ColorRectangleDrawer(Shader shader) {
        glid = glGenVertexArrays();

        this.shader = shader != null ? shader : BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("forest/colorRectangleShader.tree")).as(Shader.class);

        vertexGlid = glGenBuffers();
        glBindVertexArray(glid);

        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferData(GL_ARRAY_BUFFER, 32, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 32, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 32, 8);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 4, GL_FLOAT, false, 32, 16);
        glEnableVertexAttribArray(2);
        glBindVertexArray(0);

    }

    public void setWindowSize(int width, int height) {
        shader.use();
        shader.set("windowSize", width, height);
    }

    public void draw(ColorRectangle rectangle) {
        shader.use();

        float width = rectangle.getWidth();
        float height = rectangle.getHeight();
        Point position = rectangle.getPosition();
        Color color = rectangle.getColor();

        float[] vertex = new float[]{
                position.getX(), position.getY(), width, height,
                color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()
        };

        glBindVertexArray(glid);
        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertex);
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
