package app.model.variable;

import suite.suite.Subject;
import suite.suite.action.Action;

import java.util.Objects;
import java.util.function.BiPredicate;

public class SuppressedVar<T> extends Var<T> {

    private BiPredicate<T, T> suppressor;
    
    public SuppressedVar(BiPredicate<T, T> suppressor) {
        this.suppressor = suppressor;
    }

    public SuppressedVar(BiPredicate<T, T> suppressor, T value) {
        super(value);
        this.suppressor = suppressor;
    }

    public SuppressedVar(BiPredicate<T, T> suppressor, Subject params, Action recipe) {
        super(params, recipe);
        this.suppressor = suppressor;
    }

    public void set(T newValue) {
        if(!suppressor.test(value, newValue)) {
            subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        }
        value = newValue;
    }
}
