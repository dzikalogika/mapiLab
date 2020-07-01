package app;

import app.model.*;
import app.model.variable.*;
import jorg.jorg.Jorg;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Impression;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    static Camera camera = Camera.form(Jorg.withAdapter("speed", Camera.MOVEMENT_SPEED).parse("[pos]#1 [#speed]5,0 #[1]0,0]0,0]3,0"));
    static TextGraphic text;
    static KeyboardController keyboard = new KeyboardController();
    static MouseController mouse = new MouseController();

    static float deltaTime = 0.0f;	// Time between current frame and last frame
    static float lastFrame = 0.0f; // Time of last frame

    static boolean firstMouse = true;
    static float lastX, lastY;

    static Var<Integer> windowWidth = new Var<>(800);
    static Var<Integer> windowHeight = new Var<>(600);

    static Subject monitors = Suite.set();

    public static Monitor setOn(Subject monitored) {
        Monitor monitor = new Monitor(monitored);
        monitors.set(monitor);
        return monitor;
    }

    public static Trigger setOn(Subject monitored, Impression impression) {
        Trigger trigger = new Trigger(Trigger.INSTANT, monitored, impression);
        monitors.set(trigger);
        return trigger;
    }

    public static boolean detection(Object key) {
        var s = monitors.get(key);
        if(s.settled())return s.asGiven(Monitor.class).detection();
        return false;
    }

    public static void cancel(Object key) {
        var s = monitors.get(key);
        if(s.settled()) {
            monitors.unset(s.key().direct());
            s.asGiven(Monitor.class).abort();
        }
    }

    public static void main(String[] args) {


        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        long window = glfwCreateWindow(windowWidth.get(), windowHeight.get(), "LearnOpenGL", NULL, NULL);

        if (window == NULL)
        {
            System.out.println("Failed to create GLFW window");
            glfwTerminate();
            return;
        }

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GL.createCapabilities();
        GLUtil.setupDebugMessageCallback();

//        glfwSetWindowOpacity(window, 0.5f);

        text = TextGraphic.form(Suite.set());
        text.getProjectionWidth().assign(windowWidth);
        text.getProjectionHeight().assign(windowHeight);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwSetInputMode(window, GLFW_LOCK_KEY_MODS, GLFW_TRUE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSwapInterval(1);

        glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
            glfwMakeContextCurrent(w);
            glViewport(0, 0, width, height);
            windowWidth.set(width);
            windowHeight.set(height);
        });

        glfwSetCursorPosCallback(window, mouse::reportPositionEvent);
        glfwSetMouseButtonCallback(window, mouse::reportMouseButtonEvent);
        glfwSetScrollCallback(window, mouse::reportScrollEvent);

        glfwSetKeyCallback(window, keyboard::reportKeyEvent);
        glfwSetCharModsCallback(window, keyboard::reportCharEvent);

        float[] vertices = new float[]{
            -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                    0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                    0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                    0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                    0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                    0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                    -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                    -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                    -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                    0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                    0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                    0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                    0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                    0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                    0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                    0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                    -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                    -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                    0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                    0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                    0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                    -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                    -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };

        Vector3f[] cubePositions = new Vector3f[]{
            new Vector3f( 0.0f,  0.0f,  0.0f),
            new Vector3f( 2.0f,  5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f( 2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f,  3.0f, -7.5f),
            new Vector3f( 1.3f, -2.0f, -2.5f),
            new Vector3f( 1.5f,  2.0f, -2.5f),
            new Vector3f( 1.5f,  0.2f, -1.5f),
            new Vector3f(-1.3f,  1.0f, -1.5f)
        };
//        int[] indices = Jorg.read("indices");
//        Shader shader = Jorg.withRecipe(Shader::form).read("shader");
//
//        int vbo = glGenBuffers();
////        int ebo = glGenBuffers();
//        int vao = glGenVertexArrays();
//
//        glBindVertexArray(vao);
//
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
////        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
////        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
//        glEnableVertexAttribArray(0);
//        // texture coord attribute
//        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
//        glEnableVertexAttribArray(1);
//
//
//        Texture box = Texture.form(Jorg.parse("[file] container.jpg [flip]"));
//        Texture face = Texture.form(Jorg.parse("[file] awesomeface.png [flip] [format] rgba [textureWrap] mirroredRepeat"));
//
//        shader.setTexture("texture1", 0, box);
//        shader.setTexture("texture2", 1, face);

        Var<String> str = new Var<>(Var.INITIAL_DETECTION, Suite.
                set(keyboard.getKey(GLFW_KEY_UP).getPressed()).
                set(keyboard.getKey(GLFW_KEY_LEFT).getPressed()).
                set(keyboard.getKey(GLFW_KEY_DOWN).getPressed()).
                set(keyboard.getKey(GLFW_KEY_RIGHT).getPressed()),
                s -> {
                    String string = "" +
                    (s.getAt(0).asGiven(Boolean.class) ? "^" : "_") +
                    (s.getAt(1).asGiven(Boolean.class) ? "<" : "_") +
                    (s.getAt(2).asGiven(Boolean.class) ? "v" : "_") +
                    (s.getAt(3).asGiven(Boolean.class) ? ">" : "_");
                    return Suite.set(string);
                });

        Var<String> space = Var.build("_@/\"", Suite.set(Var.CURRENT_VALUE).set(keyboard.getKey(GLFW_KEY_SPACE).getState()),
                s -> Suite.set("." + s.asString()));

        Text text1 = Text.form(Suite.set("x", 30).set("y", 50).set("size", 50f).set("text", "text").set("r", 200).set("b", 200));

        setOn(Suite.set(keyboard.getCharEvent()).set(text1.getContent().far()), s -> {
            KeyboardController.CharEvent e = s.asExpected();
            Var<String> content = s.recent().asExpected();
            content.set(new StringBuilder(content.get()).appendCodePoint(e.getCodepoint()).toString());
        });

        Trigger t1 = setOn(Suite.set(text1.getContent().far()).set(keyboard.getKey(GLFW_KEY_BACKSPACE)
                .getState().suppress((s1, s2) -> s2 == GLFW_RELEASE)), s -> {
            Var<String> var = s.asExpected();
            String content = var.get();
            if(content.length() > 0) var.set(content.substring(0, content.length() - 1));
        });

        setOn(Suite.set(t1).set(Trigger.SELF).set(keyboard.getKey(GLFW_KEY_ENTER).getState()), s -> {
            cancel(s.direct());
            cancel(s.get(Trigger.SELF).direct());
        });

        while(!glfwWindowShouldClose(window))
        {
            float currentFrame = (float)glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            processInput(window);

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // render container
//            shader.use();
//
//            shader.set("view", camera.getView());
//            shader.set("projection", new Matrix4f().perspective(camera.getZoom(), 800.0f/600.0f, 0.1f, 100.0f));
//
//            glBindVertexArray(vao);
//            for (int i = 0; i < 10; i++)
//            {
//                float angle = 20.0f * i;
//                shader.set("model", new Matrix4f().translate(cubePositions[i]).rotate((float)Math.toRadians(angle), 1.0f, 0.3f, 0.5f));
//                glDrawArrays(GL_TRIANGLES, 0, 36);
//            }

            text.render(str.get(), 30f, 300f, 1., new Vector3f(0.5f, 0.8f, 0.2f));
            text.render(space.get(), 30f, 400f, 1., new Vector3f(0.5f, 0.8f, 0.2f));
            text1.render();

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window);
            glfwPollEvents();

//            glfwTerminate();
//            return;
        }

        glfwTerminate();
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

    static void mouse_callback(long window, double x, double y)
    {
        if (firstMouse)
        {
            lastX = (float)x;
            lastY = (float)y;
            firstMouse = false;
        }

        double xoffset = x - lastX;
        double yoffset = lastY - y;
        lastX = (float)x;
        lastY = (float)y;

        camera.processMouseMovement((float)xoffset, (float)yoffset, false);

    }

    static void scroll_callback(long window, double xoffset, double yoffset)
    {
        camera.processMouseScroll((float)yoffset);
    }
}