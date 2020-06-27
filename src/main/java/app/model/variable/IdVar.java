package app.model.variable;

import suite.suite.Subject;
import suite.suite.action.Action;

public class IdVar<T> extends Var<T> {

    public IdVar() {
    }

    public IdVar(T value) {
        super(value);
    }

    public IdVar(Subject params, Action recipe) {
        super(params, recipe);
    }

    public IdVar(int flags, Subject params, Action recipe) {
        super(flags, params, recipe);
    }

    @Override
    public void set(T newValue) {
        if(value != newValue) {
            super.set(newValue);
            subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        }
    }
}
