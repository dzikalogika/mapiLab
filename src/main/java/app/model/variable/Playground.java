package app.model.variable;

import app.model.util.Generator;
import suite.suite.Sub;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;
import suite.suite.util.Fluid;

import java.util.Arrays;
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



    public static Fun fun(Fluid in, Exp exp, Fluid out) {
        return BeltFun.express(in, out, exp);
    }

    public static Fun fun(Fluid in, String exp, Fluid out) {
        return BeltFun.express(in, out, exp);
    }

    public static Fluid num(Object ... objects) {
        return Fluid.engage(Generator.integers(), Arrays.asList(objects));
    }

    public static Fluid abc(Object ... objects) {
        return Fluid.engage(Generator.alphas(), Arrays.asList(objects));
    }

    public static Fluid abcS(Object ... objects) {
        return Fluid.engageS(Generator.alphas(), Arrays.asList(objects));
    }

    public static Subject add(Object ... values) {
        Subject s = Suite.set();
        for(Object o : values) {
            s.add(o);
        }
        return s;
    }

    public static<T> Sub<T> sub() {
        return new Sub<>();
    }

    public static Subject insec(Subject source, Object ... keys) {
        Subject result = Suite.set();
        for(Object k : keys) {
            result.inset(source.get(k));
        }
        return result;
    }

    public static boolean in(Subject source, Object ... keys) {
        Subject result = Suite.set();
        for(Object it : keys) {
            if(source.get(it).desolated()) return false;
        }
        return true;
    }

    public static boolean in(Subject source, Object key) {
        return source.get(key).settled();
    }
}
