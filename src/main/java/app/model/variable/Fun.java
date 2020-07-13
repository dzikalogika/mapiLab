package app.model.variable;

import jorg.processor.ProcessorException;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiPredicate;

public class Fun {

    public static class Const {}

    public static final Const SELF = new Const();

    public static Fun compose(Subject inputs, Subject outputs, Action transition) {
        return new Fun(inputs, outputs, transition);
    }

    public static<T, T1 extends T> Fun assign(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(source), Suite.set(Var.OWN_VALUE, target), s -> Suite.set(Var.OWN_VALUE, s.direct()));
    }

    public static<T, T1 extends T> Fun suppress(Var<T1> source, Var<T> target, BiPredicate<T1, T> suppressor) {
        return new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> suppressor.test(s.recent().asExpected(), s.asExpected()) ? Suite.set() : s);
    }

    public static<T, T1 extends T> Fun suppressIdentity(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> s.direct() == s.recent().direct() ? Suite.set() : s);
    }

    public static<T, T1 extends T> Fun suppressEquality(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> Objects.equals(s.direct(), s.recent().direct()) ? Suite.set() : s);
    }

    public static Fun express(Subject params, String expression) throws ProcessorException {
        ExpressionProcessor processor = new ExpressionProcessor();
        processor.setParams(params);
        processor.ready();
        for(PrimitiveIterator.OfInt it = expression.codePoints().iterator();it.hasNext();) {
            processor.advance(it.nextInt());
        }
        Subject result = processor.finish();

        return new Fun(result.at("in"), result.at("out"), result.get("action").asExpected());
    }

    Subject inputs;
    Subject outputs;
    Action transition;
    boolean detection;

    public Fun(Subject inputs, Subject outputs, Action transition) {
        this.inputs = inputs.front().advance(s -> {
            AbstractVar<?> v;
            if(s.assigned(AbstractVar.class)) {
                v = s.asExpected();
            } else if(s.direct() == SELF) {
                v = new Constant<>(this);
            } else {
                v = new Constant<>(s.direct());
            }
            v.attachOutput(this);
            return Suite.set(s.key().direct(), v);
        }).toSubject();
        this.outputs = Suite.set();
        for(var s : outputs.front()) {
            if(s.assigned(Var.class)) {
                Var<?> v = s.asExpected();
                if(v.cycleTest(this)) throw new RuntimeException("Illegal cycle detected");
                v.attachInput(this);
                this.outputs.set(s.key().direct(), new WeakReference<>(v));
            }
        }
        this.transition = transition;
    }

    public void execute() {
        Subject inputParams = inputs.front().advance(s -> Suite.set(s.key().direct(), s.asGiven(AbstractVar.class).get(this))).toSubject();
        if(detection) {
            detection = false;
            Subject outputParams = transition.play(inputParams);
            outputParams.front().forEach(s -> {
                var key = s.key().direct();
                var output = outputs.get(key);
                if (output.settled()) {
                    WeakReference<Var<?>> ref = output.asExpected();
                    Var<?> v = ref.get();
                    if(v == null) {
                        outputs.unset(key);
                    } else {
                        v.set(s.asExpected(), this);
                    }
                }
            });
        }
    }

    public boolean press(boolean direct) {
        if(detection) return false;
        if(direct)detection = true;
        for(var s : outputs.front()) {
            WeakReference<Var<?>> ref = s.asExpected();
            Var<?> var = ref.get();
            if(var != null && var.press(this)) return true;
        }
        return false;
    }

    public void detach() {
        inputs.front().keys().forEach(this::detachInput);
        inputs = Suite.set();
        outputs = Suite.set();
    }

    public void detachOutput(Object key) {
        outputs.unset(key);
    }

    public void detachOutputVar(Var<?> output) {
        for (var s : outputs.front()){
            WeakReference<Var<?>> ref = s.asExpected();
            Var<?> var = ref.get();
            if(var == null || var.equals(output)) {
                outputs.unset(s.key().direct());
            }
        }
    }

    public void detachInput(Object key) {
        var s = inputs.get(key);
        if(s.settled()) {
            inputs.unset(key);
            AbstractVar<?> var = s.asExpected();
            var.detachOutput(this);
        }
    }

    public void detachInputVar(Var<?> input) {
        for (var s : inputs.front()){
            if(s.direct().equals(input)) {
                detachInput(s.key().direct());
            }
        }
    }

    boolean cycleTest(Fun fun) {
        for(var s : outputs.front()) {
            WeakReference<Var<?>> ref = s.asExpected();
            Var<?> v = ref.get();
            if(v == null){
                outputs.unset(s.key().direct());
            } else if(v.cycleTest(fun)) return true;
        }
        return false;
    }
}
