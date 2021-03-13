package app.model.util;

import suite.suite.Subject;
import suite.suite.util.Glass;

import java.util.function.Supplier;

import static suite.suite.$uite.$;

public class Poll {

    Subject $source;
    Subject $selected = $();

    public Poll(Subject $source) {
        this.$source = $source;
    }

    public boolean pick(Object o) {
        $selected = $source.in(o).get();
        return $selected.present();
    }

    public boolean pick(Object o0, Object ... on) {
        if(pick(o0)) return true;
        for(var o : on) {
            if(pick(o)) return true;
        }
        return false;
    }

    public Object raw() {
        return $selected.raw();
    }

    public <B> B asExpected() {
        return $selected.asExpected();
    }

    public <B> B as(Class<B> type) {
        return $selected.as(type);
    }

    public <B> B as(Glass<? super B, B> type) {
        return $selected.as(type);
    }

    public <B> B as(Class<B> type, B reserve) {
        return $selected.as(type, reserve);
    }

    public <B> B as(Glass<? super B, B> type, B reserve) {
        return $selected.as(type, reserve);
    }

    public <B> B orGiven(B reserve) {
        return $selected.orGiven(reserve);
    }

    public <B> B orDo(Supplier<B> reserve) {
        return $selected.orDo(reserve);
    }

    public boolean is(Class<?> type) {
        return $selected.is(type);
    }
}
