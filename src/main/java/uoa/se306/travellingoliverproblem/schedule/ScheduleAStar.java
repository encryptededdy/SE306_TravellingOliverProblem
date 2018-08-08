package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.scheduler.heuristics.CostFunction;

import java.util.Collection;

public class ScheduleAStar extends Schedule{

    float costFunction;

    public ScheduleAStar(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes , int COMPUTATIONAL_LOAD) {
        super(processorCount, availableNodes, allNodes, COMPUTATIONAL_LOAD);

    }

    public ScheduleAStar(Schedule toCopy){
        super(toCopy);
    }

    public float getCostFunction(){
        CostFunction costFunction = new CostFunction(this);
        return costFunction.calculateCost();
    }


    public int compareTo(ScheduleAStar otherSchedule) {

        if (costFunction < otherSchedule.getCostFunction()){
            return -1;
        }else if (costFunction == otherSchedule.getCostFunction()){
            return 1;
        }else{
            return 0;
        }
    }


}
