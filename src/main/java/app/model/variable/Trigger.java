package app.model.variable;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;
import suite.suite.action.Impression;

public class Trigger extends Monitor{

    Action action;

    Trigger(boolean forceFirstDetection, Subject monitored, Action recipe) {
        super(forceFirstDetection, monitored);
        this.action = recipe;
    }

    public Trigger() {
        super();
    }

    public Trigger(Subject params, Impression impression) {
        this(false, params, (Action)impression);
    }

    public Trigger(boolean forceFirstDetection, Subject params, Impression impression) {
        this(forceFirstDetection, params, (Action)impression);
    }

    @Override
    public boolean detection() {
        if(!detectionFlag && suspicionFlag) {
            monitored.front().values().filter(Var.class).forEach(Var::detection);
        }
        suspicionFlag = false;
        if(detectionFlag) {
            if(action != null) {
                action.play(monitored.front().advance(
                        s -> Suite.set(s.key().direct(), s.asGiven(Var.class).get())
                ).toSubject());
            }
            detectionFlag = false;
            return true;
        }
        return false;
    }

    public void impact(Subject params, Impression impression) {
        this.action = impression;
        monitor(params);
    }
}
