package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Cascade;
import suite.suite.util.Series;

public abstract class Exp implements Action {

    Subject $inputs;
    Subject $outputs;

    public Exp(Subject $inputs, Subject $outputs) {
        this.$inputs = $inputs;
        this.$outputs = $outputs;
    }

    public static Exp compile(String expressionString) {
        return new ExpressionProcessor().process(expressionString).asExpected();
    }

    public static Subject sin(Subject s) {
        return Suite.set(Math.sin(s.asDouble()));
    }

    public static Subject cos(Subject s) {
        return Suite.set(Math.cos(s.asDouble()));
    }

    public static Subject max(Series f) {
        Cascade<Number> c = f.eachIn().eachAs(Number.class).cascade();
        if(c.hasNext()) {
            double max = c.next().doubleValue();
            for(Number n : c.toEnd()) {
                max = Math.max(max, n.doubleValue());
            }
            return Suite.set(max);
        } else return Suite.set();
    }

    public static Subject min(Series f) {
        Cascade<Number> c = f.eachIn().eachAs(Number.class).cascade();
        if(c.hasNext()) {
            double min = c.next().doubleValue();
            for(Number n : c.toEnd()) {
                min = Math.min(min, n.doubleValue());
            }
            return Suite.set(min);
        } else return Suite.set();
    }

    public static Subject sum(Series f) {
        double sum = 0.0;
        for(Number n : f.eachIn().eachAs(Number.class)) {
            sum += n.doubleValue();
        }
        return Suite.set(sum);
    }

    public static Subject rev(Series f) {
        Subject sub = Suite.set();
        for(var v : f) {
            Object o = v.in().direct();
            if(o instanceof Number) {
                sub.set(v.direct(), -((Number)o).doubleValue());
            } else {
                sub.alter(v);
            }
        }
        return sub;
    }

    public static Subject sub(Subject s) {
        return Suite.set(s.at(0).asDouble() - s.at(1).asDouble());
    }

    public static Subject add(Subject s) {
        return Suite.set(s.at(0).asDouble() + s.at(1).asDouble());
    }
}
