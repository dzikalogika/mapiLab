package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;

import java.util.function.Function;

public class Monitor extends Var<Boolean> {

    public static Monitor compose(Boolean value, Subject components, Function<Subject, Boolean> recipe) {
        Monitor composite = new Monitor(value, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(OWN_VALUE, composite), s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        return composite;
    }

    public static Monitor compose(boolean pressed, Subject components) {
        return Monitor.compose(pressed, components, s -> true);
    }

    public Monitor(Boolean value, boolean instant) {
        super(value, instant);
    }

    public boolean release() {
        boolean result = get();
        if(result) value = false;
        return result;
    }
}
