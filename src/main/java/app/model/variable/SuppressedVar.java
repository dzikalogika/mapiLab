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

    public SuppressedVar(T value, BiPredicate<T, T> suppressor) {
        super(value);
        this.suppressor = suppressor;
    }

    public SuppressedVar(Subject params, Action recipe, BiPredicate<T, T> suppressor) {
        super(params, recipe);
        this.suppressor = suppressor;
    }

    public SuppressedVar(int flags, Subject params, Action recipe, BiPredicate<T, T> suppressor) {
        super(flags, params, recipe);
        this.suppressor = suppressor;
    }

    public void set(T newValue) {
        T previousValue = value;
        value = newValue;
        if(!suppressor.test(previousValue, newValue)) {
            subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        }
    }
}
