package app;

import app.model.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static suite.suite.$uite.$;
import static suite.suite.Suite.join;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(join(
                $(Window.class, Main.class),
                $("r", .2f),
                $("g", .5f),
                $("b", .4f)
        ));
    }

    Rectangle r1;
    ColorPolygonOutfit outfit;

    public void setup() {
        r1 = new Rectangle();
        r1.setWidth(200);
        r1.setHeight(100);
//        decorator.paint(r1, Color.PURE_GREEN);
//        coordinator.distribute(r1, new Point(300, 300));

        outfit = new ColorPolygonOutfit();
        outfit.setColor(Color.PURE_GREEN);
        outfit.setIndices(new int[]{0, 2, 1, 0, 3, 2});
        outfit.setVertex(new Point[]{
                new Point(.5f, .5f),
                new Point(.5f, -.5f),
                new Point(-.5f, -.5f),
                new Point(-.5f, .5f),
        });
    }

    @Override
    public void update() {
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        outfit.print();
        processInput(getGlid());
    }

    static void processInput(long window) {
        if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
    }
}