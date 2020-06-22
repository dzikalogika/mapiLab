package app.model;

import app.model.variable.IdVar;
import app.model.variable.Var;
import org.lwjgl.glfw.GLFW;
import suite.suite.Subject;
import suite.suite.Suite;

import java.util.LinkedList;
import java.util.Queue;

public class KeyboardController {

    public static class Key {
        Var<Integer> state = new Var<>(GLFW.GLFW_RELEASE);
        Var<Boolean> pressed = new IdVar<>(true, Suite.set(state),
                s -> Suite.set(s.asGiven(Integer.class) != GLFW.GLFW_RELEASE));

        public Var<Integer> getState() {
            return state;
        }

        public Var<Boolean> getPressed() {
            return pressed;
        }
    }

    static class KeyEvent {
        int scanCode;
        int eventType;
        int modifiers;

        public KeyEvent(int scanCode, int eventType, int modifiers) {
            this.scanCode = scanCode;
            this.eventType = eventType;
            this.modifiers = modifiers;
        }
    }

    private final Subject keys = Suite.thready();
    private final Queue<KeyEvent> recorder = new LinkedList<>();

    public void reportKeyEvent(long window, int keyCode, int scanCode, int eventType, int modifiers) {
        recorder.add(new KeyEvent(scanCode, eventType, modifiers));
        getKeyByScanCode(scanCode).state.set(eventType);
    }

    public Queue<KeyEvent> getRecorder() {
        return recorder;
    }

    public boolean pressed(int keyCode) {
        return getKey(keyCode).pressed.get();
    }

    public Key getKey(int keyCode) {
        int scanCode = GLFW.glfwGetKeyScancode(keyCode);
        return keys.getDone(scanCode, Key::new).asGiven(Key.class);
    }

    public Key getKeyByScanCode(int scanCode) {
        return keys.getDone(scanCode, Key::new).asGiven(Key.class);
    }
}
