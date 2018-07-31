package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.TreeSet;

public class SchedulerRunner {
    private static SchedulerRunner ourInstance = new SchedulerRunner();
    private Graph inputGraph;
    private int noProcessors;
    private Schedule schedule;
    // TODO: Add listeners

    public static SchedulerRunner getInstance() {
        return ourInstance;
    }

    private SchedulerRunner() {
    }

    public void startScheduler(Graph inputGraph, int noProcessors) {
        this.inputGraph = inputGraph;
        this.noProcessors = noProcessors;

        Scheduler scheduler = new DFSScheduler(inputGraph, noProcessors);
        // TODO: Multithread this
        long startTime = System.nanoTime();
        schedule = scheduler.getBestSchedule();
        long endTime = System.nanoTime();
        System.out.println("Took " + (endTime - startTime) / 1000000 + " ms");
    }

    public void printResult() {
        ScheduledProcessor[] pro = schedule.getProcessors();
        for (int i = 0; i < pro.length; i++) {
            TreeSet<ScheduleEntry> nodeMap = pro[i].getFullSchedule();
            System.out.println("Processor " + Integer.toString(i) + " has tasks:" + nodeMap.toString());
        }
        System.out.println("The best overall time was: " + schedule.getOverallTime());
    }

    public Graph getInputGraph() {
        return inputGraph;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
