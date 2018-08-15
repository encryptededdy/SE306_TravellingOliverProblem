package uoa.se306.travellingoliverproblem.graph;

import java.util.Comparator;

public class NodeCostComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        return Integer.compare(o1.getCost(), o2.getCost());
    }
}
