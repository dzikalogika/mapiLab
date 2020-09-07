package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;

import java.util.function.Function;

public final class SimpleVar<T> extends Var<T> {

    public static<V> SimpleVar<V> emit() {
        return new SimpleVar<>(null, false);
    }

    public static<V> SimpleVar<V> emit(V value) {
        return new SimpleVar<>(value, false);
    }

    public static<V> SimpleVar<V> emit(V value, boolean instant) {
        return new SimpleVar<>(value, instant);
    }

    public static<V> SimpleVar<V> assigned(Var<V> that) {
        SimpleVar<V> v = new SimpleVar<>(null, false);
        Fun.assign(that, v).press(true);
        return v;
    }

    public static<V> SimpleVar<V> compound(V value, Fluid components, Action recipe, Object resultKey) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static<V> SimpleVar<V> compound(Fluid components, Action recipe, Object resultKey) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }


    public static<V> SimpleVar<V> compound(V value, Fluid components, Function<Subject, V> recipe) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        return composite;
    }

    public static<V> SimpleVar<V> compound(Fluid components, Function<Subject, V> recipe) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s))).press(true);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(Fluid components, Exp expression) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.express(ValueProducer.prepareComponents(components, composite),
                Suite.add(composite), expression).press(true);
        return composite;
    }

    private static<V> SimpleVar<V> expressed(Fluid components, String expression) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.express(ValueProducer.prepareComponents(components, composite),
                Suite.add(composite), expression).press(true);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(V value, Fluid components, Action recipe) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        BeltFun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(composite), recipe);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(Fluid components, Action recipe) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(composite), recipe).press(true);
        return composite;
    }

    public SimpleVar(T value, boolean instant) {
        super(value, instant);
    }
}
