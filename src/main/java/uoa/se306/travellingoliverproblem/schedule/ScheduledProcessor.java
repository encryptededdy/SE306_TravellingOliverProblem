package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.Stack;

public class ScheduledProcessor {
    private Stack<ScheduleEntry> schedule = new Stack<>();

    public void add(Node node, Integer communication) {
        if (schedule.empty()) {
            // No existing schedule, so add after 0
            schedule.push(new ScheduleEntry(communication, node));
        } else {
            // Add new ScheduleEntry, after the last scheduled item
            schedule.push(new ScheduleEntry(endTime() + 1 + communication, node));
        }
    }

    public int endTime() {
        return schedule.peek().getEndTime();
    }

    // Add with no communication cost
    public void add(Node node) {
        add(node, 0);
    }

}
