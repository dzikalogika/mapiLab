package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.function.BiPredicate;

public class Var<T> {

    public static final Object OWN_VALUE = new Object();

    static void utilizeCollected(Subject collector) {
        for(var s : collector.front()) {
            if(s.assigned(Var.class)) {
                Var<?> var = s.asExpected();
                var.utilize(collector);
            } else if(s.assigned(Fun.class)) {
                Fun fun = s.asExpected();
                fun.utilize(collector);
            }
        }
    }

    public static<V> Var<V> create() {
        return new Var<>(null, false, false);
    }

    public static<V> Var<V> create(V value) {
        return new Var<>(value, false, false);
    }

    public static<V> Var<V> create(V value, boolean instant) {
        return new Var<>(value, instant, false);
    }

    public static<V> Var<V> compose(boolean pressed, Subject components) {
        Var<V> composite = new Var<>(null, false, false);
        Fun fun = Fun.create(prepareComponents(components, composite), Suite.set(composite), s -> Suite.set());
        if(pressed)fun.press(true);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(null, false, false);
        Fun.create(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Action recipe) {
        return compose(components, s -> {
            var result = recipe.play(s);
            return result.settled() ? Suite.set(0, result.direct()) : Suite.set();
        }, 0);
    }

    public static<V> Var<V> compose(V value, Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(value, false, false);
        Fun.create(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static<V> Var<V> compose(V value, Subject components, Action recipe) {
        return compose(value, components, s -> {
            var result = recipe.play(s);
            return result.settled() ? Suite.set(0, result.direct()) : Suite.set();
        }, 0);
    }

    static Subject prepareComponents(Subject components, Var<?> var) {
        return components.front().advance(s -> s.direct() == OWN_VALUE ? Suite.set(s.key().direct(), var) : s).toSubject();
    }

    public static<V> V fetch(Subject s) {
        Var<V> v = s.asExpected();
        return v.get();
    }

    public static void main(String[] args) {
        Var<Integer> a = Var.create(1, false);
        Var<Integer> b = a.suppressEquality();
        Var<Integer> c = Var.create(5, true);
        Fun assign = Fun.assign(b, c);
        System.out.println(c.get());
        System.out.println("before set 2");
        a.set(2);
        System.out.println("after set 2");
        System.out.println(c.get());
        System.out.println("before set 2");
        c.set(3);
        a.set(2);
        System.out.println("after set 2");
        System.out.println(c.get());
        a.set(5);
        System.out.println(c.get());
        c.detach();
        System.out.println();
    }

    class NoVar {
        Var<T> var;

        public NoVar(Var<T> var) {
            this.var = var;
        }
    }

    T value;
    Subject inputs = Suite.set();
    Subject outputs = Suite.set();
    Subject detections;
    boolean Transient;

    public Var(T value, boolean isInstant, boolean isTransient) {
        this.value = value;
        if(!isInstant)detections = Suite.set();
        this.Transient = isTransient;
    }

    public T get() {
        if(detections != null) {
            if (detections.settled()) {
                Subject d = detections;
                detections = Suite.set();
                d.front().keys().filter(Fun.class).forEach(Fun::evaluate);
            }
        }
        return value;
    }

    T get(Fun fun) {
        if(detections != null)detections.unset(fun);
        return get();
    }

    public void set(T value) {
        this.value = value;
        outputs.front().keys().filter(Fun.class).forEach(f -> f.press(true));
    }

    void set(T value, Fun fun) {
        if(detections != null)detections.unset(fun);
        this.value = value;
        outputs.front().keys().filter(Fun.class).filter(f -> f != fun).forEach(f -> f.press(true));
    }

    public void attachOutput(Fun fun) {
        outputs.set(fun);
    }

    public void detachOutput(Fun fun) {
        outputs.unset(fun);
        fun.detachInput(this);
    }

    public void attachInput(Fun fun) {
        inputs.set(fun);
    }

    public void detachInput(Fun fun) {
        inputs.unset(fun);
        fun.detachOutput(this);
    }

    public void detachInputs() {
        for(Fun fun : inputs.front().keys().filter(Fun.class)) {
            detachInput(fun);
            if(inputs == null)return;
        }
    }

    public void detachOutputs() {
        for(Fun fun : outputs.front().keys().filter(Fun.class)) {
            detachOutput(fun);
            if(outputs == null)return;
        }
    }

    public void detach() {
        detachOutputs();
        if(!isTransient())detachInputs();
    }

    boolean press(Fun fun) {
        if(detections == null) {
            fun.evaluate();
            return true;
        } else {
            boolean trigger = detections.desolated();
            detections.set(fun);
            if(trigger) {
                for (Fun f : outputs.front().keys().filter(Fun.class).filter(f -> f != fun)) {
                    if (f.press(false)) return true;
                }
            }
            return false;
        }
    }

    public boolean release() {
        if(detections == null)return false;
        if(detections.settled()) {
            detections.unset();
            return true;
        } else return false;
    }

    boolean collectTransient(Subject collector) {
        if(collector.get(this).settled())return true;
        boolean collect = isTransient() && outputs.front().keys().filter(Fun.class).allTrue(f -> f.collectTransient(collector));
        if(collect) {
            collector.set(this);
            inputs.front().keys().filter(Fun.class).forEach(f -> f.collectTransient(collector));
        }
        return collect;
    }

    public boolean isTransient() {
        return Transient;
    }

    public boolean isInstant() {
        return detections == null;
    }

    void silentDetachInput(Fun fun) {
        inputs.unset(fun);
    }

    void silentDetachOutput(Fun fun) {
        outputs.unset(fun);
    }

    void utilize(Subject collector) {
        // Dla outputów nie ma sensu ciche odpięcie, bo wszystkie takie funkcje i tak powinny zostać zutylizowane
        for(Fun f : inputs.front().values().filter(Fun.class).filter(f -> collector.get(f).desolated())) {
            f.silentDetachOutput(this);
        }
        inputs = null;
        outputs = null;
        detections = null;
    }

    public Var<T> suppress(BiPredicate<T, T> suppressor) {
        Var<T> suppressed = new Var<>(value, true, true);
        Fun.suppress(this, suppressed, suppressor);
        return suppressed;
    }

    public Var<T> suppressIdentity() {
        Var<T> suppressed = new Var<>(value, true, true);
        Fun.suppressIdentity(this, suppressed);
        return suppressed;
    }

    public Var<T> suppressEquality() {
        Var<T> suppressed = new Var<>(value, true, true);
        Fun.suppressEquality(this, suppressed);
        return suppressed;
    }

    public<V extends T> Var<T> assign(Var<V> var) {
        Fun.assign(var, this);
        return this;
    }

    public Var<Var<T>> self() {
        return new Var<>(this, true, true);
    }

}
