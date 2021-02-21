package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Series;

import java.lang.ref.WeakReference;
import java.util.function.BiPredicate;
import java.util.function.Function;

public abstract class Var<T> implements ValueProducer<T>, ValueConsumer {
    /**
     * Leniwa implementacja (instant = false) czeka z wywołaniem funkcji do czasu użycia get().
     * Stan zmiennych położonych wyżej jest zawsze brany ostatni przed użyciem get(), a funkcje wywoływane tylko raz.
     *
     * Jeśli każdy stan zmiennych wyższych ma zostać zarejestrowany, należy użyć implementacji instant = true.
     */

    Subject $inputs = Suite.set();
    Subject $outputs = Suite.set();
    Subject $detections;

    public Var(boolean instant) {
        if(!instant) $detections = Suite.set();
    }

    void inspect() {
        if($detections != null && $detections.present()) {
            $detections.eachAs(Fun.class).forEach(Fun::execute);
            $detections = Suite.set();
        }
    }

    public abstract T get();
    public abstract void set(Object value);

    public boolean press(Fun fun) {
        if($detections == null) {
            fun.execute();
            return true;
        } else {
            boolean pressOutputs = $detections.absent();
            $detections.sate(fun);
            if(pressOutputs) {
                for(var $ : $outputs) {
                    WeakReference<Fun> ref = $.asExpected();
                    Fun f = ref.get();
                    if(f != null && f != fun && f.press(false)) return true;
                }
            }
            return false;
        }
    }

    public boolean attachOutput(Fun fun) {
        $outputs.sate(new WeakReference<>(fun));
        return $detections != null && $detections.present();
    }

    public void detachOutput(Fun fun) {
        for(var $ : $outputs) {
            WeakReference<Fun> ref = $.asExpected();
            Fun f = ref.get();
            if(f == null || f == fun) {
                $outputs.unset(ref);
            }
        }
    }

    public void attachInput(Fun fun) {
        $inputs.set(fun);
    }

    public void detach() {
        $outputs = Suite.set();
    }

    boolean cycleTest(Fun fun) {
        for(var $ : $outputs) {
            WeakReference<Fun> ref = $.asExpected();
            Fun f = ref.get();
            if(f == null){
                $outputs.unset($.direct());
            } else if(f == fun || f.cycleTest(fun)) return true;
        }
        return false;
    }

    public boolean isInstant() {
        return $detections == null;
    }

    public Var<T> select(BiPredicate<T, T> selector) {
        return suppress(selector.negate());
    }

    public Var<T> suppress(BiPredicate<T, T> suppressor) {
        Var<T> suppressed = new SimpleVar<>(get(), true);
        Fun.suppress(this, suppressed, suppressor);
        return suppressed;
    }

    public Var<T> suppressIdentity() {
        Var<T> suppressed = new SimpleVar<>(get(), true);
        Fun.suppressIdentity(this, suppressed);
        return suppressed;
    }

    public Var<T> suppressEquality() {
        Var<T> suppressed = new SimpleVar<>(get(), true);
        Fun.suppressEquality(this, suppressed);
        return suppressed;
    }

    public<V extends T> Fun assign(ValueProducer<V> vp) {
        return Fun.assign(vp, this);
    }

    public Subject assign(Subject $sub) {
        if($sub.present()) {
            Fun fun = Fun.compose($sub, Suite.set(Var.OWN_VALUE, this), $ -> Suite.set(Var.OWN_VALUE, $.in().direct()));
            fun.press(true);
            return Suite.set(fun);
        }
        return Suite.set();
    }

    public Fun compose(Series $components, Action recipe, Object resultKey) {
        return Fun.compose(ValueProducer.prepareComponents($components, this), Suite.set(resultKey, this), recipe);
    }

    public Fun compose(Series $components, Function<Subject, T> recipe) {
        return Fun.compose(ValueProducer.prepareComponents($components, this), Suite.set(OWN_VALUE, this),
                $ -> Suite.set(OWN_VALUE, recipe.apply($)));
    }

    public BeltFun express(Series $components, Exp expression) {
        return BeltFun.express(ValueProducer.prepareComponents($components, this), Suite.put(this), expression);
    }

    private BeltFun express(Series $components, String expression) {
        return BeltFun.express(ValueProducer.prepareComponents($components, this), Suite.put(this), expression);
    }

    public BeltFun express(Series $components, Action recipe) {
        return BeltFun.compose(ValueProducer.prepareComponents($components, this), Suite.put(this), recipe);
    }

    public WeakVar<T> weak() {
        return new WeakVar<>(this);
    }

    abstract T value();

    @Override
    public String toString() {
        if($detections != null && $detections.present())
            return "(" + value() + ")";
        else return "<" + value() + ">";
    }

    public String address() {
        return super.toString();
    }

    public Subject getInputs() {
        return $inputs;
    }
}
