package app.model;

import jorg.jorg.Reformable;
import suite.suite.Subject;
import org.joml.Matrix4f;
import suite.suite.Suite;

import static org.lwjgl.opengl.GL45.*;

public class Shader extends GLObject implements Reformable {

    public static Shader form(Subject sub) {
        String vertex = sub.get("vertex").asString();
        String fragment = sub.get("fragment").asString();

        return new Shader(vertex, fragment);
    }

    private Subject textures = Suite.set();

    public Shader(String vertex, String fragment) {
        super(glCreateProgram());

        int vertexGlid = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexGlid, vertex);
        glCompileShader(vertexGlid);

        int fragmentGlid = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentGlid, fragment);
        glCompileShader(fragmentGlid);

        glAttachShader(glid, vertexGlid);
        glAttachShader(glid, fragmentGlid);
        glLinkProgram(glid);

        glDeleteShader(vertexGlid);
        glDeleteShader(fragmentGlid);
    }

    public void use() {
        glUseProgram(glid);
    }

    public void set(String name, int value) {
        glUniform1i(glGetUniformLocation(glid, name), value);
    }

    public void set(String name, float value) {
        glUniform1f(glGetUniformLocation(glid, name), value);
    }

    public void set(String name, double value) {
        glUniform1d(glGetUniformLocation(glid, name), value);
    }

    public void set(String name, float v1, float v2, float v3) {
        glUniform3f(glGetUniformLocation(glid, name), v1, v2, v3);
    }

    public void set(String name, float v1, float v2, float v3, float v4) {
        glUniform4f(glGetUniformLocation(glid, name), v1, v2, v3, v4);
    }

    public void set(String name, Matrix4f matrix) {
        glUniformMatrix4fv(glGetUniformLocation(glid, name), false, matrix.get(new float[16]));
    }

    public void setTexture(String name, int index, Texture texture) {
        use();
        set("texture1", index);
        glActiveTexture(GL_TEXTURE0 + index);
        glBindTexture(GL_TEXTURE_2D, texture.getGlid());
    }
}
