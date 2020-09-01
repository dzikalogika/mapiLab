package app.model.util;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

public class Keys {

    public static Action alpha() {
        return new Action() {
            char ch = 'a';

            @Override
            public Subject play(Subject subject) {
                return Suite.set("" + ch++, subject.direct());
            }
        };
    }
}
