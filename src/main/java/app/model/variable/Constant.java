package app.model.variable;

public class Constant<T> implements ValueProducer<T> {

    T value;

    public Constant(T value) {
        this.value = value;
    }

    @Override
    public T get(Fun fun) {
        return value;
    }

    @Override
    public void attachOutput(Fun fun) {}

    @Override
    public void detachOutput(Fun fun) {}
}
