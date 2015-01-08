package org.jboss.tools.vwatch.counter;

import org.jboss.tools.vwatch.model.Issue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jpeterka on 8.1.15.
 */
public class IssueCounter {
    private Map<Class<? extends Issue>, Integer> counter = new HashMap<Class<? extends Issue>, Integer>();
    private static IssueCounter instance = null;

    private IssueCounter() {

    }

    public static IssueCounter getInstance() {
        if (instance == null) {
            instance = new IssueCounter();
        }
        return instance;

    }

    public void setValue(Class<? extends Issue> type, int count) {
        counter.put(type,count);
    }


    public int getCount(Class<? extends Issue> type) {
        return counter.get(type);
    }

}
