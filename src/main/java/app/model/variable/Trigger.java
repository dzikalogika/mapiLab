package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;

public class Trigger extends Monitor{

    public static final int INSTANT = 1;
    public static final int INITIAL_DETECTION = 2;

    static boolean raised(int buffer, int flag) {
        return (buffer & flag) != 0;
    }

    Action action;
    boolean instant;

    Trigger(boolean initialDetection, boolean instant, Subject monitored, Impression impression) {
        super(initialDetection, monitored);
        this.action = impression;
        this.instant = instant;
        if(instant)detection();
    }

    public Trigger() {
        super();
        this.instant = false;
    }

    public Trigger(Subject params, Impression impression) {
        this(false, false, params, impression);
    }

    public Trigger(int flags, Subject params, Impression impression) {
        this(raised(flags, INITIAL_DETECTION), raised(flags, INSTANT), params, impression);
    }

    @Override
    public boolean detection() {
        if(!detectionFlag && suspicionFlag) {
            monitored.front().values().filter(Var.class).forEach(Var::detection);
        }
        suspicionFlag = false;
        if(detectionFlag) {
            detectionFlag = false;
            if(action != null) {
                action.play(monitored.front().advance(
                        s -> Suite.set(s.key().direct(), s.asGiven(ValueContainer.class).get())).toSubject());
            }
            return true;
        }
        return false;
    }

    public void impact(Subject params, Impression impression) {
        impact(false, params, impression);
    }

    public void impact(boolean initialDetection, Subject params, Impression impression) {
        this.action = impression;
        monitor(params);
        detectionFlag = initialDetection;
        if(instant)detection();
    }

    @Override
    public void raiseDetectionFlag() {
        detectionFlag = true;
        if(instant)detection();
    }

    @Override
    protected void raiseSuspicionFlag() {
        suspicionFlag = true;
        if(instant)detection();
    }

    public void abort() {
        super.abort();
        action = null;
    }
}
