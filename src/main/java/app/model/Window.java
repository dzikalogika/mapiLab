package app.model;

import app.model.input.Keyboard;
import app.model.input.Mouse;
import app.model.util.PercentParcel;
import app.model.util.PixelParcel;
import app.model.variable.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import suite.suite.Slot;
import suite.suite.Subject;
import suite.suite.Suite;

import java.lang.reflect.InvocationTargetException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window extends Playground {

    static Subject windows = Suite.set();

    public static void play(Subject sub) {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");

        Window window = Window.create(
                sub.get(Window.class).orGiven(Window.class),
                sub.get("w").orGiven(800),
                sub.get("h").orGiven(600));

        glfwShowWindow(window.getGlid());

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSwapInterval(1);

        while(windows.settled())
        {
//            float currentFrame = (float)glfwGetTime();
//            deltaTime = currentFrame - lastFrame;
//            lastFrame = currentFrame;

            glfwPollEvents();
            for(var s : windows) {
                Window win = s.asExpected();
                win.play();
                if(glfwWindowShouldClose(win.getGlid()))windows.unset(win.getGlid());
            }
        }

        glfwTerminate();
    }

    public static Window create(Class<? extends Window> windowType, int width, int height) {
        Window window = null;
        try {
            window = windowType.getConstructor(int.class, int.class).newInstance(width, height);
            glfwMakeContextCurrent(window.getGlid());
            GL.createCapabilities();
            GLUtil.setupDebugMessageCallback();
            window.ready();
            long glid = window.getGlid();
            windows.setAt(Slot.PRIME, glid, window);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return window;
    }

    final long glid;
    protected final Keyboard keyboard = new Keyboard();
    protected final Mouse mouse = new Mouse();
    protected final Var<Integer> width;
    protected final Var<Integer> height;

    public Window(int width, int height) {
        this.width = Var.create(width);
        this.height = Var.create(height);
        glid = glfwCreateWindow(this.width.get(), this.height.get(), "LearnOpenGL", NULL, NULL);
        if (glid == NULL) throw new RuntimeException("Window create failed");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        glfwSetFramebufferSizeCallback(glid, (win, w, h) -> {
            glfwMakeContextCurrent(win);
            glViewport(0, 0, w, h);
            this.width.set(w);
            this.height.set(h);
        });

        glfwSetCursorPosCallback(glid, mouse::reportPositionEvent);
        glfwSetMouseButtonCallback(glid, mouse::reportMouseButtonEvent);
        glfwSetScrollCallback(glid, mouse::reportScrollEvent);

        glfwSetKeyCallback(glid, keyboard::reportKeyEvent);
        glfwSetCharModsCallback(glid, keyboard::reportCharEvent);
    }

    protected void ready() {}

    public long getGlid() {
        return glid;
    }

    public Var<Integer> getWidth() {
        return width;
    }

    public Var<Integer> getHeight() {
        return height;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public void setCursor(int cursor) {
        glfwSetInputMode(glid, GLFW_CURSOR, cursor);
    }

    public void setLockKeyModifiers(boolean lock) {
        glfwSetInputMode(glid, GLFW_LOCK_KEY_MODS, lock ? GLFW_TRUE : GLFW_FALSE);
    }

    public static final int DEFAULT = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    public PixelParcel px(Object pixels) {
        return px(pixels, DEFAULT);
    }

    public PixelParcel px(Object pixels, int base) {
        return new PixelParcel(pixels, base);
    }

    public PercentParcel pc(Object percents) {
        return pc(percents, DEFAULT);
    }

    public PercentParcel pc(Object percents, int base) {
        return new PercentParcel(percents, base);
    }

    private static final Exp textExpLeftTop = Exp.compile("a * b / 100");
    private static final Exp textExpRightBottom = Exp.compile("a - a * b / 100");

    public Text text(Subject sub) {
        Subject r = Suite.set();
        for(var s : sub) {
            switch(s.key().asString()) {
                case "x":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT, LEFT -> r.set("x", pixelParcel.ware);
                            case RIGHT -> r.set("x", NumberVar.sub(Suite.add(width).add(pixelParcel.ware)));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT, LEFT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", width).set("b", percentParcel.ware), textExpLeftTop));
                            case RIGHT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", width).set("b", percentParcel.ware), textExpRightBottom));
                        }
                    }
                    break;
                case "y":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT, TOP -> r.set("y", pixelParcel.ware);
                            case BOTTOM -> r.set("y", NumberVar.sub(Suite.add(height).add(pixelParcel.ware)));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT, TOP -> r.set("y", NumberVar.compose(
                                    Suite.set("a", height).set("b", percentParcel.ware), textExpLeftTop));
                            case BOTTOM -> r.set("y", NumberVar.compose(
                                    Suite.set("a", height).set("b", percentParcel.ware), textExpRightBottom));
                        }
                    }
                    break;
                default:
                    r.inset(s);
            }
        }
        r.put("pw", width).put("ph", height);
        return Text.form(r);
    }

    private static final Exp rectExpLeftBottom = Exp.compile("a * 2 / b - 1");
    private static final Exp rectExpRightTop = Exp.compile("a * -2 / b + 1");
    private static final Exp rectExpWidthHeight = Exp.compile("a / b * 2");
    private static final Exp rectExpPercentLeftBottom = Exp.compile("a / 50 - 1");
    private static final Exp rectExpPercentRightTop = Exp.compile("1 - a / 50");
    private static final Exp rectExpPercentWidthHeight = Exp.compile("a / 50");

    public Rectangle rect(Subject sub) {
        Subject r = Suite.set();
        for(var s : sub) {
            switch(s.key().asString()) {
                case "x":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT, LEFT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", width), rectExpLeftBottom));
                            case RIGHT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", width), rectExpRightTop));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT, LEFT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentLeftBottom));
                            case RIGHT -> r.set("x", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentRightTop));
                        }
                    }
                    break;
                case "y":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT, TOP -> r.set("y", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", height), rectExpRightTop));
                            case BOTTOM -> r.set("y", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", height), rectExpLeftBottom));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT, BOTTOM -> r.set("y", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentLeftBottom));
                            case TOP -> r.set("y", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentRightTop));
                        }
                    }
                    break;
                case "w":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT -> r.set("w", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", width), rectExpWidthHeight));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT -> r.set("w", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentWidthHeight));
                        }
                    }
                    break;
                case "h":
                    if(s.assigned(PixelParcel.class)) {
                        PixelParcel pixelParcel = s.asExpected();
                        switch (pixelParcel.waybill) {
                            case DEFAULT -> r.set("h", NumberVar.compose(
                                    Suite.set("a", pixelParcel.ware).set("b", height), rectExpWidthHeight));
                        }
                    } else if(s.assigned(PercentParcel.class)) {
                        PercentParcel percentParcel = s.asExpected();
                        switch (percentParcel.waybill) {
                            case DEFAULT -> r.set("h", NumberVar.compose(
                                    Suite.set("a", percentParcel.ware), rectExpPercentWidthHeight));
                        }
                    }
                    break;
                default:
                    r.inset(s);
            }
        }
        return Rectangle.form(r);
    }
}
