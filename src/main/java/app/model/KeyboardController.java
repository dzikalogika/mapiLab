package app.model;

import app.model.variable.Var;
import org.lwjgl.glfw.GLFW;
import suite.suite.Subject;
import suite.suite.Suite;

public class KeyboardController {

    public static class Key {
        Var<Integer> state = Var.create(GLFW.GLFW_RELEASE);
        Var<Boolean> pressed = Var.compose(false, Suite.set(state).set(Var.OWN_VALUE), s -> {
            int state = s.asInt();
            boolean pressedSoFar = s.recent().asExpected();
            if(pressedSoFar) {
                return state == GLFW.GLFW_RELEASE ? Suite.set(false) : Suite.set();
            } else {
                return state == GLFW.GLFW_RELEASE ? Suite.set() : Suite.set(true);
            }
        });

        public Var<Integer> getState() {
            return state;
        }

        public Var<Boolean> getPressed() {
            return pressed;
        }
    }

    public static class KeyEvent {
        int scanCode;
        int eventType;
        int modifiers;

        public KeyEvent(int scanCode, int eventType, int modifiers) {
            this.scanCode = scanCode;
            this.eventType = eventType;
            this.modifiers = modifiers;
        }

        public int getScanCode() {
            return scanCode;
        }

        public int getEventType() {
            return eventType;
        }

        public int getModifiers() {
            return modifiers;
        }
    }

    public static class CharEvent {
        int codepoint;
        int modifiers;

        public CharEvent(int codepoint, int modifiers) {
            this.codepoint = codepoint;
            this.modifiers = modifiers;
        }

        public int getCodepoint() {
            return codepoint;
        }

        public int getModifiers() {
            return modifiers;
        }
    }

    private final Subject keys = Suite.thready();
    private final Var<KeyEvent> keyEvent = Var.create();
    private final Var<CharEvent> charEvent = Var.create();

    public void reportKeyEvent(long window, int keyCode, int scanCode, int eventType, int modifiers) {
        keyEvent.set(new KeyEvent(scanCode, eventType, modifiers));
        getKeyByScanCode(scanCode).state.set(eventType);
    }

    public Var<KeyEvent> getKeyEvent() {
        return keyEvent;
    }

    public void reportCharEvent(long window, int codepoint, int modifiers) {
        charEvent.set(new CharEvent(codepoint, modifiers));
    }

    public Var<CharEvent> getCharEvent() {
        return charEvent;
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
