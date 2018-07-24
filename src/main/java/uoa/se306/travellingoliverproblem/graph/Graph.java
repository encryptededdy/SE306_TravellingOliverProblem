package uoa.se306.travellingoliverproblem.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Graph {
    private Set<Node> startingNodes = new HashSet<>(); // should this be made unmodifiable?

    private Set<Node> allNodes = new TreeSet<>(); // same as above

    public Graph(Collection<Node> startingNodes, Collection<Node> allNodes) {
        this.startingNodes.addAll(startingNodes);
        this.allNodes.addAll(allNodes);
    }

    public Set<Node> getAllNodes() {
        return allNodes;
    }

    public Set<Node> getStartingNodes() {
        return startingNodes;
    }
}
