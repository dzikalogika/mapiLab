package app.model;

import app.model.variable.Var;
import org.joml.Vector2d;
import suite.suite.Subject;
import suite.suite.Suite;

public class MouseController {

    public static class Button {

    }

    Subject buttons = Suite.thready();
    Var<Vector2d> position = Var.create();
    Var<Vector2d> scroll = Var.create();

    public void reportPositionEvent(long window, double posX, double posY) {
        position.set(new Vector2d(posX, posY));
    }

    public void reportScrollEvent(long window, double offsetX, double offsetY) {
        scroll.set(new Vector2d(offsetX, offsetY));
    }

    public void reportMouseButtonEvent(long window, int button, int action, int modifiers) {

    }
}
