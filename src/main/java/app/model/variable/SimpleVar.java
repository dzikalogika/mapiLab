package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Series;

import java.lang.ref.WeakReference;
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
        Fun.assign(that, v);
        return v;
    }

    public static<V> SimpleVar<V> compound(V value, Series $components, Action recipe, Object resultKey) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        Fun.compose(ValueProducer.prepareComponents($components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static<V> SimpleVar<V> compound(Series $components, Action recipe, Object resultKey) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        Fun.compose(ValueProducer.prepareComponents($components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }


    public static<V> SimpleVar<V> compound(V value, Series $components, Function<Subject, V> recipe) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        Fun.compose(ValueProducer.prepareComponents($components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        return composite;
    }

    public static<V> SimpleVar<V> compound(Series $components, Function<Subject, V> recipe) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        Fun.compose(ValueProducer.prepareComponents($components, composite), Suite.set(OWN_VALUE, composite),
                $ -> Suite.set(OWN_VALUE, recipe.apply($))).press(true);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(Series $components, Exp expression) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.express(ValueProducer.prepareComponents($components, composite),
                Suite.put(composite), expression).press(true);
        return composite;
    }

    private static<V> SimpleVar<V> expressed(Series $components, String expression) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.express(ValueProducer.prepareComponents($components, composite),
                Suite.put(composite), expression).press(true);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(V value, Series $components, Action recipe) {
        SimpleVar<V> composite = new SimpleVar<>(value, false);
        BeltFun.compose(ValueProducer.prepareComponents($components, composite), Suite.put(composite), recipe);
        return composite;
    }

    public static<V> SimpleVar<V> expressed(Series $components, Action recipe) {
        SimpleVar<V> composite = new SimpleVar<>(null, false);
        BeltFun.compose(ValueProducer.prepareComponents($components, composite), Suite.put(composite), recipe).press(true);
        return composite;
    }

    T value;

    public SimpleVar(T value, boolean instant) {
        super(instant);
        this.value = value;
    }

    @Override
    T value() {
        return value;
    }

    @Override
    public T get() {
        inspect();
        return value;
    }

    public T get(Fun fun) {
        return get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object value) {
        this.value = (T)value;
        for(var $ : $outputs) {
            WeakReference<Fun> ref = $.asExpected();
            Fun fun = ref.get();
            if(fun != null) {
                fun.press(true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object value, Fun fun) {
        this.value = (T)value;
        if($detections != null) $detections.unset(fun); // Jeśli wywołana w gałęzi równoległej, oznacz jako wykonana.
        for(var $ : $outputs) {
            WeakReference<Fun> ref = $.asExpected();
            Fun f = ref.get();
            if(f != null && f != fun) {
                f.press(true);
            }
        }
    }
}
