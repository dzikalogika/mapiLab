package app.model.trade;

import suite.suite.Subject;

public abstract class Agent extends Component implements Host {

    public Agent(Host host) {
        super(host);
    }

    @Override
    public Subject order(Subject trade) {
        return getHost().order(trade);
    }
}
