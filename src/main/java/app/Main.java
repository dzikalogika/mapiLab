package app;

import app.model.*;
import app.model.input.Keyboard;
import app.model.variable.*;
import suite.suite.Suite;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(Suite.set(Window.class, Main.class));
    }



    static float deltaTime = 0.0f;	// Time between current frame and last frame
    static float lastFrame = 0.0f; // Time of last frame

    static boolean firstMouse = true;
    static float lastX, lastY;

    public Main(int width, int height) {
        super(width, height);
    }

    @Override
    protected void ready() {
        Text text;

        NumberVar textH = NumberVar.emit(50);

        NumberVar r = NumberVar.emit(1);
        NumberVar g = NumberVar.emit(1);
        NumberVar b = NumberVar.emit(1);
        NumberVar xs = NumberVar.emit(50);
        NumberVar ys = NumberVar.emit(50);
        NumberVar w = NumberVar.emit(60);
        NumberVar h = NumberVar.emit(60);

        append(text = text(Suite.set(Pos.X, pc(50)).set(Pos.Y, pc(50)).set(Dim.H, textH).set("text", "W").
                set(Color.R, 200).set(Color.BLUE, 200)));

        append(frame(Suite.set(Side.L, px(50)).set(Side.R, px(200, Side.R)).set(Side.T, px(50)).set(Side.B, px(50, Side.B)).
                set(Color.R, 0.2).set(Color.G, 0.5).set(Color.B, 0.8).set("components",
                    Suite.add(Suite.set(Printable.class, Rectangle.class).set(Pos.X, pc(xs)).set(Pos.Y, pc(ys)).set(Dim.W, px(w)).set(Dim.H, px(h)).
                        set(Color.R, r).set(Color.G, g).set(Color.B, b).set("face", 0.4)))));

        instant(Suite.set(keyboard.getKey(GLFW_KEY_Z).getPressed().select((b0, b1) -> b1)).set(textH.weak()), textH, s -> s.recent().asInt() + 10);

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

        instant(Suite.set(keyboard.getKey(GLFW_KEY_SPACE).getState().select((s1, s2) -> s2 == GLFW_PRESS)), Suite.
                set("r", r).set("g", g).set("b", b), s -> {
            return Suite.set("r", (float)Math.random()).set("g", (float)Math.random()).set("b", (float)Math.random());
        });

        instant(Suite.set(xs.weak()).set(keyboard.getKey(GLFW_KEY_LEFT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), xs,
                s -> s.asInt() - 10);

        instant(Suite.set(xs.weak()).set(keyboard.getKey(GLFW_KEY_RIGHT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), xs,
                s -> s.asInt() + 10);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_DOWN).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() - 10);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_UP).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() + 10);

        instant(Suite.set(w.weak()).set(keyboard.getKey(GLFW_KEY_W).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), w,
                s -> keyboard.getKey(GLFW_KEY_LEFT_SHIFT).getPressed().get() ? s.asInt() + 10 : s.asInt() - 10);

        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    }

    @Override
    public void play() {
        super.play();
        processInput(getGlid());
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