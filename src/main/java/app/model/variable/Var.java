package app.model.variable;

import suite.suite.Query;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class Var<T> implements ValueProducer<T>, ValueConsumer<T> {
    /**
     * Leniwa implementacja (instant = false) czeka z wywołaniem funkcji do czasu użycia get().
     * Stan zmiennych położonych wyżej jest zawsze brany ostatni przed użyciem get(), a funkcje wywoływane tylko raz.
     *
     * Jeśli każdy stan zmiennych wyższych ma zostać zarejestrowany, należy użyć implementacji instant = true.
     */

    public static<V> Var<V> create() {
        return new Var<>(null, false);
    }

    public static<V> Var<V> create(V value) {
        return new Var<>(value, false);
    }

    public static<V> Var<V> create(V value, boolean instant) {
        return new Var<>(value, instant);
    }

    public static<V> Var<V> assigned(Var<V> that) {
        Var<V> v = new Var<>(null, false);
        Fun.assign(that, v).press(true);
        return v;
    }

    public static<V> Var<V> compose(V value, Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Action recipe, Object resultKey) {
        Var<V> composite = new Var<>(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }


    public static<V> Var<V> compose(V value, Subject components, Function<Subject, V> recipe) {
        Var<V> composite = new Var<>(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite), s -> Suite.set(OWN_VALUE, recipe.apply(s)));
        return composite;
    }

    public static<V> Var<V> compose(Subject components, Function<Subject, V> recipe) {
        Var<V> composite = new Var<>(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s))).press(true);
        return composite;
    }

    public static<V> Var<V> compose(Subject components, String expression) {
        Var<V> composite = new Var<>(null, false);
        BeltFun.express(ValueProducer.prepareComponents(components, composite), Suite.add(composite), expression).press(true);
        return composite;
    }

    public static Query doubleFrom(Subject s, Object key) {
        return Suite.from(s).get(key, Var.class).or(key, Number.class, n -> new Var<>(n.floatValue(), false));
    }

    public static Query floatFrom(Subject s, Object key) {
        return Suite.from(s).get(key, Var.class).or(key, Number.class, n -> new Var<>(n.floatValue(), false));
    }

    public static <V> Query from(Subject s, Object key, Class<V> type) {
        return Suite.from(s).get(key, Var.class).or(key, type, v -> new Var<>(v, false));
    }

    public static void main(String[] args) {
        Var<Double> width = Var.create(800.0);
        Var<Double> ys = Var.create(0.0);
        Var<Double> x = Var.compose(Suite.set("w", width).set("s", ys).set("fun", (Action) s -> {
            return s;
        }), "fun((400 + s) * 2 / w - 1)");
        ys.set(2.0);
//        try {
//            Exp exp = Exp.compile("c = f o o(a, -+b%); b = 30; a = 50");
//            System.out.println(exp.play(Suite.set("a", 4).set("b", 5).set("foo", (Action)Exp::sum)));
//            System.out.println(exp.play(Suite.set("a", 20).set("b", -20).set("foo", (Action)Exp::min)));
//
//        } catch (ProcessorException e) {
//            e.printStackTrace();
//        }


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

    public void detachInput(Fun fun) {
        inputs.unset(fun);
        fun.detachOutputVar(this);
    }

    public void detachInputs() {
        inputs.keys().filter(Fun.class).forEach(this::detachInput);

    }

    public void detachOutputs() {
        outputs = Suite.set();
    }

    public void detach() {
        detachOutputs();
        detachInputs();
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
        Fun.suppressIdentity(this, suppressed);
        return suppressed;
    }

    public Var<T> suppressEquality() {
        Var<T> suppressed = new Var<>(value, true);
        Fun.suppressEquality(this, suppressed);
        return suppressed;
    }

    public<V extends T> Var<T> assign(ValueProducer<V> vp) {
        Fun.assign(vp, this).press(true);
        return this;
    }

    public<V extends T> Var<T> assign(Subject sub) {
        if(sub.settled()) {
            Fun fun = new Fun(sub, Suite.set(Var.OWN_VALUE, this), s -> Suite.set(Var.OWN_VALUE, s.direct()));
            fun.reduce(true);
        }
        return this;
    }

    public Var<T> express(Subject components, String expression) {
        Fun.express(ValueProducer.prepareComponents(components, this), Suite.set("", this), expression).press(true);
        return this;
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

    public Subject getInputs() {
        return inputs;
    }
}
