package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.*;

public class ScheduledProcessor {
    private Map<Node,ScheduleEntry> nodeMap = new HashMap<>();
    private TreeSet<ScheduleEntry> entrySet = new TreeSet<>();

    public void add(Node node, Integer startTime) {
        ScheduleEntry entry = new ScheduleEntry(startTime, node);
        nodeMap.put(node, entry);
        entrySet.add(entry);
    }

    public int endTime() {
        return entrySet.last().getEndTime();
    }

    public ScheduleEntry lastEntry() {
        return entrySet.last();
    }

    public boolean contains(Node node) {
        return nodeMap.containsKey(node);
    }

    public ScheduleEntry getEntry(Node node) {
        return nodeMap.get(node);
    }
}
