package app.model;

import suite.suite.SolidSubject;
import suite.suite.Subject;

public abstract class AbstractSketch<T extends Subject> extends SolidSubject {

    public static final Object MODEL = new Object();

    public AbstractSketch(Subject subject) {
        super(subject);
    }

    @SuppressWarnings("unchecked")
    public T self() {
        return (T)this;
    }

    @Override
    public T set(Object element) {
        super.set(element);
        return self();
    }

    @Override
    public T exactSet(Object aim, Object element) {
        super.exactSet(aim, element);
        return self();
    }

    @Override
    public T inset(Object element, Subject $set) {
        super.inset(element, $set);
        return self();
    }

    @Override
    public T exactInset(Object aim, Object element, Subject $set) {
        super.exactInset(aim, element, $set);
        return self();
    }
    
    @Override
    public T shift(Object out, Object in) {
        super.shift(out, in);
        return self();
    }

    @Override
    public T unset() {
        super.unset();
        return self();
    }

    @Override
    public T unset(Object element) {
        super.unset(element);
        return self();
    }

    @Override
    public T alter(Iterable<? extends Subject> iterable) {
        super.alter(iterable);
        return self();
    }

    @Override
    public T exactAlter(Object sequent, Iterable<? extends Subject> iterable) {
        super.exactAlter(sequent, iterable);
        return self();
    }

    @Override
    public T set() {
        super.set();
        return self();
    }

    @Override
    public T set(Object key, Object value, Object... rest) {
        super.set(key, value, rest);
        return self();
    }

    @Override
    public T exactSet(Object aim, Object key, Object value, Object... rest) {
        super.exactSet(aim, key, value, rest);
        return self();
    }

    @Override
    public T put(Object element) {
        super.put(element);
        return self();
    }

    @Override
    public T exactPut(Object aim, Object element) {
        super.exactPut(aim, element);
        return self();
    }

    @Override
    public T put(Object value, Object... rest) {
        super.put(value, rest);
        return self();
    }

    @Override
    public T exactPut(Object aim, Object value, Object... rest) {
        super.exactPut(aim, value, rest);
        return self();
    }

    @Override
    public T input(Subject $set) {
        super.input($set);
        return self();
    }

    @Override
    public T exactInput(Object target, Subject $set) {
        super.exactInput(target, $set);
        return self();
    }

    @Override
    public T reset(Object element) {
        super.reset(element);
        return self();
    }

    @Override
    public T merge(Subject $tree) {
        super.merge($tree);
        return self();
    }

    @Override
    public T setAll(Iterable<?> iterable) {
        super.setAll(iterable);
        return self();
    }

    @Override
    public T putAll(Iterable<?> iterable) {
        super.putAll(iterable);
        return self();
    }

    @Override
    public T unsetAll(Iterable<?> iterable) {
        super.unsetAll(iterable);
        return self();
    }
}
