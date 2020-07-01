package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.util.function.BiPredicate;

public class Var<T> extends Trigger implements ValueContainer<T> {

    public static StoryVar story(Object title, Object ... intro) {
        return new StoryVar(title, intro);
    }

    public static StoryVar storyOf(Var<?> storyteller, Object intro, Object ... explication) {
        StoryVar storyVar = new StoryVar(intro, explication);
        storyVar.setStoryteller(storyteller);
        storyteller.subjects.set(storyVar);
        return storyVar;
    }

    public static<V> Var<V> build(V value, Subject params, Action action) {
        return new Var<>(0, value, params, action);
    }

    public static final int TRANSIENT = 4;

    T value;
    final Subject subjects;
    final boolean transientFlag;

    public Var() {
        this(null);
    }

    public Var(T value) {
        super();
        this.value = value;
        subjects = Suite.set();
        transientFlag = false;
    }

    public Var(Subject params, Action recipe) {
        this(0, null, params, recipe);
    }

    public Var(int flags, Subject params, Action recipe) {
        this(flags, null, params, recipe);
    }

    public Var(int flags, T value, Subject params, Action recipe) {
        super();
        subjects = Suite.set();
        instant = raised(flags, INSTANT);
        transientFlag = raised(flags, TRANSIENT);
        this.value = value;
        recipe(raised(flags, INITIAL_DETECTION), params, recipe);
    }

    @Override
    public boolean detection() {
        if(!detectionFlag && suspicionFlag) {
            suspicionFlag = false;
            monitored.front().values().filter(Var.class).forEach(Var::detection);
        } else suspicionFlag = false;
        if(detectionFlag) {
            detectionFlag = false;
            if(action != null) {
                T t = action.play(monitored.front().advance(
                        s -> Suite.set(s.key().direct(), s.asGiven(ValueContainer.class).get())
                ).toSubject()).asExpected();
                set(t);
            }
            return true;
        }
        return false;
    }

    public T get() {
        detection();
        return value;
    }

    public void set(T newValue) {
        value = newValue;
        subjects.front().values().filter(Monitor.class).forEach(Monitor::raiseDetectionFlag);
    }

    @Override
    public void raiseDetectionFlag() {
        if(instant) {
            detectionFlag = true;
            detection();
        } else if(!detectionFlag) {
            detectionFlag = true;
            subjects.front().keys().filter(Monitor.class).forEach(Monitor::raiseSuspicionFlag);
        }
    }

    @Override
    protected void raiseSuspicionFlag() {
        if(instant) {
            suspicionFlag = true;
            detection();
        } else if(!suspicionFlag && !detectionFlag) {
            suspicionFlag = true;
            subjects.front().keys().filter(Monitor.class).forEach(Monitor::raiseSuspicionFlag);
        }
    }

    public void recipe(Subject params, Action action) {
        recipe(false, params, action);
    }

    public void recipe(boolean initialDetection, Subject params, Action action) {
        this.action = action;
        if(this.monitored != null) {
            this.monitored.front().values().filter(Var.class).forEach(v -> v.unsetSubject(this));
        }
        monitored = Suite.set();
        for(var p : params.front()) {
            if(p.assigned(StoryVar.class) && !p.asGiven(StoryVar.class).introduced()) {
                StoryVar v = p.asExpected();
                v.setStoryteller(this);
                subjects.set(v);
                monitored.set(p.key().direct(), v);
            } else if(p.assigned(ValueContainer.class)) {
                ValueContainer<?> v = p.asExpected();
                monitored.set(p.key().direct(), v);
                if(v instanceof Var)((Var<?>)v).setSubject(this);
            } else if (p.direct() == CURRENT_VALUE) {
                monitored.set(p.key().direct(), this);
            } else if (p.direct() == SELF) {
                monitored.set(p.key().direct(), new Const<>(this));
            } else {
                monitored.set(p.key().direct(), new Const<>(p.direct()));
            }
        }
        if(initialDetection)raiseDetectionFlag();
        else raiseSuspicionFlag();
    }

    public void assign(Var<T> v) {
        this.monitored = Suite.set(v);
        v.setSubject(this);
        action = s -> s;
        if(v.suspicionFlag || v.detectionFlag) {
            raiseSuspicionFlag();
        }
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

    public Const<Var<T>> far() {
        return new Const<>(this);
    }

    public Subject getSubjects() {
        return subjects;
    }

    @Override
    public void abort() {
        super.abort();
    }
}
