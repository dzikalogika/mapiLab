package app;

import app.model.*;
import app.model.component.ColorText;
import app.model.font.FontManager;
import app.model.graphic.LoadedImage;
import app.model.image.Image;
import app.model.image.ImageManager;
import app.model.window.Window;
import suite.suite.Subject;

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

        var txt1 = text().setPosition(new Point(100, 30));
        txt1.text().let(txt.width().per(Objects::toString));
        show(txt1);

        var img = image().setImage(new Image("awesomeface.png"));//.setWidth(800).setHeight(100);
        show(img);
    }

    @Override
    public void update() {
    }

    FontManager fontManager = new FontManager();
    ImageManager imageManager = new ImageManager();

    @Override
    public Subject order(Subject trade) {
        if(trade.is(Class.class)) {
            Class<?> type = trade.asExpected();
            if(type.equals(FontManager.class)) {
                return $(fontManager);
            } else if(type.equals(ImageManager.class)) {
                return $(imageManager);
            }
        }
        return super.order(trade);
    }
}