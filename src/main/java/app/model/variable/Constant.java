package app.model.variable;

public class Constant<T> extends AbstractVar<T> {

    T value;

    public Constant(T value) {
        this.value = value;
    }

    @Override
    T get(Fun fun) {
        return value;
    }

    @Override
    void attachOutput(Fun fun) {}

    @Override
    void detachOutput(Fun fun) {}
}
