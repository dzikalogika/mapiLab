package app.model.variable;

interface ValueConsumer<T> {

    boolean press(Fun fun);
    void set(T value, Fun fun);
    void attachInput(Fun fun);
}
