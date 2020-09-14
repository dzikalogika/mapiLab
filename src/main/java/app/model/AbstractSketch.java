package app.model;

import suite.suite.Slot;
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
    public T set(Object key, Object value) {
        super.set(key, value);
        return self();
    }

    @Override
    public T put(Object element) {
        super.put(element);
        return self();
    }

    @Override
    public T put(Object key, Object value) {
        super.put(key, value);
        return self();
    }

    @Override
    public T add(Object element) {
        super.add(element);
        return self();
    }

    @Override
    public T unset() {
        super.unset();
        return self();
    }

    @Override
    public T unset(Object key) {
        super.unset(key);
        return self();
    }

    @Override
    public T unset(Object key, Object value) {
        super.unset(key, value);
        return self();
    }

    @Override
    public T unsetAt(Slot slot) {
        super.unsetAt(slot);
        return self();
    }

    @Override
    public T inset(Iterable<Subject> iterable) {
        super.inset(iterable);
        return self();
    }

    @Override
    public T input(Iterable<Subject> iterable) {
        super.input(iterable);
        return self();
    }

    @Override
    public T setAt(Slot slot, Object element) {
        super.setAt(slot, element);
        return self();
    }

    @Override
    public T setAt(Slot slot, Object key, Object value) {
        super.setAt(slot, key, value);
        return self();
    }

    @Override
    public T putAt(Slot slot, Object element) {
        super.putAt(slot, element);
        return self();
    }

    @Override
    public T putAt(Slot slot, Object key, Object value) {
        super.putAt(slot, key, value);
        return self();
    }

    @Override
    public T addAt(Slot slot, Object element) {
        super.addAt(slot, element);
        return self();
    }

    @Override
    public T setAll(Iterable<Object> iterable) {
        super.setAll(iterable);
        return self();
    }

    @Override
    public T putAll(Iterable<Object> iterable) {
        super.putAll(iterable);
        return self();
    }

    @Override
    public T addAll(Iterable<Object> iterable) {
        super.addAll(iterable);
        return self();
    }

    @Override
    public T set() {
        return self();
    }

    @Override
    public T put() {
        return self();
    }
}
