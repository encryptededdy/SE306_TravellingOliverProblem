package se306.travellingoliverproblem;

import org.junit.Assert;
import org.junit.Test;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.InvalidScheduleException;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.util.ArrayList;

public class TestScheduleValidator {
    @Test
    public void testOverlappingSchedules() {
        /*
        Setting up schedule with overlapping entries on the same processor
         */
        Node node1 = new Node("node1", 10, 1);
        Node node2 = new Node("node2", 10, 2);

        ArrayList<Node> availableNodes = new ArrayList<>();
        availableNodes.add(node1);
        availableNodes.add(node2);

        ArrayList<Node> allNodes = new ArrayList<>();
        allNodes.add(node1);
        allNodes.add(node2);

        Schedule testSchedule = new Schedule(1, availableNodes, allNodes, false);

        testSchedule.addToSchedule(node1, 0, 0);
        testSchedule.addToSchedule(node2, 0, 0);

        try {
            testSchedule.checkValidity();
            Assert.fail("Schedules with overlapping schedule entries should throw InvalidScheduleException");
        } catch (InvalidScheduleException e) {
            System.out.println("InvalidScheduleException thrown correctly");
        }
    }
}
