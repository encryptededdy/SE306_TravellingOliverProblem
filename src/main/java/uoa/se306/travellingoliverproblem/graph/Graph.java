package uoa.se306.travellingoliverproblem.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Graph {
    private String graphName;

    private Set<Node> startingNodes = new HashSet<>(); // should this be made unmodifiable?

    private Set<Node> allNodes = new TreeSet<>(); // same as above

    public Graph(Collection<Node> startingNodes, Collection<Node> allNodes, String graphName) {
        this.startingNodes.addAll(startingNodes);
        this.allNodes.addAll(allNodes);
        this.graphName = graphName;
        for (Node node : allNodes) {
            node.calculateBottomLevel();
        }
    }

    public Set<Node> getAllNodes() {
        return allNodes;
    }

    public Set<Node> getStartingNodes() {
        return startingNodes;
    }

    public String getGraphName() { return graphName; }


    /*
    This method gets the total weights of all the nodes in the graph
     */
    public int getComputationalLoad() {
        int load = 0;
        for (Node node : allNodes){
            load += node.getCost();
        }
        return load;
    }
}
