package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;

import java.lang.ref.WeakReference;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Var<T> implements ValueProducer<T>, ValueConsumer<T> {
    /**
     * Leniwa implementacja (instant = false) czeka z wywołaniem funkcji do czasu użycia get().
     * Stan zmiennych położonych wyżej jest zawsze brany ostatni przed użyciem get(), a funkcje wywoływane tylko raz.
     *
     * Jeśli każdy stan zmiennych wyższych ma zostać zarejestrowany, należy użyć implementacji instant = true.
     */

    T value;
    Subject inputs = Suite.set();
    Subject outputs = Suite.set();
    Subject detections;

    public Var(T value, boolean instant) {
        this.value = value;
        if(!instant)detections = Suite.set();
    }

    public T get() {
        if(detections != null && detections.settled()) {
            detections.values().filter(Fun.class).forEach(Fun::execute);
            detections = Suite.set();
        }
        return value;
    }

    public T get(Fun fun) {
        return get();
    }

    public void set(T value) {
        this.value = value;
        for(var s : outputs) {
            WeakReference<Fun> ref = s.asExpected();
            Fun fun = ref.get();
            if(fun != null) {
                fun.press(true);
            }
        }
    }

    public void set(T value, Fun fun) {
        this.value = value;
        if(detections != null)detections.unset(fun); // Jeśli wywołana w gałęzi równoległej, oznacz jako wykonana.
        for(var s : outputs) {
            WeakReference<Fun> ref = s.asExpected();
            Fun f = ref.get();
            if(f != null && f != fun) {
                f.press(true);
            }
        }
    }

    public boolean press(Fun fun) {
        if(detections == null) {
            fun.execute();
            return true;
        } else {
            boolean pressOutputs = detections.desolated();
            detections.put(fun);
            if(pressOutputs) {
                for(var s : outputs) {
                    WeakReference<Fun> ref = s.asExpected();
                    Fun f = ref.get();
                    if(f != null && f != fun && f.press(false)) return true;
                }
            }
            return false;
        }
    }

    public boolean attachOutput(Fun fun) {
        outputs.put(new WeakReference<>(fun));
        return detections != null && detections.settled();
    }

    public void detachOutput(Fun fun) {
        for(var s : outputs) {
            WeakReference<Fun> ref = s.asExpected();
            Fun f = ref.get();
            if(f == null || f == fun) {
                outputs.unset(ref);
            }
        }
    }

    public void attachInput(Fun fun) {
        inputs.set(fun);
    }

    public void detach() {
        outputs = Suite.set();
    }

    boolean cycleTest(Fun fun) {
        for(var s : outputs) {
            WeakReference<Fun> ref = s.asExpected();
            Fun f = ref.get();
            if(f == null){
                outputs.unset(s.key().direct());
            } else if(f == fun || f.cycleTest(fun)) return true;
        }
        return false;
    }

    public boolean isInstant() {
        return detections == null;
    }

    public Var<T> select(BiPredicate<T, T> selector) {
        return suppress(selector.negate());
    }

    public Var<T> suppress(BiPredicate<T, T> suppressor) {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppress(this, suppressed, suppressor);
        return suppressed;
    }

    public Var<T> suppressIdentity() {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppressIdentity(this, suppressed).attach();
        return suppressed;
    }

    public Var<T> suppressEquality() {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppressEquality(this, suppressed).attach();
        return suppressed;
    }

    public<V extends T> Fun assign(ValueProducer<V> vp) {
        Fun fun = Fun.assign(vp, this);
        fun.attach(true);
        return fun;
    }

    public Subject assign(Subject sub) {
        if(sub.settled()) {
            Fun fun = new Fun(sub, Suite.set(Var.OWN_VALUE, this), s -> Suite.set(Var.OWN_VALUE, s.direct()));
            fun.attach();
            return Suite.set(fun);
        }
        return Suite.set();
    }

    public Fun compose(Fluid components, Action recipe, Object resultKey) {
        Fun fun = Fun.compose(ValueProducer.prepareComponents(components, this), Suite.set(resultKey, this), recipe);
        fun.attach();
        return fun;
    }

    public Fun compose(Fluid components, Function<Subject, T> recipe) {
        Fun fun = Fun.compose(ValueProducer.prepareComponents(components, this), Suite.set(OWN_VALUE, this),
                s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        fun.attach();
        return fun;
    }

    public BeltFun express(Fluid components, Exp expression) {
        BeltFun fun = BeltFun.express(ValueProducer.prepareComponents(components, this),
                Suite.add(this), expression);
        fun.attach();
        return fun;
    }

    private BeltFun express(Fluid components, String expression) {
        BeltFun fun = BeltFun.express(ValueProducer.prepareComponents(components, this),
                Suite.add(this), expression);
        fun.attach();
        return fun;
    }

    public BeltFun express(Fluid components, Action recipe) {
        BeltFun fun = BeltFun.compose(ValueProducer.prepareComponents(components, this), Suite.set(this), recipe);
        fun.attach();
        return fun;
    }

    public WeakVar<T> weak() {
        return new WeakVar<>(this);
    }

    @Override
    public String toString() {
        if(detections != null && detections.settled())
            return "(" + value + ")";
        else return "<" + value + ">";
    }

    public String address() {
        return super.toString();
    }

    public Subject getInputs() {
        return inputs;
    }
}
