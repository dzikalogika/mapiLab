package app.model.variable;

import app.model.util.Alphabetic;
import app.model.util.Generator;
import app.model.util.Numeric;
import suite.suite.Sub;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;
import suite.suite.action.Statement;
import suite.suite.util.Sequence;
import suite.suite.util.Series;

import java.util.Arrays;
import java.util.function.Function;

public abstract class Playground {

    protected Monitor eventRoot = new Monitor();

    public Fun intent(Series $inputs, Series $outputs, Action action) {
        return eventRoot.intent($inputs, $outputs, action);
    }

    public Fun intent(Series $inputs, Statement statement) {
        return eventRoot.intent($inputs, statement);
    }

    public Fun intent(Series $inputs, Impression impression) {
        return eventRoot.intent($inputs, impression);
    }

    public <V> Fun intent(Series $inputs, ValueConsumer output, Function<Subject, V> function) {
        return eventRoot.intent($inputs, output, function);
    }

    public Fun instant(Series $inputs, Subject $outputs, Action action) {
        return eventRoot.instant($inputs, $outputs, action);
    }

    public Fun instant(Series $inputs, Impression impression) {
        return eventRoot.instant($inputs, impression);
    }

    public <V> Fun instant(Series $inputs, ValueConsumer output, Function<Subject, V> function) {
        return eventRoot.instant($inputs, output, function);
    }

    public void play() {
        eventRoot.release();
    }



    public static Fun fun(Series $in, Exp exp, Series $out) {
        return BeltFun.express($in, $out, exp);
    }

    public static Fun fun(Series $in, String exp, Series $out) {
        return BeltFun.express($in, $out, exp);
    }

    public static Series num(Object ... objects) {
        return Sequence.of(objects).series().convert(new Numeric());
    }

    public static Series abc(Object ... objects) {
        return Sequence.of(objects).series().convert(new Alphabetic());
    }

//    public static Series abcS(Object ... objects) {
//        return Series.engageS(Generator.alphas(), Arrays.asList(objects));
//    }
//
//    public static Subject add(Object ... values) {
//        Subject s = Suite.set();
//        for(Object o : values) {
//            s.add(o);
//        }
//        return s;
//    }
//
//    public static<T> Sub<T> sub() {
//        return new Sub<>();
//    }
//
//    public static boolean in(Subject source, Object ... keys) {
//        for(Object it : keys) {
//            if(source.get(it).desolated()) return false;
//        }
//        return true;
//    }
//
//    public static boolean in(Subject source, Object key) {
//        return source.get(key).settled();
//    }
}
