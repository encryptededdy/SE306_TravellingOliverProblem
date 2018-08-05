package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

/*
A ScheduledEntry is a node that has been scheduled on a processor
Note this is immutable and thus has no setters, and doesn't need to be deep copied
 */
public class ScheduleEntry implements Comparable<ScheduleEntry> {
    private int startTime;
    private Node node;

    // Constructor
    public ScheduleEntry(int startTime, Node node) {
        this.startTime = startTime;
        this.node = node;
    }

    public boolean equals(Node node) {
        return this.node.equals(node);
    }

    // Returns the start time of the ScheduledEntry
    public int getStartTime() { return startTime; }

    // Returns the run time of the ScheduledEntry
    public int getLength() { return node.getCost(); }

    // Returns the time at which the ScheduledEntry ended on the processor
    public int getEndTime() { return startTime + node.getCost(); }

    // Returns the node the schedule entry is referring to
    public Node getNode() {return this.node;}

    // Node name followed by start time
    @Override
    public String toString() {
        return node.toString();
    }

    // Compare ScheduleEntries based on their end time
    @Override
    public int compareTo(ScheduleEntry o) {
        return Integer.compare(startTime + node.getCost(), o.startTime + o.node.getCost());
    }
}
