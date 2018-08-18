package uoa.se306.travellingoliverproblem.graph;

import uoa.se306.travellingoliverproblem.schedule.ScheduleEntry;
import uoa.se306.travellingoliverproblem.schedule.ScheduledProcessor;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

    private ScheduledProcessor[] processors;

    private boolean outEdgeCostConsistent = true;

    public NodeComparator (ScheduledProcessor[] processors){
        this.processors = processors;
    }

    /*
    The compare() method should compare two nodes based on it's data-ready-time (drt) (INCREASING ORDER)
    The drt is the finishing time of its parent + communication cost from parent to this node
    If this node has no parent, then set drt to 0 (which means this node goes at the STAR of the list)

    If two node have the same drt, then sort them in DECREASING out-edge cost
    if this node has no child, then set cost to be 0

     */
    @Override
    public int compare(Node o1, Node o2) {

        //setup, get the drt of both nodes
        //setup, get the out-edge costs of both nodes

        //if o1 drt is SMALLER than o2 drt
        //  return -1
        //else if o1 drt is EQUAL to o2 drt
        //  if o1 out-edge cost LARGER than o2 out-edge cost
        //      return -1;
        //  else if 01 out-edge cost EQUAL to o2 out-edge cost
        //      return 0;
        //  else
        //      return 1;
        //else
        //  return 1

        int drtObjectOne = getDRT(o1);
        int drtObjectTwo = getDRT(o2);

        int outEdgeCostObjectOne = (o1.getChildren().isEmpty()) ? 0 : o1.getChildren().values().iterator().next();
        int outEdgeCostObjectTwo = (o2.getChildren().isEmpty()) ? 0 : o2.getChildren().values().iterator().next();

        switch (Integer.compare(drtObjectOne, drtObjectTwo)) {
            case 0:
                return Integer.compare(outEdgeCostObjectTwo, outEdgeCostObjectOne);
            case 1:
                if (outEdgeCostObjectOne <= outEdgeCostObjectTwo) {
                    return 1;
                } else {
                    outEdgeCostConsistent = false;
                    return 1;
                }
            case -1:
                if (outEdgeCostObjectOne >= outEdgeCostObjectTwo) {
                    return -1;
                } else {
                    outEdgeCostConsistent = false;
                    return -1;
                }
            default:
                // this should never happen
                throw new RuntimeException("Integer compare returned not -1 - 1. This should never happen.");
        }
    }

    private int getDRT(Node node) {
        if (node.getParents().isEmpty()) {
            return 0;
        } else {
            Node parent = node.getParents().keySet().iterator().next();
            int endTime = 0 ;
            // now find the parent in the procs
            for (ScheduledProcessor proc : processors) {
                ScheduleEntry sEntry = proc.getEntry(parent);
                if (sEntry != null){
                    endTime = sEntry.getEndTime();
                }
            }
            return endTime + node.getParents().get(parent);
        }
    }

    public boolean isOutEdgeCostConsistent() {
        return outEdgeCostConsistent;
    }

}
