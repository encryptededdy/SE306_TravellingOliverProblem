package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.CostFunction;

import java.util.Collection;

public class ScheduleAStar extends Schedule{

    private float cost;

    public ScheduleAStar(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes , int COMPUTATIONAL_LOAD) {
        super(processorCount, availableNodes, allNodes, COMPUTATIONAL_LOAD);

    }

    public ScheduleAStar(Schedule toCopy){
        super(toCopy);
    }

    public void getCostFunction(){
        CostFunction costFunction = new CostFunction(this);
        cost = costFunction.calculateCost();
    }

    public float getCost(){
        return cost;
    }

    public int compareTo(ScheduleAStar otherSchedule) {

        if (cost < otherSchedule.getCost()){
            return -1;
        }else if (cost == otherSchedule.getCost()){
            return 1;
        }else{
            return 0;
        }
    }


}
