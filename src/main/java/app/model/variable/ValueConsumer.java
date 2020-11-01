package app.model.variable;

interface ValueConsumer {

    boolean press(Fun fun);
    void set(Object value, Fun fun);
    void attachInput(Fun fun);
}
