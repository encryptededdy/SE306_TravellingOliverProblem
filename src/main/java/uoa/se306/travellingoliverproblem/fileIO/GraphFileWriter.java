package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.io.File;
import java.io.FileNotFoundException;

public interface GraphFileWriter {
    void createFile(File file);
    boolean writeFile(Schedule schedule) throws FileNotFoundException;
}
