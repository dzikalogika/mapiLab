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

    public IdVar(boolean forceFirstDetection, Subject params, Action recipe) {
        super(forceFirstDetection, params, recipe);
    }

    @Override
    public void set(T newValue) {
        if(value != newValue) {
            super.set(newValue);
        }
    }
}
