package app.model.variable;

import suite.suite.Subject;

public class Monitor {

    Subject monitored;
    boolean detectionFlag;
    boolean suspicionFlag;

    public Monitor() {
        suspicionFlag = false;
        detectionFlag = false;
    }

    public Monitor(boolean forceFirstDetection, Subject toMonitor) {
        monitor(toMonitor);
        detectionFlag = forceFirstDetection;
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

    public void monitor(Subject toMonitor) {
       if(this.monitored != null) {
            this.monitored.front().values().filter(Var.class).forEach(v -> v.unsetSubject(this));
       }
       toMonitor.front().values().filter(Var.class).forEach(v -> v.setSubject(this));
       this.monitored = toMonitor;
       suspicionFlag = true;
       detectionFlag = false;
    }

}
