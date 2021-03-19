package app;

import app.model.*;

import java.util.Objects;

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
        var txt = text().setText("text")
                .setHReference(ColorText.HorizontalReference.RIGHT)
                .setVReference(ColorText.VerticalReference.BOTTOM);
        txt.position().let(mouse.getPosition().or(Point.zero()));
        txt.text().let(mouse.getPosition().per(Objects::toString));
        show(txt);
    }

    @Override
    public void update() {
    }
}