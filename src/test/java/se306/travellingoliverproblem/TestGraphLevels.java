package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.fileIO.InvalidFileFormatException;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

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
            assertEquals(node.getLevel(), new Integer(2));
        }

        assertEquals(output.getLevels(), 2); // Check level of graph is correct
    }

    @Test
    public void testGraphLevelsLargeGraph() throws FileNotFoundException {
        File inputFile = new File("testInput/unitTestInput1.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        Graph output = reader.readFile();

        Node startingNode = output.getStartingNodes().iterator().next();

        assertEquals(startingNode.getLevel(), new Integer(1));

        assertEquals(output.getLevels(), 2); // Check level of graph is correct
    }

}
