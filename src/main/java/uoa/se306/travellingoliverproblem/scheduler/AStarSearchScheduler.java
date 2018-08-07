package uoa.se306.travellingoliverproblem.scheduler;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.CostFunction;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

/*
Scheduler for the A Star Algorithm
 */
public class AStarSearchScheduler extends Scheduler {

    private long totalCost;
    private Set<String> existingSchedules;

    public AStarSearchScheduler(Graph graph, int amountOfProcessors) {
        super(graph, amountOfProcessors);
    }

    @Override
    protected void calculateSchedule(Schedule currentSchedule) {
        existingSchedules.add(currentSchedule.toString()); // store this schedule as visited
        branchesConsidered++;

        // if there are no more nodes to schedule
        if(currentSchedule.getAvailableNodes().isEmpty()){
            if(bestSchedule == null || bestSchedule.getOverallTime() > currentSchedule.getOverallTime()){
                bestSchedule = currentSchedule;
            }
            return;
        }

        Stack<Schedule> optimalSchedules = new Stack<>(); // stack containing the partial schedules that are a part of optimal schedule
        PriorityQueue<Schedule> aStarQueue = new PriorityQueue<>();

        Set<Node> currentAvailableNodes = new HashSet<>(currentSchedule.getAvailableNodes());
        for(Node node : currentAvailableNodes){
            // get all the processors
            ScheduledProcessor[] processors = currentSchedule.getProcessors();



        }




//        CostFunction costFunction = new CostFunction(currentSchedule);
//        totalCost = costFunction.calculateCost();
    }


}
