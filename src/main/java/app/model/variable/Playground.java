package app.model.variable;

import suite.suite.Subject;
import suite.suite.action.Action;
import suite.suite.action.Impression;
import suite.suite.util.Fluid;

import java.util.function.Function;

public abstract class Playground {

    protected Monitor eventRoot = new Monitor();

    public Fun intent(Fluid inputs, Subject outputs, Action action) {
        return eventRoot.intent(inputs, outputs, action);
    }

    public Fun intent(Fluid inputs, Impression impression) {
        return eventRoot.intent(inputs, impression);
    }

    public <V> Fun intent(Fluid inputs, ValueConsumer<V> output, Function<Subject, V> function) {
        return eventRoot.intent(inputs, output, function);
    }

    public Fun instant(Fluid inputs, Subject outputs, Action action) {
        return eventRoot.instant(inputs, outputs, action);
    }

    public Fun instant(Fluid inputs, Impression impression) {
        return eventRoot.instant(inputs, impression);
    }

    public <V> Fun instant(Fluid inputs, ValueConsumer<V> output, Function<Subject, V> function) {
        return eventRoot.instant(inputs, output, function);
    }

    public void play() {
        eventRoot.release();
    }
}
