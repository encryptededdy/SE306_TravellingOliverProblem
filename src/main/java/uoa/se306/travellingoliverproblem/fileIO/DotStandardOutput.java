package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.Main;
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

        String outputGraphName;
        if(graph.getGraphName() == null && Main.outputFileName.equals(".dot")){
            outputGraphName = "";
        } else{
            outputGraphName = graph.getGraphName();
            if(outputGraphName == null) {
                // get the fine name without the .dot extension
                outputGraphName = Main.outputFileName.substring(0, (Main.outputFileName.length() - 4));
                // capitalise start of string
                outputGraphName = outputGraphName.substring(0, 1).toUpperCase() + outputGraphName.substring(1);
            } else {
                outputGraphName = outputGraphName.substring(0, 1).toUpperCase() + graph.getGraphName().substring(1);
                outputGraphName = removeQuotes(outputGraphName);
            }
        }


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
        });
        for (Node node: graph.getAllNodes()){
            addEdgesToOutput(node);
        }
        this.dotOutputStringBuilder.append("\n}"); // Adding Final Line

        //System.out.print(this.dotOutputStringBuilder.toString());
    }

    private void addEdgesToOutput(Node node) {
        Map<Node, Integer> children = node.getChildren();

        children.forEach((childNode, edgeWeight)->{
            this.dotOutputStringBuilder.append("\n\t");
            this.dotOutputStringBuilder.append(node);
            this.dotOutputStringBuilder.append(" -> ");
            this.dotOutputStringBuilder.append(childNode);
            this.dotOutputStringBuilder.append("\t");

            this.dotOutputStringBuilder.append("[Weight=" + edgeWeight + "];");

        });
    }

    private void addScheduleEntryToOutput(ScheduleEntry entry, int processor) {
        Node node = entry.getNode();
        processor++;

        this.dotOutputStringBuilder.append("\n\t");
        this.dotOutputStringBuilder.append(node);
        this.dotOutputStringBuilder.append("\t\t");
        this.dotOutputStringBuilder.append("[Weight=" + node.getCost() + ",");
        this.dotOutputStringBuilder.append("Start=" + entry.getStartTime() + ",");
        this.dotOutputStringBuilder.append("Processor=" + processor + "];");
    }

    private String removeQuotes(String title){
        if(title.charAt(0) == '\"'){
            title = title.substring(1);
        }
        if(title.charAt(title.length() - 1) == '\"'){
            title = title.substring(0, title.length() - 1);
        }
        return title;
    }
}
