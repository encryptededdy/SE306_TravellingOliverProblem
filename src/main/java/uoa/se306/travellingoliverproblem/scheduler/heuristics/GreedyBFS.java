package uoa.se306.travellingoliverproblem.scheduler.heuristics;

import uoa.se306.travellingoliverproblem.graph.Node;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.HashSet;
import java.util.Set;

public class GreedyBFS {

    // Best schedule found from all iterations
    protected Schedule bestSchedule;

    public Schedule getBestSchedule(){
        return bestSchedule;
    }

    /*
     This is a greedy recursive algorithm that gets a valid schedule.
     Greedy in the sense that it only looks at it's current best schedule locally.
     It will keep going deeper until it reaches the end of the graph where it has a valid schedule
     and so it will not go through the entire state space.
     Recursive in the sense that this method recursively calls itself, but does not backtrack
     because once it reaches the end, it will stop.
    */
    public void calculateGreedySchedule(Schedule partialSchedule) {

        // If the currentSchedule has no available nodes
        if (partialSchedule.getAvailableNodes().isEmpty()) {
            bestSchedule = partialSchedule;
            //End this recursive method.
            return;
        }

        //I just wanted to make a default schedule and make it have a LARGE overallTime
        Schedule tempBestSchedule = new Schedule(partialSchedule);
        tempBestSchedule.setMaxOverallTime();

        // Get all the available nodes in the schedule
        Set<Node> availableNodes = new HashSet<>(partialSchedule.getAvailableNodes());
        for (Node node : availableNodes){
            ScheduledProcessor[] processors = partialSchedule.getProcessors();

            for (int i = 0; i < processors.length; i++) {
                ScheduledProcessor processor = processors[i];
                int processorStartTime;
                int startTime = 0;
                // iterate over all the parent nodes
                for (Node parentNode : node.getParents().keySet()) {
                    // for all the processors of this current schedule
                    // if any of the processors contains the parentNode
                    // get the endTime of when this parentNode finishes inside that processor
                    for (ScheduledProcessor checkProcessor : partialSchedule.getProcessors()) {
                        ScheduleEntry sEntry = checkProcessor.getEntry(parentNode);
                        if (sEntry != null) {
                            processorStartTime = sEntry.getEndTime();
                            // add the communication cost between childNode and parentNode if not on same processor
                            processorStartTime += processor != checkProcessor ? parentNode.getChildren().get(node) : 0;
                            // if the processorStartTime is larger than the current startTime
                            // update the current startTime
                            if (processorStartTime > startTime) {
                                startTime = processorStartTime;
                                break;
                            }
                        }
                    }
                }
                //get the earliestStartTime that this available node can be scheduled (In this processor)
                startTime = processor.getEarliestStartAfter(startTime, node.getCost());
                //create a copy of our partialSchedule
                Schedule tempSchedule = new Schedule(partialSchedule);
                //add the availableNode into processor i at time startTime in the schedule
                tempSchedule.addToSchedule(node, i, startTime);

                //if this tempSchedule overallTime is smaller to the current schedule overallTime
                //update the best
                if (tempSchedule.getOverallTime() < tempBestSchedule.getOverallTime()){
                    tempBestSchedule = tempSchedule;
                }

            }
        }

        //recursively call this method by passing in the schedule that has the smallest endtime
        calculateGreedySchedule(tempBestSchedule);//recursive
    }

}
