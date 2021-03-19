package app.model;

import suite.suite.Subject;
import suite.suite.action.Action;

import static suite.suite.$uite.$;

public abstract class Component {

    private Host host;

    public Component(Host host) {
        this.host = host;
    }

    protected<T> T order(Class<T> trade) {
        return host.order($(trade)).as(trade);
    }

    protected Subject order(Subject trade) {
        return host.order(trade);
    }

    protected Thread order(Subject trade, Action callback) {
        Thread thread = new Thread(() -> callback.play(host.order(trade)));
        thread.start();
        return thread;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
