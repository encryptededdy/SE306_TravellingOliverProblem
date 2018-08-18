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
    public void testParallel20Graph4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        testParallelScheduler(inputGraph, 564);
    }

    @Test
    public void testParallel20Graph3Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(3);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        testParallelScheduler(inputGraph, 564);
    }

    @Test
    public void testParallel20Graph2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        testParallelScheduler(inputGraph, 564);
    }


    @Test
    public void test8Random2ProcessorsParallel4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_8_Random.dot");
        testParallelScheduler(inputGraph, 581);
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        testParallelScheduler(inputGraph, 55);
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        testParallelScheduler(inputGraph, 55);
    }

    static void testParallelScheduler(Graph inputGraph, int optimalTime) {
        Scheduler parallelScheduler = new ParallelScheduler(inputGraph, 2, false, true);
        TestOptimalSolutions.checkValidAndOptimal(parallelScheduler.getBestSchedule(), optimalTime);
        BranchAndBoundRecursiveAction.reset();
    }

}
