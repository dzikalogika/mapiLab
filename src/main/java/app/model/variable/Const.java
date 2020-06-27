package app.model.variable;

public class Const<T> implements ValueContainer<T> {

    public static<V> Const<V> of(V value) {
        return new Const<>(value);
    }

    private final T value;

    public Const(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
