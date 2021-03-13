package app.model;

import suite.suite.Subject;

import static suite.suite.$uite.$;

public class Decorator {

    Subject $colors = $();

    public void paint(Rectangle r, Color c) {
        $colors.put(r, c);
    }
}
