package app.model.util;

import suite.suite.util.Slime;
import suite.suite.util.Wave;

public class Generator {

    public static Slime<String> alphas() {
        return () -> new Wave<>() {
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

    public static Slime<Integer> integers() {
        return () -> new Wave<>() {
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
