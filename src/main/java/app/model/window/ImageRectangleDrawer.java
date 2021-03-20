package app.model.window;

import app.model.Point;
import app.model.component.ImageRectangle;
import app.model.graphic.LoadedImage;
import app.model.graphic.Shader;
import app.model.image.ImageManager;
import app.model.trade.Agent;
import app.model.trade.Component;
import app.model.trade.Host;
import brackettree.reader.BracketTree;

import static org.lwjgl.opengl.GL30.*;

public class ImageRectangleDrawer extends Component {

    int glid;
    Shader shader;

    private final int vertexGlid;

    public ImageRectangleDrawer(Host host, Shader shader) {
        super(host);
        glid = glGenVertexArrays();

        this.shader = shader != null ? shader : BracketTree.read(Shader.class.getClassLoader().
                getResourceAsStream("forest/textureShader.tree")).as(Shader.class);

        vertexGlid = glGenBuffers();
        glBindVertexArray(glid);

        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferData(GL_ARRAY_BUFFER, 16, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, 8);
        glEnableVertexAttribArray(1);
        glBindVertexArray(0);

    }

    public void setWindowSize(int width, int height) {
        shader.use();
        shader.set("windowSize", width, height);
    }

    public void draw(ImageRectangle rectangle) {
        shader.use();

        LoadedImage image = order(ImageManager.class).getImage(rectangle.getImage());
        int texGlid = image.getGlid();
        float width = rectangle.getWidth();
        float height = rectangle.getHeight();
        Point position = rectangle.getPosition();

        float[] vertex = new float[]{
                position.getX(), position.getY(), width, height
        };

        glBindVertexArray(glid);
        glBindTexture(GL_TEXTURE_2D, texGlid);
        glBindBuffer(GL_ARRAY_BUFFER, vertexGlid);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertex);
        glDrawArrays(GL_POINTS, 0, 1);
    }
}
