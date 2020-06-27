package app.model.variable;

import suite.suite.Subject;
import suite.suite.action.Action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiPredicate;

public class StoryVar extends Var<Object> {

    private Var<?> storyteller;
    private final Queue<Object> story;

    public StoryVar(Object title, Object[] intro) {
        story = new LinkedList<>();
        story.add(title);
        story.addAll(Arrays.asList(intro));
    }

    @Override
    public boolean detection() {
        if(detectionFlag) {
            detectionFlag = false;
            return true;
        }
        return false;
    }

    @Override
    public Object get() {
        return story.peek();
    }

    @Override
    public void raiseDetectionFlag() {
        story.remove();
        story.add(storyteller.value);
        if(!detectionFlag) {
            subjects.front().keys().filter(Monitor.class).forEach(Monitor::raiseSuspicionFlag);
            detectionFlag = true;
        }
    }

    @Override
    public void raiseSuspicionFlag() {
        raiseDetectionFlag();
    }

    @Override
    public void recipe(Subject params, Action action) {}

    public void setStoryteller(Var<?> storyteller) {
        this.storyteller = storyteller;
    }

    public boolean introduced() {
        return storyteller != null;
    }
}
