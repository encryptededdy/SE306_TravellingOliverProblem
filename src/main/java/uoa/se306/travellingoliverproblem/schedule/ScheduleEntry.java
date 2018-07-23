package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

public class ScheduleEntry implements Comparable<ScheduleEntry> {
    private int startTime;
    private int length;
    private Node node;

    public ScheduleEntry(int startTime, Node node) {
        this.startTime = startTime;
        this.node = node;
        this.length = node.getCost();
    }

    public int getStartTime() {
        return startTime;
    }

    public int getLength() {
        return length;
    }

    public int getEndTime() {
        return startTime + length;
    }

    @Override
    public String toString() {
        return node.toString();
    }

    // Compare ScheduleEntries based on their end time
    @Override
    public int compareTo(ScheduleEntry o) {
        return Integer.compare(startTime + length, o.startTime + o.length);
    }
}
