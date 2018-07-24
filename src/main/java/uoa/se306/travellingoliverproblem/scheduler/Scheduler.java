package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.schedule.Schedule;

import java.util.HashSet;

public abstract class Scheduler {
    private Schedule bestSchedule;
    private Graph graph;

    public Scheduler(Graph graph){
        this.graph = graph;
    }

    public Schedule getBestSchedule() {
        calculateSchedule(new Schedule(1, graph.getStartingNodes(), new HashSet<>()));
        return bestSchedule;
    }

    public abstract int calculateSchedule(Schedule currentSchedule);
}
