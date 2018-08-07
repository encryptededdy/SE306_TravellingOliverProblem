package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.fileIO.InvalidFileFormatException;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TestDotReader {
    @Test
    public void testReadInput1() throws FileNotFoundException {
        File inputFile = new File("testInput/unitTestInput1.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        Graph output = reader.readFile();

        assertEquals(output.getStartingNodes().size(), 1); // Only one starting node (0)

        Node startingNode = output.getStartingNodes().iterator().next();

        assertEquals(startingNode.toString(), "0"); // check starting node

        // Build graph manually for comparison
        Node node0 = new Node("0", 4, 0);
        Node node1 = new Node("1", 2, 1);
        Node node2 = new Node("2", 2, 2);
        Node node3 = new Node("3", 2, 3);

        node0.addChild(node1, 1);
        node0.addChild(node2, 1);
        node0.addChild(node3, 3);

        node1.addParent(node0, 1);
        node2.addParent(node0, 1);
        node3.addParent(node0, 3);

        assertEquals(node0, startingNode);

        // Check its children
        assertTrue(startingNode.getChildren().containsKey(node1));
        assertTrue(startingNode.getChildren().containsKey(node2));
        assertTrue(startingNode.getChildren().containsKey(node3));

        // Check weights of edges
        assertEquals(startingNode.getChildren().get(node1), new Integer(1));
        assertEquals(startingNode.getChildren().get(node2), new Integer(1));
        assertEquals(startingNode.getChildren().get(node3), new Integer(3));

        // Check getAllNodes
        assertTrue(output.getAllNodes().containsAll(Arrays.asList(node0, node1, node2, node3)));

    }

    @Test
    public void testReadInput2Cycle() throws FileNotFoundException {
        // Read a file with a cycle (so no start point)
        File inputFile = new File("testInput/unitTestInput2.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        try {
            Graph output = reader.readFile();
            fail();
        } catch (InvalidFileFormatException ife) {
            // Pass! Exception thrown!
        }
    }
}
