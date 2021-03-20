package app.model.var;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Vars {

    public static<T> Var<T> get() {
        return new Var<>();
    }

    public static<T> Var<T> get(Class<T> type) {
        return new Var<>();
    }

    public static<T> Var<T> set(T t) {
        return new Var<>(t);
    }

    public static<T> Var<T> let(Supplier<T> supplier) {
        var v = new Var<T>();
        v.let(supplier);
        return v;
    }

    public static<T, A> Var<T> let(Supplier<A> sup, Function<A, T> fun) {
        var v = new Var<T>();
        v.let(() -> fun.apply(sup.get()));
        return v;
    }

    public static<T, A, B> Var<T> let(Supplier<A> sup1, Supplier<B> sup2, BiFunction<A, B, T> fun) {
        var v = new Var<T>();
        v.let(() -> fun.apply(sup1.get(), sup2.get()));
        return v;
    }

    public static<T> PreservativeVar<T> preserve(Supplier<T> sup, Supplier<?> ... guards) {
        return new PreservativeVar<>(sup, guards);
    }

}
