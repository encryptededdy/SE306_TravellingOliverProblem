package uoa.se306.travellingoliverproblem.schedule;

import uoa.se306.travellingoliverproblem.graph.Node;

import java.util.Collection;

public class ScheduleAStar extends Schedule{

    int costFunction;

    public ScheduleAStar(int processorCount, Collection<Node> availableNodes, Collection<Node> allNodes) {
        super(processorCount, availableNodes, allNodes);
    }

    public ScheduleAStar(Schedule toCopy){
        super(toCopy);
    }

    public int getCostFunction(){
        return costFunction;
    }
    


    public int compareTo(ScheduleAStar otherSchedule) {
        return Integer.compare(costFunction, otherSchedule.getCostFunction());
    }


}
