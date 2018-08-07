package uoa.se306.travellingoliverproblem.graph;

import java.util.*;

public class Graph {
    private String graphName;

    private Set<Node> startingNodes = new HashSet<>(); // should this be made unmodifiable?

    private Set<Node> allNodes = new TreeSet<>(); // same as above

    private Map<Node, Integer> nodesBottomLevelMap= new HashMap<>();

    public Graph(Collection<Node> startingNodes, Collection<Node> allNodes, String graphName) {
        this.startingNodes.addAll(startingNodes);
        this.allNodes.addAll(allNodes);
        this.graphName = graphName;
    }

    public Set<Node> getAllNodes() {
        return allNodes;
    }

    public Set<Node> getStartingNodes() {
        return startingNodes;
    }

    public String getGraphName() { return graphName; }
}
