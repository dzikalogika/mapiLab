package app.model;

import suite.suite.Subject;

import static suite.suite.$uite.$;

public class Coordinator {

    Subject $coordinates = $();

    public void distribute(Rectangle r, Point p) {
        $coordinates.put(r, p);
    }
}
