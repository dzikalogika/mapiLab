package app.model.variable;

import suite.suite.Suite;
import suite.suite.util.Fluid;

public interface ValueProducer<T> {

    class Const {}

    Var.Const OWN_VALUE = new Const();
    Var.Const SELF = new Const();

    T get(Fun fun);
    boolean attachOutput(Fun fun);
    void detachOutput(Fun fun);
    default ValueProducer<T> weak() {
        return this;
    }

    static Fluid prepareComponents(Fluid components, ValueProducer<?> self) {
        return components.map(s -> {
            if(s.direct() == OWN_VALUE)
                return Suite.set(s.key().direct(), self.weak());
            else if(s.direct() == SELF)
                return Suite.set(s.key().direct(), new Constant<>(self));
            else return s;
        });
    }
}
