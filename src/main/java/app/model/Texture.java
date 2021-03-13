package app.model;

import suite.suite.Subject;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static suite.suite.$uite.$;
import static suite.suite.Suite.join;

public class Texture {

    public static final Subject textureWrappers = join(
            $("repeat", GL_REPEAT),
            $(GL_REPEAT, GL_REPEAT),
            $("mirrored-repeat", GL_MIRRORED_REPEAT),
            $(GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT),
            $("clamp-to-edge", GL_CLAMP_TO_EDGE),
            $(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE),
            $("clamp-to-border", GL_CLAMP_TO_BORDER),
            $(GL_CLAMP_TO_BORDER, GL_CLAMP_TO_BORDER)
    );

    public static final Subject textureFilters = join(
            $("linear", GL_LINEAR),
            $(GL_LINEAR, GL_LINEAR),
            $("nearest", GL_NEAREST),
            $(GL_NEAREST, GL_NEAREST)
    );

    public static final Subject textureFormats = join(
            $("rgb", GL_RGB),
            $(GL_RGB, GL_RGB),
            $("rgba", GL_RGBA),
            $(GL_RGBA, GL_RGBA)
    );

    public static Texture compose(Subject $) {
        String file = $.in("file").asExpected();

        int format = textureFormats.in( $.get("format").in().raw() ).orGiven(GL_RGB);
        boolean flip = !($.absent("flip") || !$.in("flip").asBoolean(false));
        int textureWrapS = textureWrappers.in( $.get("texture-wrap-s", "texture-wrap").in().raw() ).orGiven(GL_CLAMP_TO_EDGE);
        int textureWrapT = textureWrappers.in( $.get("texture-wrap-t", "texture-wrap").in().raw() ).orGiven(GL_CLAMP_TO_EDGE);
        int textureMinFilter = textureFilters.in( $.get("texture-min-filter", "texture-filter").in().raw() ).orGiven(GL_LINEAR);
        int textureMaxFilter = textureFilters.in( $.get("texture-max-filter", "texture-filter").in().raw() ).orGiven(GL_LINEAR);

        return new Texture(file, flip, textureWrapS, textureWrapT, textureMinFilter, textureMaxFilter, format);
    }

    int glid;

    public Texture(String file,  boolean flipImage, int textureWrapS, int textureWrapT, int textureMinFilter, int textureMaxFilter,
                   int format) {
        glid = glGenTextures();
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

    public int getGlid() {
        return glid;
    }
}
