package app.model;

import suite.suite.Subject;

@FunctionalInterface
public interface Host {
    Subject order(Subject trade);
}