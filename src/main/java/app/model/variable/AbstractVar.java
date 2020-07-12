package app.model.variable;

public abstract class AbstractVar<T> {

    abstract T get(Fun fun);
    abstract void attachOutput(Fun fun);
    abstract void detachOutput(Fun fun);

}
