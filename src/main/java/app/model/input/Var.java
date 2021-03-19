package app.model.input;

import suite.suite.Subject;

import java.util.function.Function;
import java.util.function.Supplier;

import static suite.suite.$uite.$;

public class Var<T> implements Source<T> {
    Subject v;

    public Var() {
        v = $();
    }

    public Var(T value) {
        this.v = $(value);
    }

    public void set(T value) {
        this.v.reset(value);
    }

    public void draft(Supplier<T> supplier) {
        this.v.reset(supplier);
    }

    @Override
    public T get() {
        if(v.is(Supplier.class)) {
            Supplier<T> supplier = v.asExpected();
            return supplier.get();
        } else return v.orGiven(null);
    }

    @Override
    public T getOr(T reserve) {
        if(v.is(Supplier.class)) {
            Supplier<T> supplier = v.asExpected();
            return supplier.get();
        } else return v.orGiven(reserve);
    }

    @Override
    public boolean present() {
        return v.present();
    }

    public<A> void let(Supplier<A> sup, Function<A, T> fun) {
        draft(() -> fun.apply(sup.get()));
    }
}
