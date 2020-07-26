package app.model.variable;

import jorg.processor.IntProcessor;
import jorg.processor.ProcessorException;
import suite.suite.Slot;
import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;

public class ExpressionProcessor implements IntProcessor {

    enum State {
        PENDING, NUMBER, SYMBOL
    }

    static class ActionProfile {
        final int priority;
        Action action;

        public ActionProfile(int priority, Impression impression) {
            this.priority = priority;
            this.action = impression;
        }

        public ActionProfile(int priority, Action action) {
            this.priority = priority;
            this.action = action;
        }
    }

    static class FunctionProfile extends ActionProfile {

        public FunctionProfile() {
            super(100, null);
        }

        public FunctionProfile(Action action) {
            super(100, action);
        }
    }

    static class VarNumber extends Number {
        final String symbol;
        Number value;

        public VarNumber(String symbol) {
            this.symbol = symbol;
        }

        void setValue(Number value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public long longValue() {
            return value.longValue();
        }

        @Override
        public float floatValue() {
            return value.floatValue();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public String toString() {
            return symbol + " = " + value;
        }
    }

    enum SpecialSymbol {
        OPEN_BRACKET, CLOSE_BRACKET, ACTION_BRACKET
    }

    private static final ActionProfile attribution = new ActionProfile(0, ExpressionProcessor::attribution);
    private static final ActionProfile maximum = new ActionProfile(1, ExpressionProcessor::maximum);
    private static final ActionProfile minimum = new ActionProfile(1, ExpressionProcessor::minimum);
    private static final ActionProfile addition = new ActionProfile(2, ExpressionProcessor::addition);
    private static final ActionProfile subtraction = new ActionProfile(2, ExpressionProcessor::subtraction);
    private static final ActionProfile multiplication = new ActionProfile(3, ExpressionProcessor::multiplication);
    private static final ActionProfile division = new ActionProfile(3, ExpressionProcessor::division);
    private static final ActionProfile exponentiation = new ActionProfile(4, ExpressionProcessor::exponentiation);
    private static final ActionProfile reversion = new ActionProfile(5, ExpressionProcessor::reversion);
    private static final ActionProfile inversion = new ActionProfile(5, ExpressionProcessor::inversion);
    private static final ActionProfile proportion = new ActionProfile(5, ExpressionProcessor::proportion);
    private static final ActionProfile absolution = new ActionProfile(5, ExpressionProcessor::absolution);

    private static final Subject descriptiveActionProfiles = Suite.
            set("sin", (Action)Exp::sin).
            set("cos", (Action)Exp::cos).
            set("max", (Action)Exp::max).
            set("min", (Action)Exp::min).
            set("sum", (Action)Exp::sum);


    private Subject variables;
    private Subject functions;

    private Subject actions;
    private StringBuilder builder;
    private State state;
    private Subject rpn;
    private Subject outputs;
    private boolean emptyValueBuffer;

    @Override
    public Subject ready() {
        variables = Suite.set();
        functions = Suite.set();
        outputs = Suite.set();
        rpn = Suite.set();
        actions = Suite.set();
        state = State.PENDING;
        emptyValueBuffer = true;
        return Suite.set();
    }

    private void pushAction(ActionProfile profile) {
        for(var s : actions.reverse()) {
            if(s.assigned(ActionProfile.class)) {
                ActionProfile actionProfile = s.asExpected();
                if(actionProfile.priority > profile.priority) {
                    rpn.add(actionProfile);
                    actions.unset(s.key().direct());
                } else break;
            } else if(s.direct() == SpecialSymbol.OPEN_BRACKET) {
                break;
            }
        }
        actions.add(profile);
        emptyValueBuffer = true;
    }

    @Override
    public void advance(int i) throws ProcessorException {
        switch (state) {
            case PENDING:
                if(Character.isDigit(i)) {
                    builder = new StringBuilder();
                    builder.appendCodePoint(i);
                    state = State.NUMBER;
                } else if(Character.isJavaIdentifierStart(i)) {
                    builder = new StringBuilder();
                    builder.appendCodePoint(i);
                    state = State.SYMBOL;
                } else if(i == '+') {
                    pushAction(emptyValueBuffer ? absolution : addition);
                } else if(i == '-') {
                    pushAction(emptyValueBuffer ? reversion : subtraction);
                } else if(i == '*') {
                    if(!emptyValueBuffer) pushAction(multiplication);
                } else if(i == '/') {
                    pushAction(emptyValueBuffer ? inversion : division);
                } else if(i == '^') {
                    pushAction(exponentiation);
                } else if(i == '%') {
                    pushAction(proportion);
                } else if(i == '&') {
                    pushAction(minimum);
                } else if(i == '|') {
                    pushAction(maximum);
                }   else if(i == '=') {
                    VarNumber var = rpn.recent().asExpected();
                    outputs.put(var.symbol, var);
                    if(emptyValueBuffer)rpn.add(var);
                    pushAction(attribution);
                } else if(i == '(') {
                    actions.add(SpecialSymbol.OPEN_BRACKET);
                    rpn.add(SpecialSymbol.OPEN_BRACKET);
                    emptyValueBuffer = true;
                } else if(i == ')') {
                    for(var s : actions.reverse()) {
                        actions.unset(s.key().direct());
                        if(s.direct() == SpecialSymbol.OPEN_BRACKET) {
                            break;
                        } else {
                            rpn.add(s.direct());
                        }
                    }
                } else if(i == ',') {
                    for(var s : actions.reverse()) {
                        if(s.direct() == SpecialSymbol.OPEN_BRACKET) {
                            break;
                        } else {
                            rpn.add(s.direct());
                            actions.unset(s.key().direct());
                        }
                    }
                    emptyValueBuffer = true;
                } else if(i == ';') {
                    for(var s : rpn.reverse()) {
                        if(s.direct() == SpecialSymbol.OPEN_BRACKET) {
                            break;
                        } else {
                            actions.add(s.direct());
                            rpn.unset(s.key().direct());
                        }
                    }
                    emptyValueBuffer = true;
                } else if(i == '`') {
                    builder = new StringBuilder();
                    state = State.SYMBOL;
                } else if(!Character.isWhitespace(i)) {
                    throw new ProcessorException();
                }
                break;
            case NUMBER:
                if(Character.isDigit(i) || i == '.') {
                    builder.appendCodePoint(i);
                } else if(!Character.isWhitespace(i)) {
                    try {
                        double d = Double.parseDouble(builder.toString());
                        rpn.add(d);
                        state = State.PENDING;
                        emptyValueBuffer = false;
                        advance(i);
                    } catch (NumberFormatException nfe) {
                        throw new ProcessorException(nfe);
                    }
                }
                break;
            case SYMBOL:
                if(Character.isJavaIdentifierPart(i)) {
                    builder.appendCodePoint(i);
                } else if(i == '(') {
                    String str = builder.toString();
                    pushAction(functions.getSaved(str, new FunctionProfile()).asExpected());
                    actions.add(SpecialSymbol.OPEN_BRACKET);
                    rpn.add(SpecialSymbol.OPEN_BRACKET);
                    state = State.PENDING;
                } else if(!Character.isWhitespace(i)) {
                    VarNumber var = new VarNumber(builder.toString());
                    var = variables.getSaved(var.symbol, var).asExpected();
                    rpn.add(var);
                    emptyValueBuffer = false;
                    state = State.PENDING;
                    advance(i);
                }
                break;
        }
    }

    @Override
    public Subject finish() throws ProcessorException {
        switch (state) {
            case NUMBER -> {
                try {
                    double d = Double.parseDouble(builder.toString());
                    rpn.add(d);
                } catch (NumberFormatException nfe) {
                    throw new ProcessorException(nfe);
                }
            }
            case SYMBOL -> {
                VarNumber var = new VarNumber(builder.toString());
                var = variables.getSaved(var.symbol, var).asExpected();
                rpn.add(var);
            }
        }
        for(var s : actions.reverse()) {
            if(s.assigned(ActionProfile.class)) {
                rpn.add(s.direct());
            }
        }
        return Suite.set(new Exp() {
            @Override
            public Subject play(Subject subject) {
                for (var v : variables.front().values().filter(VarNumber.class)) {
                    v.value = subject.get(v.symbol).orGiven(null);
                }
                for (var f : functions.front()) {
                    String funName = f.key().asString();
                    Subject s1 = subject.get(funName);
                    if (s1.settled()) f.asGiven(FunctionProfile.class).action = s1.asExpected();
                    else {
                        s1 = descriptiveActionProfiles.get(funName);
                        if (s1.settled()) f.asGiven(FunctionProfile.class).action = s1.asExpected();
                        else throw new RuntimeException("Function '" + funName + "' is not defined");
                    }
                }
                Subject bracketStack = Suite.set();
                Subject result = Suite.set();
                for (var su : rpn.front()) {
                    if (su.assigned(Number.class)) {
                        result.add(su.direct());
                    } else if (su.assigned(ActionProfile.class)) {
                        if (su.assigned(FunctionProfile.class)) {
                            Subject p = Suite.set();
                            for (var s1 : result.reverse()) {
                                if (s1.direct() == bracketStack.recent().direct()) {
                                    bracketStack.unset(bracketStack.recent().key().direct());
                                    break;
                                }
                                result.unset(s1.key().direct());
                                p.addAt(Slot.PRIME, s1.direct());
                            }
                            p = su.asGiven(FunctionProfile.class).action.play(p);
                            result.addAll(p.front().values());
                        } else {
                            su.asGiven(ActionProfile.class).action.play(result);
                        }
                    } else {
                        bracketStack.insetAll(result.recent().front());
//                        result.add(su.direct());
                    }
                }
                return Suite.zip(outputs.front().values().filter(VarNumber.class).map(v -> v.symbol),
                        result.front().values().filter(Number.class).map(Number::doubleValue));
            }
        });
    }

    protected static void addition(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(b + a);
    }

    protected static void subtraction(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(b - a);
    }

    protected static void multiplication(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(b * a);
    }

    protected static void division(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(b / a);
    }

    protected static void exponentiation(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(Math.pow(b, a));
    }

    protected static void proportion(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        s.add(a / 100.0);
    }

    protected static void attribution(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        VarNumber var = s.recent().asExpected();
        var.value = a;
    }

    protected static void inversion(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        s.add(1 / a);
    }

    protected static void reversion(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        s.add(-a);
    }

    protected static void absolution(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        s.add(Math.abs(a));
    }

    protected static void maximum(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(Math.max(a, b));
    }

    protected static void minimum(Subject s) {
        double a = s.takeAt(Slot.RECENT).asDouble();
        double b = s.takeAt(Slot.RECENT).asDouble();
        s.add(Math.min(a, b));
    }
}
