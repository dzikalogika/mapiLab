package app.model.variable;

import suite.suite.Subject;
import suite.suite.action.Action;

import java.util.Objects;

public class EqVar<T> extends Var<T> {

    public EqVar() {
    }

    public EqVar(T value) {
        super(value);
    }

    public EqVar(Subject params, Action recipe) {
        super(params, recipe);
    }

    public EqVar(boolean forceFirstDetection, Subject params, Action recipe) {
        super(forceFirstDetection, params, recipe);
    }

    public void set(T newValue) {
        if(!Objects.equals(value, newValue)) {
            subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        }
        value = newValue;
    }
}
