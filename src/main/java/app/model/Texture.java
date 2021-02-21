package app.model;

import org.lwjgl.stb.STBImage;
import suite.suite.Subject;
import suite.suite.Suite;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture extends GLObject {

    public static final Subject textureWrappers = Suite.
            set("repeat", GL_REPEAT).
            set("mirroredRepeat", GL_MIRRORED_REPEAT).
            set("clampToEdge", GL_CLAMP_TO_EDGE).
            set("clampToBorder", GL_CLAMP_TO_BORDER);

    public static final Subject textureFilters = Suite.
            set("linear", GL_LINEAR).
            set("nearest", GL_NEAREST);

    public static final Subject textureFormats = Suite.
            set("rgb", GL_RGB).
            set("rgba", GL_RGBA);

    public static Texture form(Subject sub) {
        String file = sub.in("file").asExpected();
        int format = Suite.from(sub).get("format").map(String.class, String::toLowerCase).map(textureFormats).orGiven(GL_RGB);
        boolean flip = !(sub.absent("flip") || !sub.in("flip").get().asBoolean(false));
        int textureWrapS = Suite.from(sub).get("textureWrapS").or("textureWrap").map(textureWrappers).orGiven(GL_CLAMP_TO_EDGE);
        int textureWrapT = Suite.from(sub).get("textureWrapT").or("textureWrap").map(textureWrappers).orGiven(GL_CLAMP_TO_EDGE);
        int textureMinFilter = Suite.from(sub).get("textureMinFilter").or("textureFilter").map(textureFilters).orGiven(GL_LINEAR);
        int textureMaxFilter = Suite.from(sub).get("textureMaxFilter").or("textureFilter").map(textureFilters).orGiven(GL_LINEAR);

        return new Texture(file, flip, textureWrapS, textureWrapT, textureMinFilter, textureMaxFilter, format);
    }

    public Texture(String file,  boolean flipImage, int textureWrapS, int textureWrapT, int textureMinFilter, int textureMaxFilter,
                   int format) {
        super(glGenTextures());
        glBindTexture(GL_TEXTURE_2D, glid);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, textureWrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, textureWrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, textureMinFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, textureMaxFilter);
        int[] width = new int[1], height = new int[1], nrChannels = new int[1];
        stbi_set_flip_vertically_on_load(flipImage);
        ByteBuffer image = stbi_load(file, width, height, nrChannels, 0);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, format, GL_UNSIGNED_BYTE, image);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(image);
    }
}
