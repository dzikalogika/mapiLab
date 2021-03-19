package app.model;

import app.model.input.Keyboard;
import app.model.input.Mouse;
import app.model.input.Var;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import suite.suite.Subject;

import java.lang.reflect.InvocationTargetException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.system.MemoryUtil.NULL;
import static suite.suite.$uite.$;

public class Window {

    static Subject $windows = $();

    public static void play(Subject $sub) {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");
        Window window = Window.create(
                $sub.in(Window.class).asExpected(),
                $sub.get("width", "w").in().asInt(800),
                $sub.get("height", "h").in().asInt(600),
                $sub.get("red", "r").in().asFloat(0.2f),
                $sub.get("green", "g").in().asFloat(0.4f),
                $sub.get("blue", "b").in().asFloat(0.4f));
        glfwShowWindow(window.getGlid());

        glfwSwapInterval(1);

        while($windows.present())
        {
//            float currentFrame = (float)glfwGetTime();
//            deltaTime = currentFrame - lastFrame;
//            lastFrame = currentFrame;

            glfwPollEvents();
            for(Window win : $windows.eachIn().eachAs(Window.class)) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                win.update_();
                glfwSwapBuffers(win.getGlid());
                if(glfwWindowShouldClose(win.getGlid())) $windows.unset(win.getGlid());
            }
        }

        glfwTerminate();
    }

    public static Window create(Class<? extends Window> windowType, int width, int height, float red, float green, float blue) {
        Window window = null;
        try {
            window = windowType.getConstructor().newInstance();
            window.setWidth(width);
            window.setHeight(height);
            window.setColor(Color.mix(red, green, blue, 1));
            window.setup0();

            glfwMakeContextCurrent(window.getGlid());

            GL.createCapabilities();
            GLUtil.setupDebugMessageCallback();

//            glEnable(GL_ALPHA_TEST);
//            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            window.setup1();
            window.setup();

            long glid = window.getGlid();

            $windows.aimedPut($windows.first().raw(), glid, window);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return window;
    }

    long glid;

    protected final Keyboard keyboard = new Keyboard();
    protected final Mouse mouse = new Mouse(this);
    protected Drawer drawer;
    Var<Number> width = new Var<>(800);
    Var<Number> height = new Var<>(600);
    protected Color color;

    void setup0() {
        glid = glfwCreateWindow(width.get().intValue(), height.get().intValue(), "Fancy title", NULL, NULL);
        if (glid == NULL) throw new RuntimeException("Window based failed");

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        glfwSetFramebufferSizeCallback(glid, (win, w, h) -> {
            glfwMakeContextCurrent(win);
            glViewport(0, 0, w, h);
            setWidth(w);
            setHeight(h);
        });

        glfwSetCursorPosCallback(glid, mouse::reportPositionEvent);
        glfwSetMouseButtonCallback(glid, mouse::reportMouseButtonEvent);
        glfwSetScrollCallback(glid, mouse::reportScrollEvent);

        glfwSetKeyCallback(glid, keyboard::reportKeyEvent);
        glfwSetCharModsCallback(glid, keyboard::reportCharEvent);
    }

    void setup1() {
        drawer = new Drawer(this, new ColorRectangleDrawer(),
                new ColorTextDrawer(new Font("ttf/trebuc.ttf"), 24));
    }

    public void setup() {

    }

    public void update_() {
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        update();
        drawer.draw();
        processInput(getGlid());
    }

    public void update() {

    }

    public void setColor(Color color) {
        this.color = color;
    }

    private void setWidth(int width) {
        this.width.set(width);
    }

    private void setHeight(int height) {
        this.height.set(height);
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width.get().intValue();
    }

    public int getHeight() {
        return height.get().intValue();
    }

    public long getGlid() {
        return glid;
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

    public void draw(ColorRectangle rectangle) {
        drawer.set(rectangle);
    }

    public void draw(ColorText text) {
        drawer.set(text);
    }

    void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}
