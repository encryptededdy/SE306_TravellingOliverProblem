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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestGraphLevels {

    @Test
    public void testGraphLevelsInput1() throws FileNotFoundException {
        File inputFile = new File("testInput/unitTestInput1.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        Graph output = reader.readFile();

        assertEquals(output.getStartingNodes().size(), 1); // Only one starting node (0)

        Node startingNode = output.getStartingNodes().iterator().next();

        assertEquals(startingNode.getLevel(), new Integer(1));

        for (Node node: startingNode.getChildren().keySet()) {
            assertEquals(node.getLevel(), new Integer(2));
        }

    }

}
