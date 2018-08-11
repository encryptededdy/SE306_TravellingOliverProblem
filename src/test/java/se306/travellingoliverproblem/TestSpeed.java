package se306.travellingoliverproblem;

import org.junit.Before;
import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.scheduler.AStarSearchScheduler;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

public class TestSpeed {

    Graph input3Graph, input10Graph;

    @Before
    public void setupNodes() throws FileNotFoundException {
        input3Graph = getGraphFromInput("input3.dot");
        input10Graph = getGraphFromInput("Nodes_10_Random.dot");
        //input10Graph = getGraphFromInput("Nodes_11_OutTree.dot");
    }

    @Test
    public void compareSpeed2ProcessorsInput3(){
        Scheduler DFSScheduler3_2 = new DFSScheduler(input3Graph, 2);
        Scheduler AStarScheduler3_2 = new AStarSearchScheduler(input3Graph, 2);

        long startTime = System.nanoTime();
        DFSScheduler3_2.getBestSchedule();
        long endTime = System.nanoTime();
        long DFSScheduler3_2time = endTime - startTime;
        System.out.println("DFS Took " + (endTime - startTime) / 1000000 + " ms");

        long startTime2 = System.nanoTime();
        AStarScheduler3_2.getBestSchedule();
        long endTime2 = System.nanoTime();
        long AStarScheduler3_2time = endTime2 - startTime2;
        System.out.println("AStar Took " + (endTime2 - startTime2) / 1000000 + " ms");

        if (DFSScheduler3_2time > AStarScheduler3_2time) {
            System.out.println("AStar was faster on 2 processors for input 3");
        } else {
            System.out.println("DFS was faster on 2 processors for input 3");
        }
    }

    @Test
    public void compareSpeed2ProcessorsInput10(){
        Scheduler DFSScheduler10_2 = new DFSScheduler(input10Graph, 2);
        Scheduler AStarScheduler10_2 = new AStarSearchScheduler(input10Graph, 2);

        long startTime = System.nanoTime();
        DFSScheduler10_2.getBestSchedule();
        long endTime = System.nanoTime();
        long DFSScheduler10_2time = endTime - startTime;
        System.out.println("DFS Took " + (endTime - startTime) / 1000000 + " ms");

        long startTime2 = System.nanoTime();
        AStarScheduler10_2.getBestSchedule();
        long endTime2 = System.nanoTime();
        long AStarScheduler10_2time = endTime2 - startTime2;
        System.out.println("AStar Took " + (endTime2 - startTime2) / 1000000 + " ms");

        if(DFSScheduler10_2time > AStarScheduler10_2time){
            System.out.println("AStar was faster on 2 processors for input 10");
        } else{
            System.out.println("DFS was faster on 2 processors for input 10");
        }
    }

    @Test
    public void compareSpeed4ProcessorsInput3(){
        Scheduler DFSScheduler3_4 = new AStarSearchScheduler(input3Graph, 4);
        Scheduler AStarScheduler3_4 = new AStarSearchScheduler(input3Graph, 4);

        long startTime = System.nanoTime();
        DFSScheduler3_4.getBestSchedule();
        long endTime = System.nanoTime();
        long DFSScheduler3_4time = endTime - startTime;
        System.out.println("DFS Took " + (endTime - startTime) / 1000000 + " ms");

        long startTime2 = System.nanoTime();
        AStarScheduler3_4.getBestSchedule();
        long endTime2 = System.nanoTime();
        long AStarScheduler3_4time = endTime2 - startTime2;
        System.out.println("AStar Took " + (endTime2 - startTime2) / 1000000 + " ms");

        if(DFSScheduler3_4time > AStarScheduler3_4time){
            System.out.println("AStar was faster on 4 processors on input 3");
        } else{
            System.out.println("DFS was faster on 4 processors on input 3");
        }
    }

    @Test
    public void compareSpeed4ProcessorsInput10(){
        Scheduler AStarScheduler10_4 = new AStarSearchScheduler(input10Graph, 8);
        Scheduler DFSScheduler10_4 = new AStarSearchScheduler(input10Graph, 8);

        long startTime = System.nanoTime();
        DFSScheduler10_4.getBestSchedule();
        long endTime = System.nanoTime();
        long DFSScheduler10_4time = endTime - startTime;
        System.out.println("DFS Took " + (endTime - startTime) / 1000000 + " ms");

        long startTime2 = System.nanoTime();
        AStarScheduler10_4.getBestSchedule();
        long endTime2 = System.nanoTime();
        long AStarScheduler10_4time = endTime2 - startTime2;
        System.out.println("AStar Took " + (endTime2 - startTime2) / 1000000 + " ms");

        if(DFSScheduler10_4time > AStarScheduler10_4time){
            System.out.println("AStar was faster on 4 processors on input 10");
        } else{
            System.out.println("DFS was faster on 4 processors on input 10");
        }

    }


    private Graph getGraphFromInput(String fileName) throws FileNotFoundException {
        File inputFile = new File("testInput/" + fileName);
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        return reader.readFile();
    }
}
