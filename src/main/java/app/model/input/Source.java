package app.model.input;

import java.util.function.Supplier;

public interface Source<T> extends Supplier<T> {
    boolean present();
    T getOr(T reserve);
}
