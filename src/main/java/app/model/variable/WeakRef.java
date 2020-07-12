package app.model.variable;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class WeakRef<T> extends WeakReference<T> {

    public WeakRef(T referent) {
        super(referent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(get());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WeakRef && Objects.equals(get(), ((WeakRef<?>) obj).get());
    }
}
