package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.function.Function;

public class NumberVar extends Var<Number> {

    public static NumberVar create() {
        return new NumberVar(0, false);
    }

    public static NumberVar create(Number value) {
        return new NumberVar(value, false);
    }

    public static NumberVar create(Number value, boolean instant) {
        return new NumberVar(value, instant);
    }

    public static NumberVar compose(Number value, Subject components, Action recipe, Object resultKey) {
        NumberVar composite = new NumberVar(value, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe);
        return composite;
    }

    public static NumberVar compose(Subject components, Action recipe, Object resultKey) {
        NumberVar composite = new NumberVar(null, false);
        Fun.compose(prepareComponents(components, composite), Suite.set(resultKey, composite), recipe).press(true);
        return composite;
    }

    public static NumberVar compose(Subject components, String expression) {
        NumberVar composite = new NumberVar(null, false);
        Fun.express(prepareComponents(components, composite), Suite.set("$0", composite), "$0=" + expression).press(true);
        return composite;
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
