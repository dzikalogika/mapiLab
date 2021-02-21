package app.model.util;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

public class Alphabetic implements Action {
    char c = 'a';

    @Override
    public Subject play(Subject subject) {
        return Suite.inset(c++, subject);
    }
}
