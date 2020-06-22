package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.function.BiPredicate;

public class PreviousVar<T> extends Var<T> {

    private Var<T> var;
    private T value;

    public PreviousVar(Var<T> var) {
        this.var = var;
    }

    @Override
    public boolean detection() {
        if(detectionFlag) {
            detectionFlag = false;
            return true;
        }
        return false;
    }

    @Override
    public T get() {
//        System.out.println("v" + value);
        return value;
    }

    @Override
    public void raiseDetectionFlag() {
        if(!detectionFlag) {
            subjects.front().keys().filter(Monitor.class).forEach(Monitor::raiseSuspicionFlag);
            detectionFlag = true;
        }
        value = var.value;
    }

    @Override
    public void raiseSuspicionFlag() {
        raiseDetectionFlag();
    }

    @Override
    public void recipe(Subject params, Action action) {}

    @Override
    public void assign(Var<T> v) {
        this.var = v;
    }

    public IdVar<T> suppressIdentity() {
        IdVar<T> identityVar = new IdVar<>();
        identityVar.assign(this);
        return identityVar;
    }

    public EqVar<T> suppressEquality() {
        EqVar<T> equalityVar = new EqVar<>();
        equalityVar.assign(this);
        return equalityVar;
    }

    public SuppressedVar<T> suppress(BiPredicate<T, T> suppressor) {
        SuppressedVar<T> suppressedVar = new SuppressedVar<>(suppressor);
        suppressedVar.assign(this);
        return suppressedVar;
    }
}
