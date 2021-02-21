package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Series;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiPredicate;


public class Fun {

    public static class Const {}

    public static final Const SELF = new Const();

    public static Fun compose(Series $inputs, Series $outputs, Action transition) {
        Fun fun = new Fun($inputs, $outputs, transition);
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
                s -> selector.test(s.last().in().asExpected(), s.in().asExpected()) ? s : Suite.set());
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppress(ValueProducer<T1> source, Var<T> target, BiPredicate<T1, T> suppressor) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> suppressor.test(s.last().in().asExpected(), s.in().asExpected()) ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppressIdentity(ValueProducer<T1> source, Var<T> target) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> s.in().direct() == s.last().in().direct() ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static<T, T1 extends T> Fun suppressEquality(ValueProducer<T1> source, Var<T> target) {
        Fun fun = new Fun(Suite.set(Var.OWN_VALUE, source).set(target.weak()), Suite.set(Var.OWN_VALUE, target),
                s -> Objects.equals(s.in().direct(), s.last().in().direct()) ? Suite.set() : s);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static Fun express(Series $inputs, Series $outputs, Exp expression) {
        Fun fun = new Fun($inputs, $outputs, expression);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static Fun express(Series $inputs, Series $outputs, String expression) {
        return express($inputs, $outputs, Exp.compile(expression));
    }

    Subject $inputs;
    Subject $outputs;
    Action transition;
    boolean detection;

    public Fun(Series $inputs, Series $outputs, Action transition) {
        var $press = Suite.set(false);
        this.$inputs = $inputs.convert($ -> {
            var $v = $.at();
            ValueProducer<?> vp;
            if($v.is(ValueProducer.class)) {
                vp = $v.asExpected();
            } else if($v.direct() == SELF) {
                vp = new Constant<>(this);
            } else {
                vp = new Constant<>($v.direct());
            }
            if(vp.attachOutput(this)) $press.reset(true);
            return Suite.set($.direct(), vp);
        }).set();
        this.$outputs = Suite.set();
        for(var $ : $outputs) {
            var $v = $.at();
            if($v.is(ValueConsumer.class)) {
                ValueConsumer vc = $v.asExpected();
                if(vc instanceof Var && ((Var<?>) vc).cycleTest(this))
                    throw new RuntimeException("Illegal cycle detected");
                vc.attachInput(this);
                this.$outputs.set($.direct(), new WeakReference<>(vc));
            }
        }
        this.transition = transition;
        if($press.asBoolean()) press(false);
    }

    public void execute() {
        var $inputParams = $inputs.convert($ -> Suite.set($.direct(), $.in().as(ValueProducer.class).get(this))).set();
        if(detection) {
            detection = false;
            var $outputParams = transition.play($inputParams);
            $outputParams.forEach($ -> {
                var key = $.direct();
                var $output = $outputs.in(key).get();
                if ($output.present()) {
                    WeakReference<ValueConsumer> ref = $output.asExpected();
                    ValueConsumer vc = ref.get();
                    if(vc == null) {
                        $outputs.unset(key);
                    } else {
                        vc.set($.in().asExpected(), this);
                    }
                }
            });
        }
    }

    public boolean press(boolean direct) {
        if(detection) return false;
        if(direct)detection = true;
        for(var $ : $outputs.eachIn()) {
            WeakReference<ValueConsumer> ref = $.asExpected();
            ValueConsumer var = ref.get();
            if(var != null && var.press(this)) return true;
        }
        return false;
    }

    public void attach() {
        attach(false);
    }

    public void attach(boolean forcePress) {
        boolean press = false;
        for(ValueProducer<?> vp : $inputs.eachIn().eachAs(ValueProducer.class)) {
            if(vp.attachOutput(this)) press = true;
        }
        if(forcePress) press(true);
        else if(press) press(false);
    }

    public void detach() {
        $inputs.eachDirect().forEach(this::detachInput);
    }

    public void detachInput(Object key) {
        var $ = $inputs.get(key);
        if($.present()) {
            ValueProducer<?> var = $.in().asExpected();
            var.detachOutput(this);
        }
    }

    public void detachInputVar(Var<?> input) {
        for (var $ : $inputs){
            if($.in().direct().equals(input)) {
                detachInput($.direct());
            }
        }
    }

    public Fun reduce(boolean execute) {
        boolean allConstants = true;
        for(Object o : $inputs.eachIn().eachDirect()) {
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
        for(var $ : $outputs) {
            WeakReference<ValueConsumer> ref = $.in().asExpected();
            ValueConsumer v = ref.get();
            if(v == null){
                $outputs.unset($.direct());
            } else if(v instanceof Var && ((Var<?>) v).cycleTest(fun)) return true;
        }
        return false;
    }
}
