package app.model.var;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PreservativeVar<T> implements Source<T>{

    public interface Guard {
        boolean update();
    }

    public static abstract class CacheGuard implements Guard {
        Supplier<?> supplier;
        Object cache;

        public CacheGuard(Supplier<?> supplier, Object cache) {
            this.supplier = supplier;
            this.cache = cache;
        }
    }

    public static class IdentityGuard extends CacheGuard {

        public IdentityGuard(Supplier<?> supplier, Object cache) {
            super(supplier, cache);
        }

        @Override
        public boolean update() {
            Object o = supplier.get();
            if(o == cache) {
                return false;
            } else {
                cache = o;
                return true;
            }
        }
    }

    public static class EqualityGuard extends CacheGuard {

        public EqualityGuard(Supplier<?> supplier, Object cache) {
            super(supplier, cache);
        }

        @Override
        public boolean update() {
            Object o = supplier.get();
            if(Objects.equals(o, cache)) {
                cache = o;
                return false;
            } else {
                cache = o;
                return true;
            }
        }
    }

    private final List<Guard> guards = new LinkedList<>();
    private Supplier<T> sup;
    private T cache;
    private boolean cached;

    public PreservativeVar() {
        this.cached = false;
    }

    public PreservativeVar(Supplier<T> sup) {
        this.sup = sup;
        this.cached = false;
    }

    public PreservativeVar(Supplier<T> sup, Supplier<?> ... guards) {
        this.sup = sup;
        this.cached = false;
        for (var g : guards) {
            this.guards.add(new EqualityGuard(g, null));
        }
    }

    private void update() {
        if(cached) {
            for (Guard guard : guards) {
                if(guard.update()) cached = false;
            }
        }
    }

    @Override
    public boolean present() {
        update();
        return (cached && cache != null) || sup != null;
    }

    @Override
    public T getOr(T reserve) {
        update();
        if(!cached) {
            if(sup == null) return reserve;
            cache = sup.get();
            cached = true;
        }
        return cache != null ? cache : reserve;
    }

    @Override
    public T get() {
        update();
        if(!cached) {
            if(sup == null) return null;
            cache = sup.get();
            cached = true;
        }
        return cache;
    }

    public PreservativeVar<T> let(Supplier<T> sup) {
        this.sup = sup;
        this.cached = false;
        return this;
    }
}
