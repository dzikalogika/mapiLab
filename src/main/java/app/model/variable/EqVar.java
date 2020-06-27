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

    public EqVar(int flags, Subject params, Action recipe) {
        super(flags, params, recipe);
    }

    public void set(T newValue) {
        T previousValue = value;
        value = newValue;
        if(!Objects.equals(previousValue, newValue)) {
            subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        }
    }
}
