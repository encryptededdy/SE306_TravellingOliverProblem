package uoa.se306.travellingoliverproblem.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    private Set<Node> startingNodes = new HashSet<>(); // should this be made unmodifiable?

    public Graph(Node... nodes) {
        startingNodes.addAll(Arrays.asList(nodes));
    }

    public Graph(Set<Node> nodes) {
        startingNodes.addAll(nodes);
    }

    public Set<Node> getStartingNodes() {
        return startingNodes;
    }
}
