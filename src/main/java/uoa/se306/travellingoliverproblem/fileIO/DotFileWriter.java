package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DotFileWriter extends DotStandardOutput implements ScheduleOutputter {
    private String fileName;



    public DotFileWriter(Graph graph, Schedule schedule, String fileName) {
        super(graph, schedule);
        this.fileName = fileName;
    }

    @Override
    public void outputSchedule() {
        super.outputSchedule();
        this.writeFile();
    }

    private void writeFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
            writer.write(this.dotOutputStringBuilder.toString());

            writer.close();
            System.out.println("");
            System.out.println("File Written!");
            //System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't create/write to file: " + this.fileName +"\nType -h or --help for help.");
            //System.exit(1);
        }
    }
}
