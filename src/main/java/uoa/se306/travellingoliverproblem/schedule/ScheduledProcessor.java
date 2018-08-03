package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.lang.reflect.Array;
import java.util.*;

/*
Processor where Nodes are Scheduled on
 */
public class ScheduledProcessor {
    // Maps nodes to scheduled entries
    private Map<Node, ScheduleEntry> nodeMap = new HashMap<>();
    // For quick look up of scheduled entries
    private ArrayList<ScheduleEntry> entryArray = new ArrayList<>(); // GOING TO TRY USE ARRAYLIST

    ScheduledProcessor() {
        super();
    }

    // Copy constructor
    ScheduledProcessor(ScheduledProcessor toCopy) {
        this.nodeMap = new HashMap<>(toCopy.nodeMap);
        this.entryArray = new ArrayList<>(toCopy.entryArray);
    }

    // Add a node to the processor at a given start time
    void add(Node node, Integer startTime) {
        ScheduleEntry entry = new ScheduleEntry(startTime, node);
        nodeMap.put(node, entry);

        //TODO: please change this later to not use Collections.sort()
        entryArray.add(entry);
        Collections.sort(entryArray);

    }

    // Used for rendering schedules
    // TODO: Remove this and use listeners instead
    public ArrayList<ScheduleEntry> getFullSchedule() {
        return entryArray;
    }

    // Get the latest end time
    public int endTime() {
        return entryArray.get(entryArray.size() - 1).getEndTime();
    }

    // Get the map of all the nodes in this processor
    public Map<Node, ScheduleEntry> getNodeMap(){
        return nodeMap;
    }

    // Get the last scheduled entry
    public ScheduleEntry lastEntry() {
        return entryArray.get(entryArray.size() - 1);
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
        for (ScheduleEntry entry : entryArray) {
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
        if (entryArray.isEmpty() || entryArray.get(0).getStartTime() - startTime >= processTime) {
            return startTime;
        }

        ScheduleEntry scheduleBefore = entryArray.get(0);
        ScheduleEntry scheduleAfter;
        for (int i = 0 ; i < entryArray.size() ; i++) {
            scheduleAfter = entryArray.get(i);
            if (scheduleAfter.getStartTime() >= startTime +  processTime) {
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
        return lastEntry().getEndTime() > startTime ? lastEntry().getEndTime() : startTime;
    }
}
