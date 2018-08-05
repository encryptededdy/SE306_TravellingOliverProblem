package se306.travellingoliverproblem;

import org.junit.Test;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.fileIO.InvalidFileFormatException;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileNotFoundException;

public class TestOutputFile {
    @Test
    public void testReadInput1() throws FileNotFoundException {
        File inputFile = new File("testInput/unitTestInput1.dot");
        GraphFileReader reader = new DotReader();
        reader.openFile(inputFile);

    }
}
