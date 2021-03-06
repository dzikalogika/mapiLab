package app.model.variable;

import app.model.util.Generator;
import jorg.processor.ProcessorException;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.util.Cascade;
import suite.suite.util.Fluid;

import java.util.Arrays;
import java.util.PrimitiveIterator;

public abstract class Exp implements Action {

    Subject inputs;
    Subject outputs;

    public Exp(Subject inputs, Subject outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static Exp compile(String expressionString) {
        ExpressionProcessor processor = new ExpressionProcessor();
        processor.ready();
        try {
            for (PrimitiveIterator.OfInt it = expressionString.codePoints().iterator(); it.hasNext(); ) {
                processor.advance(it.nextInt());
            }
            Subject result = processor.finish();
            return result.asExpected();
        } catch (ProcessorException pe) {
            throw new RuntimeException(pe);
        }
    }

    public static Subject sin(Subject s) {
        return Suite.set(Math.sin(s.asDouble()));
    }

    public static Subject cos(Subject s) {
        return Suite.set(Math.cos(s.asDouble()));
    }

    public static Subject max(Fluid f) {
        Cascade<Number> c = f.values().filter(Number.class).cascade();
        if(c.hasNext()) {
            double max = c.next().doubleValue();
            for(Number n : c.toEnd()) {
                max = Math.max(max, n.doubleValue());
            }
            return Suite.set(max);
        } else return Suite.set();
    }

    public static Subject min(Fluid f) {
        Cascade<Number> c = f.values().filter(Number.class).cascade();
        if(c.hasNext()) {
            double min = c.next().doubleValue();
            for(Number n : c.toEnd()) {
                min = Math.min(min, n.doubleValue());
            }
            return Suite.set(min);
        } else return Suite.set();
    }

    public static Subject sum(Fluid f) {
        double sum = 0.0;
        for(Number n : f.values().filter(Number.class)) {
            sum += n.doubleValue();
        }
        return Suite.set(sum);
    }

    public static Subject rev(Fluid f) {
        Subject sub = Suite.set();
        for(var v : f) {
            Object o = v.direct();
            if(o instanceof Number) {
                sub.set(v.key().direct(), -((Number)o).doubleValue());
            } else {
                sub.inset(v);
            }
        }
        return sub;
    }

    public static Subject sub(Subject s) {
        return Suite.set(s.asDouble() - s.recent().asDouble());
    }

    public static Subject add(Subject s) {
        return Suite.set(s.asDouble() + s.recent().asDouble());
    }
}
