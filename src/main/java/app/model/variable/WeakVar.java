package app.model.variable;

public class WeakVar<T> extends AbstractVar<T> {

    Var<T> var;

    public WeakVar(Var<T> var) {
        this.var = var;
    }

    @Override
    T get(Fun fun) {
        return var.value;
    }

    @Override
    void attachOutput(Fun fun) {}

    @Override
    void detachOutput(Fun fun) {}

}
