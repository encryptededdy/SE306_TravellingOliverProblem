package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.Map;

public class DotStandardOutput implements ScheduleOutputter {
    protected StringBuilder dotOutputStringBuilder;
    private Graph graph;
    private Schedule schedule;

    public DotStandardOutput() { }

    public DotStandardOutput(Graph graph, Schedule schedule) {
        String outputGraphName = graph.getGraphName().substring(0,1).toUpperCase() + graph.getGraphName().substring(1);
        this.dotOutputStringBuilder = new StringBuilder("digraph \"output" + outputGraphName + "\" {");
        this.graph = graph;
        this.schedule = schedule;
    }

    @Override
    public void outputSchedule() {
        ScheduledProcessor[] processors =  this.schedule.getProcessors();

        this.graph.getAllNodes().forEach(node->{
            for (int i = 0; i < processors.length; i++) {
                ScheduledProcessor processor = processors[i];
                if (processor.contains(node)) {
                    addScheduleEntryToOutput(processor.getEntry(node), i);
                }
            }
            this.addEdgesToOutput(node);
        });
        this.dotOutputStringBuilder.append("\n}"); // Adding Final Line

        System.out.print(this.dotOutputStringBuilder.toString());
    }

    private void addEdgesToOutput(Node node) {
        Map<Node, Integer> children = node.getChildren();

        children.forEach((childNode, edgeWeight)->{
            this.dotOutputStringBuilder.append("\n\t");
            this.dotOutputStringBuilder.append(node);
            this.dotOutputStringBuilder.append(" -> ");
            this.dotOutputStringBuilder.append(childNode);
            this.dotOutputStringBuilder.append("\t");

            this.dotOutputStringBuilder.append("[ Weight=" + edgeWeight + "];");

        });
    }

    private void addScheduleEntryToOutput(ScheduleEntry entry, int processor) {
        Node node = entry.getNode();

        this.dotOutputStringBuilder.append("\n\t");
        this.dotOutputStringBuilder.append(node);
        this.dotOutputStringBuilder.append("\t\t");
        this.dotOutputStringBuilder.append("[ Weight=" + node.getCost() + ",");
        this.dotOutputStringBuilder.append("Start=" + entry.getStartTime() + ",");
        this.dotOutputStringBuilder.append("Processor=" + processor + "];");
    }
}
