package app.model.input;

import app.model.variable.SimpleVar;
import app.model.variable.Var;
import org.joml.Vector2d;
import suite.suite.Subject;
import suite.suite.Suite;

public class Mouse {

    public static class Button {
        Var<ButtonEvent> state = SimpleVar.emit();

        public Var<ButtonEvent> getState() {
            return state;
        }
    }

    public static class ButtonEvent {
        int action;
        Vector2d position;
        int modifiers;

        public ButtonEvent(int action, Vector2d position, int modifiers) {
            this.action = action;
            this.position = position;
            this.modifiers = modifiers;
        }

        public int getAction() {
            return action;
        }

        public Vector2d getPosition() {
            return position;
        }

        public int getModifiers() {
            return modifiers;
        }
    }

    public static class Scroll {
        Var<Double> x = SimpleVar.emit(0.0);
        Var<Double> y = SimpleVar.emit(0.0);

        public Var<Double> getX() {
            return x;
        }

        public Var<Double> getY() {
            return y;
        }
    }

    Subject $buttons = Suite.thready();
    Var<Vector2d> position = SimpleVar.emit();
    Scroll scroll = new Scroll();

    public void reportPositionEvent(long window, double posX, double posY) {
        position.set(new Vector2d(posX, posY));
    }

    public void reportScrollEvent(long window, double offsetX, double offsetY) {
        if(offsetX != 0.0)scroll.x.set(offsetX);
        if(offsetY != 0.0)scroll.y.set(offsetY);
    }

    public void reportMouseButtonEvent(long window, int button, int action, int modifiers) {
        getButton(button).state.set(new ButtonEvent(action, position.get(), modifiers));
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

    public Var<Vector2d> getPosition() {
        return position;
    }
}
