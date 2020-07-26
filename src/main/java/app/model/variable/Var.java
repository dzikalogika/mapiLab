package app.model.variable;

import jorg.processor.ProcessorException;
import suite.suite.Query;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.lang.ref.WeakReference;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Var<T> extends AbstractVar<T> {
    /**
     * Leniwa implementacja (instant = false) czeka z wywołaniem funkcji do czasu użycia get().
     * Stan zmiennych położonych wyżej jest zawsze brany ostatni przed użyciem get(), a funkcje wywoływane tylko raz.
     *
     * Jeśli każdy stan zmiennych wyższych ma zostać zarejestrowany, należy użyć implementacji instant = true.
     */

    public static class Const {}

    public static final Const OWN_VALUE = new Const();
    public static final Const SELF = new Const();

    public static<V> Var<V> create() {
        return new Var<>(null, false);
    }

    public static<V> Var<V> create(V value) {
        return new Var<>(value, false);
    }

    public static<V> Var<V> create(V value, boolean instant) {
        return new Var<>(value, instant);
    }

    public static<V> Var<V> compose(V value, Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(value, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(null, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }


    public static<V> Var<V> compose(V value, Subject components, Function<Subject, V> recipe) {
        Var<V> composite = new Var<>(value, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(OWN_VALUE, composite), s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Function<Subject, V> recipe) {
        Var<V> composite = new Var<>(null, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s))).press(true);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, String expression) {
        Var<V> composite = new Var<>(null, false);
        try {
            Fun.express(prepareComponents(components, composite), Suite.set("$0", composite), "$0=" + expression).press(true);
        } catch (ProcessorException e) {
            throw new RuntimeException(e);
        }
        return composite;
    }

    static Subject prepareComponents(Subject components, Var<?> self) {
        return components.front().advance(s -> {
            if(s.direct() == OWN_VALUE)
                return Suite.set(s.key().direct(), self.weak());
            else if(s.direct() == SELF)
                return Suite.set(s.key().direct(), new Constant<>(self));
            else return s;
        }).toSubject();
    }

    public static Query ofDoubleFrom(Subject s, Object key) {
        return Suite.from(s).get(key, Var.class).or(key, Number.class, n -> new Var<>(n.doubleValue(), true));
    }

    public static Query ofFloatFrom(Subject s, Object key) {
        return Suite.from(s).get(key, Var.class).or(key, Number.class, n -> new Var<>(n.floatValue(), true));
    }

    public static <V> Query ofObjectFrom(Subject s, Object key, Class<V> type) {
        return Suite.from(s).get(key, Var.class).or(key, type, o -> new Var<>(o, true));
    }

    public static void main(String[] args) {
        try {
            Exp exp = Exp.compile("b = 30, c = f o o(a, b)");
            System.out.println(exp.play(Suite.set("a", 4).set("b", 5).set("foo", (Action)Exp::sum)));
            System.out.println(exp.play(Suite.set("a", 20).set("b", -20).set("foo", (Action)Exp::min)));

        } catch (ProcessorException e) {
            e.printStackTrace();
        }


//        Var<Integer> a = Var.create(1, false);
//        Var<Integer> b = a.suppressEquality();
//        Var<Integer> c = Var.create(5, true);
//        Fun assign = Fun.assign(b, c);
//        System.out.println(c.get());
//        System.out.println("before set 2");
//        a.set(2);
//        System.out.println("after set 2");
//        System.out.println(c.get());
//        System.out.println("before set 2");
//        c.set(3);
//        a.set(2);
//        System.out.println("after set 2");
//        System.out.println(c.get());
//        a.set(5);
//        System.out.println(c.get());
//        assign.detach();
//        a.set(8);
//        System.out.println(c.get());
//        Fun.assign(b, c);
//        a.set(11);
//        System.out.println(c.get());
//        c.detach();
//        a.set(1);
//        System.out.println(c.get());
//        Var<Integer> d = Var.create();
//        Fun.compose(Suite.set(b), Suite.set(0, c).set(1, d), s -> {
//            System.out.println("run fun");
//            int val = s.asInt();
//            return Suite.set(0, val + 1).set(1, val + 2);
//        }).press(true);
//        System.out.println(c.get());
//        System.out.println(d.get());
//
//        Var<Integer> e = Var.compose(Suite.set("c", c).set("d", d), "(c + d) * e; e = 30");
//        System.out.println(e.get());
//        a.set(2);
//        System.out.println(e.get());
    }

    T value;
    public Subject inputs = Suite.set();
    Subject outputs = Suite.set();
    Subject detections;

    public Var(T value, boolean instant) {
        this.value = value;
        if(!instant)detections = Suite.set();
    }

    public T get() {
        if(detections != null) {
            if (detections.settled()) {
                Subject d = detections;
                detections = Suite.set();
                d.front().values().filter(Fun.class).forEach(Fun::execute);
            }
        }
        return value;
    }

    T get(Fun fun) {
        return get();
    }

    public void set(T value) {
        this.value = value;
        for(var s : outputs.front()) {
            WeakReference<Fun> ref = s.asExpected();
            Fun fun = ref.get();
            if(fun != null) {
                fun.press(true);
            }
        }
    }

    void set(T value, Fun fun) {
        this.value = value;
        if(detections != null)detections.unset(fun); // Jeśli wywołana w gałęzi równoległej, oznacz jako wykonana.
        for(var s : outputs.front()) {
            WeakReference<Fun> ref = s.asExpected();
            Fun f = ref.get();
            if(f != null &&f != fun) {
                f.press(true);
            }
        }
    }

    boolean press(Fun fun) {
        if(detections == null) {
            fun.execute();
            return true;
        } else {
            boolean pressOutputs = detections.desolated();
            detections.put(fun);
            if(pressOutputs) {
                for(var s : outputs.front()) {
                    WeakReference<Fun> ref = s.asExpected();
                    Fun f = ref.get();
                    if(f != null && f != fun && f.press(false)) return true;
                }
            }
            return false;
        }
    }

    public void attachOutput(Fun fun) {
        outputs.put(new WeakReference<>(fun));
    }

    public void detachOutput(Fun fun) {
        for(var s : outputs.front()) {
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

    public void detachInput(Fun fun) {
        inputs.unset(fun);
        fun.detachOutputVar(this);
    }

    public void detachInputs() {
        inputs.front().keys().filter(Fun.class).forEach(this::detachInput);

    }

    public void detachOutputs() {
        outputs = Suite.set();
    }

    public void detach() {
        detachOutputs();
        detachInputs();
    }

    boolean cycleTest(Fun fun) {
        for(var s : outputs.front()) {
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

    public Var<T> suppress(BiPredicate<T, T> suppressor) {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppress(this, suppressed, suppressor);
        return suppressed;
    }

    public Var<T> suppressIdentity() {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppressIdentity(this, suppressed);
        return suppressed;
    }

    public Var<T> suppressEquality() {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppressEquality(this, suppressed);
        return suppressed;
    }

    public<V extends T> Var<T> assign(Var<V> var) {
        Fun.assign(var, this);
        return this;
    }

    public WeakVar<T> weak() {
        return new WeakVar<>(this);
    }

}
