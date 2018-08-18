package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Graph;

import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

import static se306.travellingoliverproblem.TestParallelSolutions.testParallelScheduler;

public class TestParallelSolutionMass {

    /*
    These tests are to ensure that no concurrency issues are taking place
    This is insurance that there is not a case where an operation gets
    interrupted etc. These tests are not used for TravisCi, as they take
    a long time
     */
    private static ForkJoinPool forkJoinPool;
    @Test
    public void testParallel20Graph1000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564, 2);
        }
    }

    @Test
    public void testParallel20Graph1000Times3Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(3);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564, 2);
        }
    }

    @Test
    public void testParallel20Graph1000Times2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_20_Random.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 564, 2);
        }
    }


    @Test
    public void test8Random2ProcessorsParallel10000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_8_Random.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 581, 2);
        }
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel10000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 55, 2);
        }
    }

    @Test
    public void test9SeriesParallel2ProcessorsParallel10000Times2Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(2);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_9_SeriesParallel.dot");
        for (int i = 0; i < 10000; i++) {
            testParallelScheduler(inputGraph, 55, 2);
        }
    }

    // Tests for the bad graph
    @Test
    public void test11NodesOutTree2ProcessorsParallel1000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_11_OutTree.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 350, 2);
        }
    }

    // Tests for the bad graph
    @Test
    public void test11NodesOutTreeParallel4ProcessorsParallel1000Times4Threads() throws FileNotFoundException {
        forkJoinPool = new ForkJoinPool(4);
        Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_11_OutTree.dot");
        for (int i = 0; i < 1000; i++) {
            testParallelScheduler(inputGraph, 350, 2);
        }
    }

}
