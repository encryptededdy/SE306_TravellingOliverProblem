package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.scheduler.ParallelScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;
import uoa.se306.travellingoliverproblem.scheduler.parallel.BranchAndBoundRecursiveAction;

import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

public class TestParallelSolutions {


    private static ForkJoinPool forkJoinPool;
    @Test
    public void testParallel20Graph1000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4); // TODO, Doesnt work because of statics
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph);
        }
    }

    @Test
    public void testParallel20Graph1000Times3Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(3); // TODO, Doesnt work because of statics
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph);
        }
    }

    @Test
    public void testParallel20Graph1000Times2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2); // TODO, Doesnt work because of
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph);
        }
    }

    private void testParallelScheduler(Graph inputGraph) {
        Scheduler parallelScheduler = new ParallelScheduler(inputGraph, 2, false, true);
        TestOptimalSolutions.checkValidAndOptimal(parallelScheduler.getBestSchedule(), 564);
        BranchAndBoundRecursiveAction.reset();
    }
}
