package app.model;

import app.model.variable.Playground;

public abstract class Component extends Playground {
    public static final Object STATES = new Object();

    abstract void print();
}
