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

public class Window extends Composite {

    static Subject windows = Suite.set();

    public static void play(Subject sub) {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");

        Window window = Window.create(
                sub.get(Window.class).orGiven(Window.class),
                sub.get(Dim.WIDTH).orGiven(800),
                sub.get(Dim.HEIGHT).orGiven(600),
                sub.get(Color.RED).orGiven(0.2f),
                sub.get(Color.GREEN).orGiven(0.4f),
                sub.get(Color.BLUE).orGiven(0.4f));

        glfwShowWindow(window.getGlid());

        glfwSwapInterval(1);

        while(windows.settled())
        {
//            float currentFrame = (float)glfwGetTime();
//            deltaTime = currentFrame - lastFrame;
//            lastFrame = currentFrame;

            glfwPollEvents();
            for(Window win : windows.values(Window.class)) {
                win.play();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                win.print();
                glfwSwapBuffers(win.getGlid());
                if(glfwWindowShouldClose(win.getGlid()))windows.unset(win.getGlid());
            }
        }

        glfwTerminate();
    }

    public static Window create(Class<? extends Window> windowType, int width, int height, float red, float green, float blue) {
        Window window = null;
        try {
            window = windowType.getConstructor(int.class, int.class).newInstance(width, height);
            glfwMakeContextCurrent(window.getGlid());
            GL.createCapabilities();
            GLUtil.setupDebugMessageCallback();
//            glEnable(GL_ALPHA_TEST);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            window.redColor.set(red);
            window.greenColor.set(green);
            window.blueColor.set(blue);
            window.alphaColor.set(1);
            window.intent(num(window.redColor, window.greenColor, window.blueColor, window.alphaColor), s -> {
                glClearColor(s.asFloat(), s.get(1).asFloat(), s.get(2).asFloat(), s.get(3).asFloat());
            }).press(true);

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
    protected final NumberVar width;
    protected final NumberVar height;
    final NumberVar redColor = NumberVar.emit(0);
    final NumberVar greenColor = NumberVar.emit(0);
    final NumberVar blueColor = NumberVar.emit(0);
    final NumberVar alphaColor = NumberVar.emit(1);

    public Window(int width, int height) {
        this.width = NumberVar.emit(width);
        this.height = NumberVar.emit(height);
        glid = glfwCreateWindow(this.width.getInt(), this.height.getInt(), "LearnOpenGL", NULL, NULL);
        if (glid == NULL) throw new RuntimeException("Window based failed");

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

    @Override
    void print() {
        components.values().filter(Component.class).forEach(Component::print);
    }

    protected void ready() {}

    public long getGlid() {
        return glid;
    }

    public NumberVar getWidth() {
        return width;
    }

    public NumberVar getHeight() {
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

    private static final Exp textExpLeftTop = Exp.compile("a * b / 100");
    private static final Exp textExpRightBottom = Exp.compile("a - a * b / 100");

    Subject textTransform(Subject sketch) {
        Subject r = Suite.set();
        for(var s : sketch) {
            var k = s.key().direct();
            if(k == Side.LEFT || k == Side.RIGHT || k == Pos.HORIZONTAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, pixelParcel.ware);
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.difference(width, pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            abc(width, percentParcel.ware), textExpLeftTop));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            abc(width, percentParcel.ware), textExpRightBottom));
                }
            } else if(k == Side.BOTTOM || k == Side.TOP || k == Pos.VERTICAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, pixelParcel.ware);
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.difference(height, pixelParcel.ware));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            abc(height, percentParcel.ware), textExpLeftTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            abc(height, percentParcel.ware), textExpRightBottom));
                }
            } else r.inset(s);
        }
        r.put("pw", width).put("ph", height);
        return r;
    }

    private static final Exp rectExpLeftBottom = Exp.compile("a * 2 / b - 1");
    private static final Exp rectExpRightTop = Exp.compile("a * -2 / b + 1");
    private static final Exp rectExpWidthHeight = Exp.compile("a / b * 2");
    private static final Exp rectExpPercentLeftBottom = Exp.compile("a / 50 - 1");
    private static final Exp rectExpPercentRightTop = Exp.compile("1 - a / 50");
    private static final Exp rectExpPercentWidthHeight = Exp.compile("a / 50");

    Subject rectTransform(Subject sketch) {
        Subject r = Suite.set();
        for(var s : sketch) {
            var k = s.key().direct();
            if(k == Pos.HORIZONTAL_CENTER || k == Side.LEFT || k == Side.RIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            abc(pixelParcel.ware, width), rectExpLeftBottom));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            abc(pixelParcel.ware, width), rectExpRightTop));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentLeftBottom));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentRightTop));
                }
            } else if(k == Pos.VERTICAL_CENTER || k == Side.TOP || k == Side.BOTTOM) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            abc(pixelParcel.ware, height), rectExpRightTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            abc(pixelParcel.ware, height), rectExpLeftBottom));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentRightTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentLeftBottom));
                }
            } else if(k == Dim.WIDTH) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed(
                            abc(pixelParcel.ware, width), rectExpWidthHeight));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentWidthHeight));
                }
            } else if(k == Dim.HEIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed(
                            abc(pixelParcel.ware, height), rectExpWidthHeight));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed(
                            abc(percentParcel.ware), rectExpPercentWidthHeight));
                }
            } else r.inset(s);
        }
        r.put(Composite.class, this).put(Window.class, this);
        return r;
    }
}
