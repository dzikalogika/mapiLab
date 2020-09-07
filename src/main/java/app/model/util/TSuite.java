package app.model.util;

import suite.suite.util.Fluid;

import java.util.Arrays;

public class TSuite {

    public static Fluid params(Object ... objects) {
        return Fluid.engage(Generator.integers(), Arrays.asList(objects));
    }
}
