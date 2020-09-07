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

public class Window extends Playground implements Frame{

    static Subject windows = Suite.set();

    public static void play(Subject sub) {
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if ( !glfwInit() ) throw new IllegalStateException("Unable to initialize GLFW");

        Window window = Window.create(
                sub.get(Window.class).orGiven(Window.class),
                sub.get("w").orGiven(800),
                sub.get("h").orGiven(600));

        glfwShowWindow(window.getGlid());

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
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                for(Printable p : win.components.values(Printable.class)) {
                    p.print();
                }
                glfwSwapBuffers(win.getGlid());
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
//            glEnable(GL_ALPHA_TEST);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
    protected final Subject components = Suite.set();

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

    public void append(Printable component) {
        components.set(component);
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

    @Override
    public NumberVar windowWidth() {
        return width;
    }

    @Override
    public NumberVar windowHeight() {
        return height;
    }

    private static final Exp textExpLeftTop = Exp.compile("a * b / 100");
    private static final Exp textExpRightBottom = Exp.compile("a - a * b / 100");

    public Text text(Subject sub) {
        Subject r = Suite.set();
        for(var s : sub) {
            var k = s.key().direct();
            if(k == Side.LEFT || k == Side.RIGHT || k == Pos.HORIZONTAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, pixelParcel.ware);
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.difference(Suite.add(width).add(pixelParcel.ware)));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            Exp.params(width, percentParcel.ware), textExpLeftTop));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            Exp.params(width, percentParcel.ware), textExpRightBottom));
                }
            } else if(k == Side.BOTTOM || k == Side.TOP || k == Pos.VERTICAL_CENTER) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, pixelParcel.ware);
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.difference(Suite.add(height).add(pixelParcel.ware)));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            Exp.params(height, percentParcel.ware), textExpLeftTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            Exp.params(height, percentParcel.ware), textExpRightBottom));
                }
            } else r.inset(s);
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
            var k = s.key().direct();
            if(k == Pos.HORIZONTAL_CENTER || k == Side.LEFT || k == Side.RIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, width), rectExpLeftBottom));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, width), rectExpRightTop));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.LEFT) r.set(k, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentLeftBottom));
                    else if(wb == Side.RIGHT) r.set(k, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentRightTop));
                }
            } else if(k == Pos.VERTICAL_CENTER || k == Side.TOP || k == Side.BOTTOM) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, height), rectExpRightTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, height), rectExpLeftBottom));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null || wb == Side.TOP) r.set(k, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentRightTop));
                    else if(wb == Side.BOTTOM) r.set(k, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentLeftBottom));
                }
            } else if(k == Dim.WIDTH) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, width), rectExpWidthHeight));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.WIDTH, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentWidthHeight));
                }
            } else if(k == Dim.HEIGHT) {
                if(s.assigned(PixelParcel.class)) {
                    PixelParcel pixelParcel = s.asExpected();
                    var wb = pixelParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed(
                            Exp.params(pixelParcel.ware, height), rectExpWidthHeight));
                } else if(s.assigned(PercentParcel.class)) {
                    PercentParcel percentParcel = s.asExpected();
                    var wb = percentParcel.waybill;
                    if(wb == null) r.set(Dim.HEIGHT, NumberVar.expressed(
                            Exp.params(percentParcel.ware), rectExpPercentWidthHeight));
                }
            } else r.inset(s);
        }
        return Rectangle.form(r);
    }

    public InterFrame frame(Subject sub) {
        sub.put(Frame.class, this);
        sub.getDone(Rectangle.class, this::rect, sub);
        return InterFrame.form(sub);
    }
}
