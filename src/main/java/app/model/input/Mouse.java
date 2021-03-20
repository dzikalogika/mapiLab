package app.model.input;

import app.model.Point;
import app.model.window.Window;
import app.model.var.Source;
import app.model.var.Var;
import suite.suite.Subject;
import suite.suite.Suite;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {

    public static class Button {
        Var<Boolean> pressed = new Var<>(false);

        public boolean isPressed() {
            return pressed.get();
        }

        public Source<Boolean> pressed() {
            return pressed;
        }
    }

    public static class Scroll {
        Var<Double> x = new Var<>();
        Var<Double> y = new Var<>();

        public Var<Double> getX() {
            return x;
        }

        public Var<Double> getY() {
            return y;
        }
    }

    Window window;
    Subject $buttons = Suite.thready();
    Var<Point> position = new Var<>();
    Scroll scroll = new Scroll();
    Button leftButton = new Button();
    Button rightButton = new Button();

    public Mouse(Window window) {
        this.window = window;
    }

    public void reportPositionEvent(long w, double posX, double posY) {
        position.set(new Point(posX, posY));
    }

    public void reportScrollEvent(long window, double offsetX, double offsetY) {
        if(offsetX != 0.0)scroll.x.set(offsetX);
        if(offsetY != 0.0)scroll.y.set(offsetY);
    }

    public void reportMouseButtonEvent(long window, int button, int action, int modifiers) {
        switch (button) {
            case GLFW_MOUSE_BUTTON_1 -> leftButton.pressed.set(action == GLFW_PRESS);
            case GLFW_MOUSE_BUTTON_2 -> rightButton.pressed.set(action == GLFW_PRESS);
        }
    }

    public Button getButton(int button) {
        var $ = $buttons.in(button).set();
        if($.absent()) {
            $.set(new Button());
        }
        return $.asExpected();
    }

    public Scroll getScroll() {
        return scroll;
    }


    public Source<Point> getPosition() {
        return position;
    }

    public Button getLeftButton() {
        return leftButton;
    }

    public Button getRightButton() {
        return rightButton;
    }
}
