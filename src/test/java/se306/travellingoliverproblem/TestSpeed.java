package se306.travellingoliverproblem;

import org.junit.Before;
import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.scheduler.AStarSearchScheduler;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;
import uoa.se306.travellingoliverproblem.scheduler.ParallelScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;
import uoa.se306.travellingoliverproblem.scheduler.parallel.BranchAndBoundRecursiveAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.ForkJoinPool;

import static se306.travellingoliverproblem.TestOptimalSolutions.getGraphFromInput;

public class TestSpeed {

    @Before
    public void setUpParallelisation() {
        new ForkJoinPool(4);
    }

    @Test
    public void compareSpeed2ProcessorsInput3() throws FileNotFoundException {
        test3SchedulerSpeeds(2, "input3.dot", 26);
    }

    @Test
    public void compareSpeed4ProcessorsInput3() throws FileNotFoundException {
        test3SchedulerSpeeds(4, "input3.dot", 26);
    }

    @Test
    public void compareSpeed2ProcessorsInput10() throws FileNotFoundException {
        test3SchedulerSpeeds(2, "Nodes_10_Random.dot", 50);
    }

    @Test
    public void compareSpeed4ProcessorsInput10() throws FileNotFoundException {
        test3SchedulerSpeeds(4, "Nodes_10_Random.dot", 50);
    }

    private void test3SchedulerSpeeds(int amtOfProcessors, String fileName, int optimalTime) throws FileNotFoundException {
        Graph graph = getGraphFromInput(fileName);

        long startTime = System.nanoTime();
        Scheduler DFSScheduler = new DFSScheduler(graph, amtOfProcessors, false);
        TestOptimalSolutions.checkValidAndOptimal(DFSScheduler.getBestSchedule(), optimalTime);
        long endTime = System.nanoTime();
        long DFSSchedulerTime = endTime - startTime;
        System.out.println("DFS Took " + (endTime - startTime) / 1000000 + " ms");

        long startTime2 = System.nanoTime();
        Scheduler AStarScheduler = new AStarSearchScheduler(graph, amtOfProcessors, false);
        TestOptimalSolutions.checkValidAndOptimal(AStarScheduler.getBestSchedule(), optimalTime);
        long endTime2 = System.nanoTime();
        long AStarSchedulerTime = endTime2 - startTime2;
        System.out.println("AStar Took " + (endTime2 - startTime2) / 1000000 + " ms");

        long startTime3 = System.nanoTime();
        BranchAndBoundRecursiveAction.graph = graph;
        Scheduler ParallelScheduler = new ParallelScheduler(graph, amtOfProcessors, false, true);
        TestOptimalSolutions.checkValidAndOptimal(ParallelScheduler.getBestSchedule(), optimalTime);
        long endTime3 = System.nanoTime();
        long ParallelSchedulerTime = endTime3 - startTime3;
        System.out.println("Parallel Took " + (endTime3 - startTime3) / 1000000 + " ms");
        BranchAndBoundRecursiveAction.reset();

        if (DFSSchedulerTime > AStarSchedulerTime && DFSSchedulerTime > ParallelSchedulerTime) {
            System.out.println("AStar was faster on " + amtOfProcessors + " processors for " + fileName);
        } else if (AStarSchedulerTime > ParallelSchedulerTime){
            System.out.println("DFS was faster on " + amtOfProcessors + " processors for " + fileName);
        } else {
            System.out.println("Parallel was faster on " + amtOfProcessors + " processors for " + fileName);
        }
    }
}
