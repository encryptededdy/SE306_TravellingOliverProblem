package se306.travellingoliverproblem;

import org.junit.Before;
import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

public class TestNode {
    Graph input3Graph, input10Graph;

    @Before
    public void setupNodes() throws FileNotFoundException {
        input3Graph = getGraphFromInput("input3.dot");
        input10Graph = getGraphFromInput("Nodes_10_Random.dot");
    }

    @Test
    public void testBottomLevelInput10(){
        Node node = input10Graph.getStartingNodes().iterator().next();
        assertEquals(29, node.getBottomLevel());
    }

    @Test
    public void testBottomLevelInput3(){
        Node node = input3Graph.getStartingNodes().iterator().next();
        assertEquals(21, node.getBottomLevel());
    }


    private Graph getGraphFromInput(String fileName) throws FileNotFoundException {
        File inputFile = new File("testInput/" + fileName);
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);
        return reader.readFile();
    }
}
