package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiPredicate;


public class Fun {

    public static class Const {}

    public static final Const SELF = new Const();

    public static Fun compose(Fluid inputs, Fluid outputs, Action transition) {
        Fun fun = new Fun(inputs, outputs, transition);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T> Fun assign(Object source, ValueConsumer target) {
        Fun fun = new Fun(Suite.set(source), Suite.set(Var.OWN_VALUE, target), s -> Suite.set(Var.OWN_VALUE, s.direct()));
        fun.attach();
        fun.press(true);
        return fun;
    }

    public static<T, T1 extends T> Fun select(ValueProducer<T1> source, Var<T> target, BiPredicate<T1, T> selector) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> selector.test(s.recent().asExpected(), s.asExpected()) ? s : Suite.set());
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppress(ValueProducer<T1> source, Var<T> target, BiPredicate<T1, T> suppressor) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> suppressor.test(s.recent().asExpected(), s.asExpected()) ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppressIdentity(ValueProducer<T1> source, Var<T> target) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> s.direct() == s.recent().direct() ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppressEquality(ValueProducer<T1> source, Var<T> target) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> Objects.equals(s.direct(), s.recent().direct()) ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static Fun express(Fluid inputs, Fluid outputs, Exp expression) {
        Fun fun = new Fun(inputs, outputs, expression);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static Fun express(Fluid inputs, Fluid outputs, String expression) {
        return express(inputs, outputs, Exp.compile(expression));
    }

    Subject inputs;
    Subject outputs;
    Action transition;
    boolean detection;

    public Fun(Fluid inputs, Fluid outputs, Action transition) {
        Subject press = Suite.set(Boolean.class, false);
        this.inputs = inputs.map(s -> {
            ValueProducer<?> vp;
            if(s.assigned(ValueProducer.class)) {
                vp = s.asExpected();
            } else if(s.direct() == SELF) {
                vp = new Constant<>(this);
            } else {
                vp = new Constant<>(s.direct());
            }
            if(vp.attachOutput(this)) press.set(Boolean.class, true);
            return Suite.set(s.key().direct(), vp);
        }).set();
        this.outputs = Suite.set();
        for(var s : outputs) {
            if(s.assigned(ValueConsumer.class)) {
                ValueConsumer vc = s.asExpected();
                if(vc instanceof Var && ((Var<?>) vc).cycleTest(this))
                    throw new RuntimeException("Illegal cycle detected");
                vc.attachInput(this);
                this.outputs.set(s.key().direct(), new WeakReference<>(vc));
            }
        }
        this.transition = transition;
        if(press.get(Boolean.class).asExpected())press(false);
    }

    public void execute() {
        Subject inputParams = inputs.map(s -> Suite.set(s.key().direct(), s.asGiven(ValueProducer.class).get(this))).set();
        if(detection) {
            detection = false;
            Subject outputParams = transition.play(inputParams);
            outputParams.forEach(s -> {
                var key = s.key().direct();
                var output = outputs.get(key);
                if (output.settled()) {
                    WeakReference<ValueConsumer> ref = output.asExpected();
                    ValueConsumer vc = ref.get();
                    if(vc == null) {
                        outputs.unset(key);
                    } else {
                        vc.set(s.asExpected(), this);
                    }
                }
            });
        }
    }

    public boolean press(boolean direct) {
        if(detection) return false;
        if(direct)detection = true;
        for(var s : outputs) {
            WeakReference<ValueConsumer> ref = s.asExpected();
            ValueConsumer var = ref.get();
            if(var != null && var.press(this)) return true;
        }
        return false;
    }

    public void attach() {
        boolean press = false;
        for(ValueProducer<?> vp : inputs.values(ValueProducer.class)) {
            if(vp.attachOutput(this)) press = true;
        }
        if(press) press(false);
    }

    public void attach(boolean forcePress) {
        boolean press = false;
        for(ValueProducer<?> vp : inputs.values(ValueProducer.class)) {
            if(vp.attachOutput(this)) press = true;
        }
        if(forcePress) press(true);
        else if(press) press(false);
    }

    public void detach() {
        inputs.keys().forEach(this::detachInput);
    }

    public void detachInput(Object key) {
        var s = inputs.get(key);
        if(s.settled()) {
            ValueProducer<?> var = s.asExpected();
            var.detachOutput(this);
        }
    }

    public void detachInputVar(Var<?> input) {
        for (var s : inputs){
            if(s.direct().equals(input)) {
                detachInput(s.key().direct());
            }
        }
    }

    public Fun reduce(boolean execute) {
        boolean allConstants = true;
        for(Object o : inputs.values()) {
            if(!(o instanceof Constant)) {
                allConstants = false;
                break;
            }
        }
        if(execute){
            detection = true;
            execute();
        }
        if(allConstants)detach();
        return this;
    }

    boolean cycleTest(Fun fun) {
        for(var s : outputs) {
            WeakReference<ValueConsumer> ref = s.asExpected();
            ValueConsumer v = ref.get();
            if(v == null){
                outputs.unset(s.key().direct());
            } else if(v instanceof Var && ((Var<?>) v).cycleTest(fun)) return true;
        }
        return false;
    }
}
