package app.old;

import suite.suite.Subject;

public abstract class AbstractSketch<P> {

    Subject $;

    public AbstractSketch(Subject $) {
        this.$ = $;
    }

    public Subject subject() {
        return $;
    }

    abstract AbstractSketch<P> put(Object key, Object value);
    abstract P paint();
}
