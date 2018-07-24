package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/*
Processor where Nodes are Scheduled on
 */
public class ScheduledProcessor {
    // Maps nodes to scheduled entries
    private Map<Node, ScheduleEntry> nodeMap = new HashMap<>();
    // For quick look up of scheduled entries
    private TreeSet<ScheduleEntry> entrySet = new TreeSet<>();

    // Add a node to the processor at a given start time
    void add(Node node, Integer startTime) {
        ScheduleEntry entry = new ScheduleEntry(startTime, node);
        nodeMap.put(node, entry);
        entrySet.add(entry);
    }

    // Get the latest end time
    public int endTime() {
        return entrySet.last().getEndTime();
    }

    // Get the last scheduled entry
    public ScheduleEntry lastEntry() {
        return entrySet.last();
    }

    // Check if the processor contains a scheduled node
    public boolean contains(Node node) {
        return nodeMap.containsKey(node);
    }

    // Get the scheduled entry for a node
    public ScheduleEntry getEntry(Node node) {
        return nodeMap.get(node);
    }

    // Return the earliest time after the specified start time that can fit the
    // nodes processing time on without collision
    public int getEarliestStartAfter(int startTime, int processTime) {
        if (entrySet.isEmpty() || entrySet.first().getStartTime() - startTime >= processTime) {
            return startTime;
        }
        // ScheduleBefore is the one before the gap where we fit processTime, ScheduleAfter is after.
        ScheduleEntry scheduleBefore = entrySet.first();
        for (ScheduleEntry scheduleAfter : entrySet) {
            if (scheduleAfter.getStartTime() >= startTime+processTime) {
                if (scheduleBefore.getEndTime() < startTime) {
                    return startTime;
                } else {
                    if (scheduleAfter.getStartTime() - scheduleBefore.getEndTime() >= processTime) {
                        return scheduleBefore.getEndTime();
                    }
                }
            }
            scheduleBefore = scheduleAfter;
        }
        return lastEntry().getEndTime();
    }
}
