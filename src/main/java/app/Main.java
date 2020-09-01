package app;

import app.model.*;
import app.model.input.Keyboard;
import app.model.input.Mouse;
import app.model.variable.*;
import suite.suite.Suite;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(Suite.set(Window.class, Main.class));
    }

    Var<String> str;
    Var<String> space;
    Var<String> button;
    Text text;
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

        text = text(Suite.set("x", pc(50)).set("y", pc(50)).set("size", 50).set("text", "t").
                set("r", 200).set("b", 200));

        instant(Suite.set(keyboard.getCharEvent()).set(text.getContent().weak()), text.getContent(), s -> {
            Keyboard.CharEvent e = s.asExpected();
            String content = s.recent().asString();
            return new StringBuilder(content).appendCodePoint(e.getCodepoint()).toString();
        });

        Fun t1 = instant(Suite.set(text.getContent().weak()).set(keyboard.getKey(GLFW_KEY_BACKSPACE).
                getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), text.getContent(), s -> {
            String content = s.asString();
            return content.length() > 0 ? content.substring(0, content.length() - 1) : "";
        });

        instant(Suite.set(t1).set(Fun.SELF).set(keyboard.getKey(GLFW_KEY_ENTER).getState()), s -> {
            s.asGiven(Fun.class).detach();
            s.get(Fun.SELF).asGiven(Fun.class).detach();
        });

        button = Var.compose("", Suite.set(mouse.getButton(GLFW_MOUSE_BUTTON_1).getState()), s -> {
            Mouse.ButtonEvent e = s.asExpected();
            return e.getPosition().toString() + "  " + e.getAction() + "   " + e.getModifiers();
        });

        NumberVar r = NumberVar.create(1);
        NumberVar g = NumberVar.create(1);
        NumberVar b = NumberVar.create(1);
        NumberVar xs = NumberVar.create(400);
        NumberVar ys = NumberVar.create(50);
        NumberVar w = NumberVar.create(800);
        NumberVar h = NumberVar.create(10);

        rect = rect(Suite.set("x", px(xs)).set("y", px(ys)).
                set("w", px(w)).set("h", pc(h)).set("r", r).set("g", g).set("b", b).set("a", .5f));

        instant(Suite.set(keyboard.getKey(GLFW_KEY_SPACE).getState().select((s1, s2) -> s2 == GLFW_PRESS)), Suite.
                set("r", r).set("g", g).set("b", b), s -> {
            return Suite.set("r", (float)Math.random()).set("g", (float)Math.random()).set("b", (float)Math.random());
        });

        instant(Suite.set(xs.weak()).set(keyboard.getKey(GLFW_KEY_LEFT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), xs,
                s -> s.asInt() - 10);

        instant(Suite.set(xs.weak()).set(keyboard.getKey(GLFW_KEY_RIGHT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), xs,
                s -> s.asInt() + 10);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_DOWN).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() + 10);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_UP).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() - 10);

        instant(Suite.set(w.weak()).set(keyboard.getKey(GLFW_KEY_W).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), w,
                s -> keyboard.getKey(GLFW_KEY_LEFT_SHIFT).getPressed().get() ? s.asInt() + 10 : s.asInt() - 10);

    }

    @Override
    public void play() {
        super.play();
        processInput(getGlid());

        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        text.render();
        rect.print();

        glfwSwapBuffers(getGlid());
//        glfwSetWindowShouldClose(getGlid(), true);
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