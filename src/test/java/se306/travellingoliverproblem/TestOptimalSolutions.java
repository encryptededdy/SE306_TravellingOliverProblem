package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestOptimalSolutions {

    @Test
    public void testSingleNode() throws FileNotFoundException {
        Graph inputGraph = getGraphFromInput("onenode.dot");

        assertEquals(inputGraph.getStartingNodes().size(), 1); // Only one starting node (0)

        Node startingNode = inputGraph.getStartingNodes().iterator().next();

        assertEquals(startingNode.toString(), "a"); // check starting node

        Node nodeA = new Node("a", 1);
        assertEquals(nodeA, startingNode);

        Scheduler scheduler = new DFSScheduler(inputGraph, 1);
        checkValidAndOptimal(scheduler.getBestSchedule(), 1);
    }

    @Test
    public void testStraightLine() throws FileNotFoundException {
        Graph inputGraph = getGraphFromInput("straightline.dot");

        assertEquals(inputGraph.getStartingNodes().size(), 1); // Only one starting node (0)

        Scheduler scheduler = new DFSScheduler(inputGraph, 2);

        checkValidAndOptimal(scheduler.getBestSchedule(), 2);

    }

    @Test
    public void testTwoStartingNodes() throws FileNotFoundException {

        Graph inputGraph = getGraphFromInput("twostartingnodes.dot");
        // 2 starting nodes
        assertEquals(inputGraph.getStartingNodes().size(), 2);

        Scheduler scheduler = new DFSScheduler(inputGraph, 2);

       checkValidAndOptimal(scheduler.getBestSchedule(), 3);
    }

    @Test
    public void testSlotInTask() throws FileNotFoundException {

        Graph inputGraph = getGraphFromInput("slotintask.dot");
        // 2 starting nodes
        assertEquals(inputGraph.getStartingNodes().size(), 2);

        Scheduler scheduler = new DFSScheduler(inputGraph, 2);

        checkValidAndOptimal(scheduler.getBestSchedule(), 10);
    }


    private void checkValidAndOptimal(Schedule scheduleToCheck, int optimalTime) {
        //TODO implement this method
        assertTrue(scheduleToCheck.checkValidity());
        assertEquals(optimalTime, scheduleToCheck.getOverallTime());
    }

    private Graph getGraphFromInput(String fileName) throws FileNotFoundException {
        File inputFile = new File("testInput/checkOptimal/" + fileName);
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        return reader.readFile();
    }

}
