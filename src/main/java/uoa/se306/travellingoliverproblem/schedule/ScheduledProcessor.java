package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.ArrayList;
import java.util.Collections;

/*
Processor where Nodes are Scheduled on
 */
public class ScheduledProcessor {
    // For quick look up of scheduled entries
    private ArrayList<ScheduleEntry> entryArray = new ArrayList<>();

    ScheduledProcessor() {
        super();
    }

    // Copy constructor
    ScheduledProcessor(ScheduledProcessor toCopy) {
        this.entryArray = new ArrayList<>(toCopy.entryArray);
    }

    // Add a node to the processor at a given start time
    void add(Node node, Integer startTime) {
        ScheduleEntry entry = new ScheduleEntry(startTime, node);
        if (entryArray.isEmpty() || entry.getStartTime() > entryArray.get(entryArray.size() - 1).getStartTime()) {
            entryArray.add(entry);
        } else {
            entryArray.add(entry);
            Collections.sort(entryArray);
        }
    }

    // Used for rendering schedules
    // TODO: Remove this and use listeners instead
    public ArrayList<ScheduleEntry> getFullSchedule() {
        return entryArray;
    }

    // Get the latest end time
    public int endTime() {
        if (entryArray.isEmpty()) {
            return 0;
        }
        return entryArray.get(entryArray.size() - 1).getEndTime();
    }

    // Get the last scheduled entry
    public ScheduleEntry lastEntry() {
        return entryArray.get(entryArray.size() - 1);
    }

    /* Check if the processor contains a scheduled node
    * Use discouraged, as using getEntry is just as fast, and in most use cases the entry is
    * needed anyway, so two calls is slower than just getting the entry and checking
    * if it's null
     */
    @Deprecated
    public boolean contains(Node node) {
        return getEntry(node) != null;
    }

    // Get the scheduled entry for a node
    public ScheduleEntry getEntry(Node node) {
        for (ScheduleEntry entry : entryArray) {
            if (entry.equals(node)) return entry;
        }
        return null;
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
            // Insert entry identifier
            builder.append(entry.getNode().getIdentifier());
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
        for (int i = 0; i < entryArray.size(); i++) {
            scheduleAfter = entryArray.get(i);
            if (scheduleAfter.getStartTime() >= startTime + processTime) {
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
