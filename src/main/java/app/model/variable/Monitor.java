package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;
import suite.suite.util.Fluid;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class Monitor implements ValueConsumer<Object>, ValueProducer<Boolean> {

    Subject instantInputs = Suite.set();
    Subject intentInputs = Suite.set();
    Subject outputs = Suite.set();
    Subject detections = Suite.set();

    public static Monitor compose(boolean pressed, Fluid components, Impression impression) {
        Monitor monitor = new Monitor();
        Fun fun = monitor.intent(ValueProducer.prepareComponents(components, monitor).set(), impression);
        if(pressed) fun.press(true);
        return monitor;
    }

    public static Monitor compose(boolean pressed, Subject components) {
        return Monitor.compose(pressed, components, s -> {});
    }

    public Fun intent(Subject inputs, Subject outputs, Action action) {
        return Fun.compose(inputs, outputs.add(this), action);
    }

    public Fun intent(Subject inputs, Impression impression) {
        return Fun.compose(inputs, Suite.set(this), impression);
    }

    public <V> Fun intent(Subject inputs, ValueConsumer<V> output, Function<Subject, V> function) {
        return Fun.compose(inputs, Suite.set(Var.OWN_VALUE, output).set(this), s -> Suite.set(Var.OWN_VALUE, function.apply(s)));
    }

    public Fun instant(Subject inputs, Subject outputs, Action action) {
        Fun fun = Fun.compose(inputs, outputs.add(this), action);
        attachInstant(fun);
        return fun;
    }

    public Fun instant(Subject inputs, Impression impression) {
        Fun fun = Fun.compose(inputs, Suite.set(this), impression);
        attachInstant(fun);
        return fun;
    }

    public <V> Fun instant(Subject inputs, ValueConsumer<V> output, Function<Subject, V> function) {
        Fun fun = Fun.compose(inputs, Suite.set(Var.OWN_VALUE, output).set(this), s -> Suite.set(Var.OWN_VALUE, function.apply(s)));
        attachInstant(fun);
        return fun;
    }

    public boolean release() {
        if (detections.settled()) {
            detections.values().filter(Fun.class).forEach(Fun::execute);
            detections = Suite.set();
            return true;
        }
        return false;
    }

    public Boolean get() {
        return release();
    }

    public Boolean get(Fun fun) {
        return release();
    }

    @Override
    public boolean press(Fun fun) {
        if(instantInputs.get(fun).settled()) {
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

    @Override
    public void set(Object value, Fun fun) {
        detections.unset(fun);
    }

    public void attachInstant(Fun fun) {
        detections.unset(fun);
        intentInputs.unset(fun);
        instantInputs.set(fun);
        if(fun.detection)fun.execute();
    }

    @Override
    public void attachInput(Fun fun) {
        if(instantInputs.get(fun).desolated()) intentInputs.set(fun);
    }

    @Override
    public void detachInput(Fun fun) {
        detach(fun);
    }

    public void detach(Fun fun) {
        intentInputs.unset(fun);
        instantInputs.unset(fun);
        detections.unset(fun);
    }

    @Override
    public boolean attachOutput(Fun fun) {
        outputs.put(new WeakReference<>(fun));
        return detections.settled();
    }

    @Override
    public void detachOutput(Fun fun) {
        for(var s : outputs) {
            WeakReference<Fun> ref = s.asExpected();
            Fun f = ref.get();
            if(f == null || f == fun) {
                outputs.unset(ref);
            }
        }
    }

    public Subject getDetections() {
        return detections;
    }
}
