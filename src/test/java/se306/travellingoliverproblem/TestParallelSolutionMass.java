package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Graph;

import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

import static se306.travellingoliverproblem.TestParallelSolutions.testParallelScheduler;

public class TestParallelSolutionMass {

    private static ForkJoinPool forkJoinPool;
    @Test
    public void testParallel20Graph1000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564);
        }
    }

    @Test
    public void testParallel20Graph1000Times3Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(3);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564);
        }
    }

    @Test
    public void testParallel20Graph1000Times2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564);
        }
    }


    @Test
    public void test8Random2ProcessorsParallel10000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_8_Random.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 581);
        }
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel10000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 55);
        }
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel10000Times2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 55);
        }
    }

}
