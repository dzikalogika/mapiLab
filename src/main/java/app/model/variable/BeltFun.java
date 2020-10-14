package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;

import java.lang.ref.WeakReference;

public class BeltFun extends Fun {

    public static BeltFun compose(Fluid inputs, Fluid outputs, Action transition) {
        BeltFun fun = new BeltFun(inputs, outputs, transition);
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static BeltFun express(Fluid inputs, Fluid outputs, Exp expression) {
        BeltFun fun = new BeltFun(inputs, outputs, expression);
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static BeltFun express(Fluid inputs, Fluid outputs, String expression) {
        return express(inputs, outputs, Exp.compile(expression));
    }

    public BeltFun(Fluid inputs, Fluid outputs, Action transition) {
        super(inputs, outputs, transition);
    }

    @Override
    public void execute() {
        Subject inputParams = inputs.map(s -> Suite.set(s.key().direct(), s.asGiven(ValueProducer.class).get(this))).set();
        if(detection) {
            detection = false;
            Subject outputParams = transition.play(inputParams);
            for (var s : Fluid.engage(outputParams.values(), outputs.front().values())) {
                if(s.assigned(WeakReference.class)) {
                    WeakReference<ValueConsumer<?>> ref = s.asExpected();
                    ValueConsumer<?> vc = ref.get();
                    if (vc != null) vc.set(s.key().asExpected(), this);
                }
            }
        }
    }

    boolean cycleTest(Fun fun) {
        for(var s : outputs) {
            WeakReference<ValueConsumer<?>> ref = s.asExpected();
            ValueConsumer<?> v = ref.get();
            if(v instanceof Var && ((Var<?>) v).cycleTest(fun)) return true;
        }
        return false;
    }
}
