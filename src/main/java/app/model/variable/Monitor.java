package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;
import suite.suite.util.Series;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class Monitor implements ValueConsumer, ValueProducer<Boolean> {

    Subject $instantInputs = Suite.set();
    Subject $intentInputs = Suite.set();
    Subject $outputs = Suite.set();
    Subject $detections = Suite.set();

    public static Monitor compose(boolean pressed, Series $components, Impression impression) {
        Monitor monitor = new Monitor();
        Fun fun = monitor.intent(ValueProducer.prepareComponents($components, monitor).set(), impression);
        if(pressed) fun.press(true);
        return monitor;
    }

    public static Monitor compose(boolean pressed, Series $components) {
        return Monitor.compose(pressed, $components, s -> {});
    }

    public Fun intent(Series $inputs, Series $outputs, Action action) {
        Fun fun = Fun.compose($inputs, $outputs.join(Suite.put(this)), action);
        return fun;
    }

    public Fun intent(Series $inputs, Impression impression) {
        Fun fun = Fun.compose($inputs, Suite.put(this), impression);
        return fun;
    }

    public <V> Fun intent(Series $inputs, ValueConsumer output, Function<Subject, V> function) {
        Fun fun = Fun.compose($inputs, Suite.set(Var.OWN_VALUE, output).put(this), $ -> Suite.set(Var.OWN_VALUE, function.apply($)));
        return fun;
    }

    public Fun instant(Series $inputs, Subject $outputs, Action action) {
        Fun fun = Fun.compose($inputs, $outputs.put(this), action);
        attachInstant(fun);
        return fun;
    }

    public Fun instant(Series $inputs, Impression impression) {
        Fun fun = Fun.compose($inputs, Suite.put(this), impression);
        attachInstant(fun);
        return fun;
    }

    public <V> Fun instant(Series $inputs, ValueConsumer output, Function<Subject, V> function) {
        Fun fun = Fun.compose($inputs, Suite.set(Var.OWN_VALUE, output).put(this), $ -> Suite.set(Var.OWN_VALUE, function.apply($)));
        attachInstant(fun);
        return fun;
    }

    public boolean release() {
        if ($detections.present()) {
            $detections.eachAs(Fun.class).forEach(Fun::execute);
            $detections = Suite.set();
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
        if($instantInputs.present(fun)) {
            fun.execute();
            return true;
        } else {
            boolean pressOutputs = $detections.absent();
            $detections.sate(fun);
            if(pressOutputs) {
                for(var $ : $outputs.eachIn()) {
                    WeakReference<Fun> ref = $.asExpected();
                    Fun f = ref.get();
                    if(f != null && f != fun && f.press(false)) return true;
                }
            }
            return false;
        }
    }

    @Override
    public void set(Object value, Fun fun) {
        $detections.unset(fun);
    }

    public void attachInstant(Fun fun) {
        $detections.unset(fun);
        $intentInputs.unset(fun);
        $instantInputs.set(fun);
        if(fun.detection)fun.execute();
    }

    @Override
    public void attachInput(Fun fun) {
        if($instantInputs.absent(fun)) $intentInputs.set(fun);
    }

    public void detach(Fun fun) {
        $intentInputs.unset(fun);
        $instantInputs.unset(fun);
        $detections.unset(fun);
    }

    @Override
    public boolean attachOutput(Fun fun) {
        $outputs.sate(new WeakReference<>(fun));
        return $detections.present();
    }

    @Override
    public void detachOutput(Fun fun) {
        for(var $ : $outputs.eachIn()) {
            WeakReference<Fun> ref = $.asExpected();
            Fun f = ref.get();
            if(f == null || f == fun) {
                $outputs.unset(ref);
            }
        }
    }

    public Subject getDetections() {
        return $detections;
    }
}
