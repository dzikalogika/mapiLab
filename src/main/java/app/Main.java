package app;

import app.model.*;

import static suite.suite.$uite.$;
import static suite.suite.Suite.join;

public class Main extends Window {

    public static void main(String[] args) {
        Window.play(join(
                $(Window.class, Main.class),
                $("r", .2f),
                $("g", .5f),
                $("b", .4f)
        ));
    }

    public void setup() {

    }

    @Override
    public void update() {

    }
}