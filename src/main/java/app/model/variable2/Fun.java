package app.model.variable2;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.Objects;
import java.util.function.BiPredicate;

public class Fun {

    static Fun create(Subject inputs, Subject outputs, Action transition) {
        return new Fun(inputs, outputs, transition);
    }

    static<T, T1 extends T> Fun assign(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(source), Suite.set(0, target), s -> Suite.set(0, s.direct()));
    }

    static<T, T1 extends T> Fun suppress(Var<T1> source, Var<T> target, BiPredicate<T1, T> suppressor) {
        return new Fun(Suite.set(0, source).set(target), Suite.set(0, target),
                s -> suppressor.test(s.asExpected(), s.recent().asExpected()) ? Suite.set() : s);
    }

    static<T, T1 extends T> Fun suppressIdentity(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(0, source).set(target), Suite.set(0, target),
                s -> s.direct() == s.recent().direct() ? Suite.set() : s);
    }

    static<T, T1 extends T> Fun suppressEquality(Var<T1> source, Var<T> target) {
        return new Fun(Suite.set(0, source).set(target), Suite.set(0, target),
                s -> Objects.equals(s.direct(), s.recent().direct()) ? Suite.set() : s);
    }

    Subject inputs;
    Subject outputs;
    Action transition;
    boolean detection;

    public Fun(Subject inputs, Subject outputs, Action transition) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.transition = transition;
        this.inputs.front().values().filter(Var.class).forEach(v -> v.attachOutput(this));
        this.outputs.front().values().filter(Var.class).forEach(v -> v.attachInput(this));
    }

    public void evaluate() {
        Subject inputParams = inputs.front().advance(s -> Suite.set(s.key().direct(), s.asGiven(Var.class).get(this))).toSubject();
        if(detection) {
            detection = false;
            Subject outputParams = transition.play(inputParams);
            outputParams.front().forEach(s -> {
                var output = outputs.get(s.key().direct());
                if (output.settled()) {
                    Var<?> v = output.asExpected();
                    v.set(s.asExpected(), this);
                }
            });
        }
    }

    public boolean press(boolean direct) {
        if(detection) return false;
        if(direct)detection = true;
        for(var v : outputs.front().values().filter(Var.class)) {
            if(v.press(this))return true;
        }
        return false;
    }

    public void cancel() {
        Subject collector = Suite.set(this);
        outputs.front().keys().filter(Fun.class).forEach(f -> f.collectTransient(collector));
        inputs.front().keys().filter(Fun.class).forEach(f -> f.collectTransient(collector));
        Var.utilizeCollected(collector);
    }

    public void detachOutput(Var<?> output) {
        silentDetachOutput(output);
        Subject collector = Suite.set();
        collectTransient(collector);
        output.collectTransient(collector);
        Var.utilizeCollected(collector);
    }

    public void detachInput(Var<?> input) {
        for (var s : inputs.front()){
            if(s.direct().equals(input)) {
                cancel();
                return;
            }
        }
    }

    boolean collectTransient(Subject collector) {
        if(collector.get(this).settled())return true;
        collector.set(this);
        boolean collect = outputs.front().keys().filter(Var.class).allTrue(f -> f.collectTransient(collector));
        if(collect) inputs.front().keys().filter(Var.class).forEach(f -> f.collectTransient(collector));
        else collector.unset(this);
        return collect;
    }

    void silentDetachOutput(Var<?> output) {
        for (var s : outputs.front()){
            if(s.direct().equals(output)) {
                outputs.unset(s.key().direct());
            }
        }
    }

    void utilize(Subject collector) {
        for(Var<?> v : inputs.front().values().filter(Var.class).filter(v -> collector.get(v).desolated())) {
            v.silentDetachOutput(this);
        }
        for(Var<?> v : outputs.front().values().filter(Var.class).filter(v -> collector.get(v).desolated())) {
            v.silentDetachInput(this);
        }
        inputs = null;
        outputs = null;
        transition = null;
    }
}
