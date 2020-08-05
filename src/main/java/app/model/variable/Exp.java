package app.model.variable;

import jorg.processor.ProcessorException;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.PrimitiveIterator;

public abstract class Exp implements Action {

    Subject inputs;
    Subject outputs;

    public Exp(Subject inputs, Subject outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static Exp compile(String expressionString) throws ProcessorException {
        ExpressionProcessor processor = new ExpressionProcessor();
        processor.ready();
        for(PrimitiveIterator.OfInt it = expressionString.codePoints().iterator(); it.hasNext();) {
            processor.advance(it.nextInt());
        }
        Subject result = processor.finish();
        return result.asExpected();
    }

    public static Subject sin(Subject s) {
        return Suite.set(Math.sin(s.asDouble()));
    }

    public static Subject cos(Subject s) {
        return Suite.set(Math.cos(s.asDouble()));
    }

    public static Subject max(Subject s) {
        double max = s.asDouble();
        for(Number n : s.front().values().filter(Number.class)) {
            max = Math.max(max, n.doubleValue());
        }
        return Suite.set(max);
    }

    public static Subject min(Subject s) {
        double min = s.asDouble();
        for(Number n : s.front().values().filter(Number.class)) {
            min = Math.min(min, n.doubleValue());
        }
        return Suite.set(min);
    }

    public static Subject sum(Subject s) {
        double sum = 0.0;
        for(Number n : s.front().values().filter(Number.class)) {
            sum += n.doubleValue();
        }
        return Suite.set(sum);
    }
}
