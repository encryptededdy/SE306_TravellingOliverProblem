package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.io.File;
import java.io.FileNotFoundException;

public class DotWriter implements GraphFileWriter {

    @Override
    public void createFile(File file) {
    }

    @Override
    public boolean writeFile(Schedule schedule) throws FileNotFoundException {
        return false;
    }
}
