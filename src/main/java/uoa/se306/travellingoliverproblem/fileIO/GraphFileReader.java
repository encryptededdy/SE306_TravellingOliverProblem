package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;

public interface GraphFileReader {
    void openFile(File file) throws FileNotFoundException;
    Graph readFile();
}
