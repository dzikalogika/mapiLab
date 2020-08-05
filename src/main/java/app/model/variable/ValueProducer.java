package app.model.variable;

public interface ValueProducer<T> {

    T get(Fun fun);
    void attachOutput(Fun fun);
    void detachOutput(Fun fun);

}
