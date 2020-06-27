package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;

public class Monitor {

    public static final Object SELF = new Object();
    public static final Object CURRENT_VALUE = new Object();

    Subject monitored;
    boolean detectionFlag;
    boolean suspicionFlag;

    public Monitor() {
        suspicionFlag = false;
        detectionFlag = false;
    }

    public Monitor(boolean initialDetection, Subject toMonitor) {
        monitor(toMonitor);
        detectionFlag = initialDetection;
    }

    public Monitor(Subject toMonitor) {
        monitor(toMonitor);
    }

    public boolean detection() {
        if(!detectionFlag && suspicionFlag) {
            monitored.front().values().filter(Var.class).forEach(Var::detection);
        }
        suspicionFlag = false;
        if(detectionFlag) {
            detectionFlag = false;
            return true;
        }
        return false;
    }

    public void raiseDetectionFlag() {
        detectionFlag = true;
    }

    protected void raiseSuspicionFlag() {
        suspicionFlag = true;
    }

    public void monitor(Subject params) {
        if (this.monitored != null) {
            this.monitored.front().values().filter(Var.class).forEach(v -> v.unsetSubject(this));
        }
        monitored = Suite.set();
        for (var p : params.front()) {
            if (p.assigned(ValueContainer.class)) {
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
        suspicionFlag = true;
        detectionFlag = false;
    }

    public void abort() {
        if(monitored != null) {
            monitored.front().values().filter(Var.class).forEach(v -> v.unsetSubject(this));
            monitored = null;
        }
    }

    public Subject getMonitored() {
        return monitored;
    }
}
