package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Fluid;

import java.util.function.Function;

public final class NumberVar extends Var<Number> {

    public static NumberVar emit() {
        return new NumberVar(null, false);
    }

    public static NumberVar emit(Number value) {
        return new NumberVar(value, false);
    }

    public static NumberVar emit(Number value, boolean instant) {
        return new NumberVar(value, instant);
    }

    public static NumberVar assigned(Object that) {
        NumberVar v = new NumberVar(null, false);
        Fun.assign(that, v).attach(true);
        return v;
    }

    public static NumberVar compound(Number value, Fluid components, Action recipe, Object resultKey) {
        NumberVar composite = new NumberVar(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).attach();
        return composite;
    }

    public static NumberVar compound(Fluid components, Action recipe, Object resultKey) {
        NumberVar composite = new NumberVar(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).attach(true);
        return composite;
    }


    public static NumberVar compound(Number value, Fluid components, Function<Subject, Number> recipe) {
        NumberVar composite = new NumberVar(value, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s))).attach();
        return composite;
    }

    public static NumberVar compound(Fluid components, Function<Subject, Number> recipe) {
        NumberVar composite = new NumberVar(null, false);
        Fun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(OWN_VALUE, composite),
                s -> Suite.set(OWN_VALUE, recipe.apply(s))).attach(true);
        return composite;
    }

    public static NumberVar expressed(Fluid components, Exp expression) {
        NumberVar composite = new NumberVar(null, false);
        BeltFun.express(ValueProducer.prepareComponents(components, composite),
                Suite.add(composite), expression).attach(true);
        return composite;
    }

    public static NumberVar expressed(Fluid components, String expression) {
        NumberVar composite = new NumberVar(null, false);
        BeltFun.express(ValueProducer.prepareComponents(components, composite),
                Suite.add(composite), expression).attach(true);
        return composite;
    }

    public static NumberVar expressed(Number value, Fluid components, Action recipe) {
        NumberVar composite = new NumberVar(value, false);
        BeltFun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(composite), recipe).attach();
        return composite;
    }

    public static NumberVar expressed(Fluid components, Action recipe) {
        NumberVar composite = new NumberVar(null, false);
        BeltFun.compose(ValueProducer.prepareComponents(components, composite), Suite.set(composite), recipe).attach(true);
        return composite;
    }

    public static NumberVar expressed(String e, Object ... params) {
        return expressed( Playground.abc(params), e);
    }

    public static NumberVar expressed(String e, Fluid params) {
        return expressed(params, e);
    }

    public static NumberVar add(Object a, Object b) {
        return expressed( Playground.abc(a, b), Exp::add);
    }

    public static NumberVar sum(Object ... o) {
        return expressed( Playground.abc(o), Exp::sum);
    }

    public static NumberVar difference(Object a, Object b) {
        return expressed( Playground.abc(a, b), Exp::sub);
    }

    public NumberVar(Number value, boolean instant) {
        super(value, instant);
    }

    public byte getByte() {
        return get().byteValue();
    }

    public short getShort() {
        return get().shortValue();
    }

    public int getInt() {
        return get().intValue();
    }

    public long getLong() {
        return get().longValue();
    }

    public float getFloat() {
        return get().floatValue();
    }

    public double getDouble() {
        return get().doubleValue();
    }
}
