package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.function.BiPredicate;

public class Var<T> extends Trigger {

    public static<U> Var<U> past(Var<U> var, int shift) {
        if(shift < 0) {
            for (;shift < 0;++shift) {
                var = new PreviousVar<>(var);
            }
        } else throw new RuntimeException("Past shift must be less than zero");

        return var;
    }

    T value;
    final Subject subjects;

    public Var() {
        this(null);
    }

    public Var(T value) {
        super();
        this.value = value;
        subjects = Suite.set();
    }

    public Var(Subject params, Action recipe) {
        this(false, params, recipe);
    }

    public Var(boolean forceFirstDetection, Subject params, Action recipe) {
        subjects = Suite.set();
        recipe(params, recipe);
        detectionFlag = forceFirstDetection;
    }

    public Subject s() {
        return monitored;
    }

    @Override
    public boolean detection() {
        if(!detectionFlag && suspicionFlag) {
            monitored.front().values().filter(Var.class).forEach(Var::detection);
        }
        suspicionFlag = false;
        if(detectionFlag) {
            if(action != null) {
                T t = action.play(monitored.front().advance(
                        s -> Suite.set(s.key().direct(), s.asGiven(Var.class).get())
                ).toSubject()).asExpected();
                set(t);
            }
            detectionFlag = false;
            return true;
        }
        return false;
    }

    public T get() {
        detection();
        return value;
    }

    public void set(T newValue) {
        subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
        value = newValue;
    }

    @Override
    public void raiseDetectionFlag() {
        if(!detectionFlag) {
            subjects.front().keys().filter(Monitor.class).forEach(Monitor::raiseSuspicionFlag);
            detectionFlag = true;
        }
    }

    public void recipe(Subject params, Action action) {
        this.action = action;
        if(this.monitored != null) {
            this.monitored.front().values().filter(Var.class).forEach(v -> v.unsetSubject(this));
        }
        monitored = Suite.set();
        for(var p : params.front()) {
            if(p.assigned(Var.class)) {
                Var<?> v = p.asExpected();
                v.setSubject(this);
                monitored.set(p.key().direct(), v);
            } else if(p.assigned(Integer.class)) {
                Var<T> v = Var.past(this, p.asInt());
                subjects.set(v);
                monitored.set(p.key().direct(), v);
            }
        }
        suspicionFlag = true;
        detectionFlag = false;
    }

    public void assign(Var<T> v) {
        this.monitored = Suite.set(v);
        v.setSubject(this);
        action = s -> s;
    }

    protected void setSubject(Monitor monitor) {
        subjects.set(monitor);
    }

    protected void unsetSubject(Monitor monitor) {
        subjects.unset(monitor);
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
