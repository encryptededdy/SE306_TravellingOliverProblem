package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.parallel.BranchAndBoundRecursiveAction;
import uoa.se306.travellingoliverproblem.scheduler.HybridScheduler;

import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertEquals;

public class TestParallelSolutions {


    public static ForkJoinPool forkJoinPool;
    @Test
    public void testParallel10Graph() throws FileNotFoundException {
    for (int i = 0; i < 1000; i++) {
            Graph inputGraph = TestOptimalSolutions.getGraphFromInput("Nodes_10_Random.dot");
            forkJoinPool = new ForkJoinPool(2); // TODO, Doesnt work because of statics
            HybridScheduler initialScheduler = new HybridScheduler(inputGraph, 2, true, 10);
            initialScheduler.getBestSchedule();
            BranchAndBoundRecursiveAction bab = new BranchAndBoundRecursiveAction(initialScheduler.getSchedules(), 2);
            BranchAndBoundRecursiveAction.graph = inputGraph;
            bab.invoke();
            TestOptimalSolutions.checkValidAndOptimal(bab.getBestSchedule(), 50);
    }
    }
}
