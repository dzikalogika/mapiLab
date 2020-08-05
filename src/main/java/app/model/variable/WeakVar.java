package app.model.variable;

public class WeakVar<T> implements ValueProducer<T> {

    Var<T> var;

    public WeakVar(Var<T> var) {
        this.var = var;
    }

    @Override
    public T get(Fun fun) {
        return var.value;
    }

    @Override
    public void attachOutput(Fun fun) {}

    @Override
    public void detachOutput(Fun fun) {}

}
