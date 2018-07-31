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

    ScheduledProcessor() {
        super();
    }

    // Copy constructor
    ScheduledProcessor(ScheduledProcessor toCopy) {
        this.nodeMap = new HashMap<>(toCopy.nodeMap);
        this.entrySet = new TreeSet<>(toCopy.entrySet);
    }

    // Add a node to the processor at a given start time
    void add(Node node, Integer startTime) {
        ScheduleEntry entry = new ScheduleEntry(startTime, node);
        nodeMap.put(node, entry);
        entrySet.add(entry);
    }

    // Used for rendering schedules
    // TODO: Remove this and use listeners instead
    public TreeSet<ScheduleEntry> getFullSchedule() {
        return entrySet;
    }

    // Get the latest end time
    public int endTime() {
        return entrySet.last().getEndTime();
    }

    // Get the map of all the nodes in this processor
    public Map<Node, ScheduleEntry> getNodeMap(){
        return nodeMap;
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

    // Convert schedule into a string representation
    @Override
    public String toString() {
        int lastScheduleEnd = 0;
        StringBuilder builder = new StringBuilder();
        for (ScheduleEntry entry : entrySet) {
            if (entry.getStartTime() > lastScheduleEnd) {
                // Insert Gap
                builder.append(entry.getStartTime() - lastScheduleEnd);
            }
            // Insert entry name
            builder.append(entry.toString());
        }
        return builder.toString();
    }

    // Return the earliest time after the specified start time that can fit the
    // nodes processing time on without collision
    public int getEarliestStartAfter(int startTime, int processTime) {
        // If the processor doesn't have any entries/nodes, or if the first entry/node didn't start on 0
        // and it has a gap (before the node) large enough for the processTime, we can just return the startTime
        if (entrySet.isEmpty() || entrySet.first().getStartTime() - startTime >= processTime) {
            return startTime;
        }
        // ScheduleBefore is the one before the gap where we fit processTime, ScheduleAfter is after.
        ScheduleEntry scheduleBefore = entrySet.first();
        for (ScheduleEntry scheduleAfter : entrySet) { // For every nodeAfter scheduled in the processor
            if (scheduleAfter.getStartTime() >= startTime+processTime) { // If the nodeAfters start time is just before the finishing time of the inputNode
                if (scheduleBefore.getEndTime() < startTime) { // If the nodeBefore end time is smaller than inputNodes startTime
                    return startTime;                          // you can fit the inputNode between nodeBefore and nodeAfter
                } else {
                    // If you can fit the processTime of the inputNode in-between nodeBefore and nodeAfter
                    if (scheduleAfter.getStartTime() - scheduleBefore.getEndTime() >= processTime) {
                        return scheduleBefore.getEndTime(); // Return the endTime of nodeBefore
                    }
                }
            }
            scheduleBefore = scheduleAfter; // Increment to the next node to test for a gap
        }
        return lastEntry().getEndTime() > startTime ? lastEntry().getEndTime() : startTime; // This gets returned when there is no gap large enough in the processor to fit the inputNode
    }
}
