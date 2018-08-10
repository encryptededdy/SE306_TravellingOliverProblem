package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class TestParallelSolutions {

    @Test
    public void testSingleNode() throws FileNotFoundException {
    for (int i = 0; i < 1000; i++) {
        new TestOptimalSolutions().testSingleNode(); // TODO replace with parallel implementation
    }
    }
}
