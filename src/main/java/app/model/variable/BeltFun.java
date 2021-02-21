package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Series;

import java.lang.ref.WeakReference;

public class BeltFun extends Fun {

    public static BeltFun compose(Series $inputs, Series $outputs, Action transition) {
        BeltFun fun = new BeltFun($inputs, $outputs, transition);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static BeltFun express(Series $inputs, Series $outputs, Exp expression) {
        BeltFun fun = new BeltFun($inputs, $outputs, expression);
        fun.attach();
        if(fun.detection) fun.press(false);
        return fun;
    }

    public static BeltFun express(Series $inputs, Series $outputs, String expression) {
        return express($inputs, $outputs, Exp.compile(expression));
    }

    public BeltFun(Series $inputs, Series $outputs, Action transition) {
        super($inputs, $outputs, transition);
    }

    @Override
    public void execute() {
        var $inputParams = $inputs.convert($ -> Suite.set($.direct(), $.in().as(ValueProducer.class).get(this))).set();
        if(detection) {
            detection = false;
            var $outputParams = transition.play($inputParams);
            for (var $ : Series.parallel($outputs.eachIn(), $outputParams.eachIn())) {
                if($.at(0).is(WeakReference.class)) {
                    WeakReference<ValueConsumer> ref = $.at(0).asExpected();
                    ValueConsumer vc = ref.get();
                    if (vc != null) vc.set($.at(1).asExpected(), this);
                }
            }
        }
    }

    boolean cycleTest(Fun fun) {
        for(var $ : $outputs.eachIn()) {
            WeakReference<ValueConsumer> ref = $.asExpected();
            ValueConsumer v = ref.get();
            if(v instanceof Var && ((Var<?>) v).cycleTest(fun)) return true;
        }
        return false;
    }
}
