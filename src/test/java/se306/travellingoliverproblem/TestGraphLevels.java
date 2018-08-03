package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestGraphLevels {

    @Test
    public void testGraphLevelsInput1() throws FileNotFoundException {
        File inputFile = new File("testInput/unitTestInput1.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        Graph output = reader.readFile();

        assertEquals(output.getStartingNodes().size(), 1);

        Node startingNode = output.getStartingNodes().iterator().next();

        assertEquals(startingNode.getLevel(), new Integer(1));

        for (Node node: startingNode.getChildren().keySet()) {
            // Check all children nodes of a starting node are at level 2
            assertEquals(new Integer(2), node.getLevel());
        }

        assertEquals(new Integer(2), output.getLevels()); // Check level of graph is correct
    }

    @Test
    public void testGraphLevelsLargeGraph() throws FileNotFoundException {
        File inputFile = new File("testInput/Nodes_10_Random.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        Graph output = reader.readFile();
        Map<String, Integer> nodeLevels = new HashMap<>(); //Done on pencil and paper

        // All nodes added, which are calculated by hand
        nodeLevels.put("0", 1);
        nodeLevels.put("1", 1);
        nodeLevels.put("2", 2);
        nodeLevels.put("3", 3);
        nodeLevels.put("4", 2);
        nodeLevels.put("5", 2);
        nodeLevels.put("6", 3);
        nodeLevels.put("7", 4);
        nodeLevels.put("8", 5);
        nodeLevels.put("9", 6);

        Node startingNode = output.getStartingNodes().iterator().next();

        assertEquals(new Integer(1), startingNode.getLevel());
        assertEquals(new Integer(6), output.getLevels()); // Check level of graph is correct

        //Check that all nodes have the correct level
        for (Node node: output.getAllNodes()) {
            if (nodeLevels.containsKey(node.toString())) {
                assertEquals(nodeLevels.get(node.toString()), node.getLevel());
            } else {
                // If it's not contained obv something went wrong
                fail();
            }
        }
    }

}
