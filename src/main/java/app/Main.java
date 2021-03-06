package app;

import app.model.*;
import app.model.input.Keyboard;
import app.model.variable.*;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Statement;

import static org.lwjgl.glfw.GLFW.*;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(Suite.
                set(Window.class, Main.class).
                set(Color.RED, .2f).
                set(Color.GREEN, .5f).
                set(Color.BLUE, .4f));
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

        NumberVar textH = NumberVar.emit(50);

        NumberVar r = NumberVar.emit(0);
        NumberVar g = NumberVar.emit(1);
        NumberVar b = NumberVar.emit(1);
        NumberVar xs = NumberVar.emit(50);
        NumberVar ys = NumberVar.emit(50);
        NumberVar w = NumberVar.emit(60);
        NumberVar h = NumberVar.emit(60);

        Rectangle rect = new Rectangle();

        place(rect(rect).
                sides(100, 10, 10, 50).
                color(0.2, 0.5, 0.5).
                place(rect().
                        color(r, g, b).
                        set("face", 0.4).
                        place(rect().
                                sides(5, 5, 5, 5).
                                color(0.1, 0.1, 0.1).
                                set("face", 0.3)
                        )
                )
        );

        Text text = new Text();
        place(text(text).
                horizontalCenter(xs, Unit.PERCENT).
                verticalCenter(ys, Unit.PERCENT).
                height(textH).
                content("W").
                redColor(200).
                blueColor(200));

        instant(num(mouse.getPosition()), text.content(), Subject::asString);

        instant(Suite.set(keyboard.getKey(GLFW_KEY_Z).getPressed().select((b0, b1) -> b1)).set(textH.weak()), textH, s -> s.recent().asInt() + 10);

        instant(Suite.set(keyboard.getCharEvent()).set(text.content().weak()), text.content(), s -> {
            Keyboard.CharEvent e = s.asExpected();
            String content = s.recent().asString();
            return new StringBuilder(content).appendCodePoint(e.getCodepoint()).toString();
        });

        Fun t1 = instant(Suite.set(text.content().weak()).set(keyboard.getKey(GLFW_KEY_BACKSPACE).
                getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), text.content(), s -> {
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
                s -> s.asInt() -1);

        instant(Suite.set(xs.weak()).set(keyboard.getKey(GLFW_KEY_RIGHT).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), xs,
                s -> s.asInt() +1);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_DOWN).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() +1);

        instant(Suite.set(ys.weak()).set(keyboard.getKey(GLFW_KEY_UP).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), ys,
                s -> s.asInt() -1);

        instant(Suite.set(w.weak()).set(keyboard.getKey(GLFW_KEY_W).getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), w,
                s -> keyboard.getKey(GLFW_KEY_LEFT_SHIFT).getPressed().get() ? s.asInt() + 10 : s.asInt() - 10);
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