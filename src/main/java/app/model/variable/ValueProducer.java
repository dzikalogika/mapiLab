package app.model.variable;

import suite.suite.Suite;
import suite.suite.util.Series;

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

    static Series prepareComponents(Series $components, ValueProducer<?> self) {
        return $components.convert($ -> {
            var $v = $.at();
            if($v.direct() == OWN_VALUE)
                return Suite.set($.direct(), self.weak());
            else if($v.direct() == SELF)
                return Suite.set($.direct(), new Constant<>(self));
            else return $;
        });
    }
}
