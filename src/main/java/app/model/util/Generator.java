package app.model.util;

import suite.suite.util.FluidIterator;
import suite.suite.util.FluidObject;

public class Generator {

    public static FluidObject<String> alpha() {
        return () -> new FluidIterator<>() {
            char ch = 'a';

            @Override
            public boolean hasNext() {
                return ch <= 'z';
            }

            @Override
            public String next() {
                return "" + ch++;
            }
        };
    }

    public static FluidObject<Integer> integers() {
        return () -> new FluidIterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < Integer.MAX_VALUE;
            }

            @Override
            public Integer next() {
                return i++;
            }
        };
    }
}
