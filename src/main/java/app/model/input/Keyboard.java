package app.model.input;

import org.lwjgl.glfw.GLFW;
import suite.suite.Subject;
import suite.suite.Suite;
import vars.vars.Var;
import vars.vars.Vars;

import static suite.suite.$uite.$;
import static suite.suite.$uite.$$;

public class Keyboard {

    public static class Key {
        Var<Integer> state = Vars.set(GLFW.GLFW_RELEASE);
        Var<Boolean> pressed = Vars.set(false).act(false, $$(state, Var.OWN_VALUE), $ -> {
            int state = $.in(0).asInt();
            boolean pressedSoFar = $.in(1).asExpected();
            if(pressedSoFar) {
                return state == GLFW.GLFW_RELEASE ? $(false) : $();
            } else {
                return state == GLFW.GLFW_RELEASE ? $() : $(true);
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

    private final Subject $keys = Suite.thready();
    private final Var<KeyEvent> keyEvent = Vars.set();
    private final Var<CharEvent> charEvent = Vars.set();

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
        return getKeyByScanCode(scanCode);
    }

    public Key getKeyByScanCode(int scanCode) {
        var $ = $keys.in(scanCode).set();
        if($.absent()) {
            $.set(new Key());
        }
        return $.asExpected();
    }
}
