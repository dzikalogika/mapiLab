package app;

import app.model.*;
import app.model.input.Keyboard;
import app.model.input.Mouse;
import app.model.variable.*;
import org.joml.Vector3f;
import suite.suite.Suite;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(Suite.set(Window.class, Main.class));
    }

    TextGraphic text;
    Var<String> str;
    Var<String> space;
    Var<String> button;
    Text text1;
    Rectangle rect;

    static float deltaTime = 0.0f;	// Time between current frame and last frame
    static float lastFrame = 0.0f; // Time of last frame

    static boolean firstMouse = true;
    static float lastX, lastY;

    public Main(int width, int height) {
        super(width, height);
    }

    @Override
    protected void ready() {
        text = TextGraphic.form(Suite.set());
        text.getProjectionWidth().assign(width);
        text.getProjectionHeight().assign(height);

        str = Var.compose(Suite.
                        set(keyboard.getKey(GLFW_KEY_UP).getPressed()).
                        set(keyboard.getKey(GLFW_KEY_LEFT).getPressed()).
                        set(keyboard.getKey(GLFW_KEY_DOWN).getPressed()).
                        set(keyboard.getKey(GLFW_KEY_RIGHT).getPressed()),
                s -> (s.getAt(0).asGiven(Boolean.class) ? "^" : "_") +
                        (s.getAt(1).asGiven(Boolean.class) ? "<" : "_") +
                        (s.getAt(2).asGiven(Boolean.class) ? "v" : "_") +
                        (s.getAt(3).asGiven(Boolean.class) ? ">" : "_"));

        space = Var.compose("_@/\"",
                Suite.set(Var.OWN_VALUE).set(keyboard.getKey(GLFW_KEY_SPACE).getState().
                        suppress((s1, s2) -> s2 == GLFW_RELEASE)),
                s -> "." + s.asString());

        text1 = Text.form(Suite.set("x", 30).set("y", 50).set("size", 50f).set("text", "text").set("r", 200).set("b", 200));

        setOn(Suite.set(keyboard.getCharEvent()).set(text1.getContent().weak()), text1.getContent(), s -> {
            Keyboard.CharEvent e = s.asExpected();
            String content = s.recent().asString();
            return new StringBuilder(content).appendCodePoint(e.getCodepoint()).toString();
        });

        Fun t1 = setOn(Suite.set(text1.getContent().weak()).set(keyboard.getKey(GLFW_KEY_BACKSPACE).
                getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), text1.getContent(), s -> {
            String content = s.asString();
            return content.length() > 0 ? content.substring(0, content.length() - 1) : "";
        });

        setOn(Suite.set(t1).set(Fun.SELF).set(keyboard.getKey(GLFW_KEY_ENTER).getState()), s -> {
            s.asGiven(Fun.class).detach();
            s.get(Fun.SELF).asGiven(Fun.class).detach();
        });

        button = Var.compose("", Suite.set(mouse.getButton(GLFW_MOUSE_BUTTON_1).getState()), s -> {
            Mouse.ButtonEvent e = s.asExpected();
            return e.getPosition().toString() + "  " + e.getAction() + "   " + e.getModifiers();
        });

        Var<Float> r = Var.create(1f);
        Var<Float> g = Var.create(1f);
        Var<Float> b = Var.create(1f);
        Var<Double> x = Var.create(0.25);
        Var<Double> y = Var.create(0.25);
        Var<Double> w = Var.create();

        rect = Rectangle.form(Suite.set("x", x).set("y", y).set("w", 0.5).set("h", 0.5).set("r", r).set("g", g).set("b", b));
//        rect = rectangle(Suite.set("w", width.percent(30)))

        setOn(Suite.set(keyboard.getKey(GLFW_KEY_SPACE).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), Suite.
                set("r", r).
                set("g", g).
                set("b", b), s -> {
            return Suite.set("r", (float)Math.random()).set("g", (float)Math.random()).set("b", (float)Math.random());
        });

        setOn(Suite.set(x.weak()).set(keyboard.getKey(GLFW_KEY_LEFT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), x,
                s -> {
                    System.out.println("left");
                    return s.asGiven(Double.class) - 0.1;
                });

        setOn(Suite.set(x.weak()).set(keyboard.getKey(GLFW_KEY_RIGHT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), x,
                s -> {
                    System.out.println("right");
                    return s.asGiven(Double.class) + 0.1;
                });

        setOn(Suite.set(y.weak()).set(keyboard.getKey(GLFW_KEY_DOWN).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), y,
                s -> {
                    System.out.println("down");
                    return s.asGiven(Double.class) - 0.1;
                });

        setOn(Suite.set(y.weak()).set(keyboard.getKey(GLFW_KEY_UP).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), y,
                s -> {
                    System.out.println("up");
                    return s.asGiven(Double.class) + 0.1;
                });

    }

    @Override
    protected void play() {
        processInput(getGlid());
        triggersRoot.get();

        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        text.render(str.get(), 30f, 300f, 1., new Vector3f(0.5f, 0.8f, 0.2f));
        text.render(space.get(), 30f, 400f, 1., new Vector3f(0.5f, 0.8f, 0.2f));
        text1.render();
        rect.print();

        glfwSwapBuffers(getGlid());
    }

    static void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
//        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
//            camera.processKeyboard(Camera.CameraMovement.FORWARD, deltaTime);
//        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
//            camera.processKeyboard(Camera.CameraMovement.BACKWARD, deltaTime);
//        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
//            camera.processKeyboard(Camera.CameraMovement.LEFT, deltaTime);
//        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
//            camera.processKeyboard(Camera.CameraMovement.RIGHT, deltaTime);
    }
}