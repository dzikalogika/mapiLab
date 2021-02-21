package app.model.util;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

public class Numeric implements Action {
    int n = 0;

    @Override
    public Subject play(Subject subject) {
        return Suite.inset(n++, subject);
    }
}
