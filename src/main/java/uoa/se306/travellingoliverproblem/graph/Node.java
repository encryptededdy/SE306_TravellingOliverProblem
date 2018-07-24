package uoa.se306.travellingoliverproblem.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {
    private String name;
    private Integer cost = 0;
    private Map<Node, Integer> children = new HashMap<>();
    private Map<Node, Integer> parents = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public void addChild(Node node, int cost) {
        children.put(node, cost);
    }

    public void addParent(Node node, int cost) {
        parents.put(node, cost);
    }

    public Map<Node, Integer> getParents() {
        return parents;
    }

    public Map<Node, Integer> getChildren() {
        return children;
    }

    public Integer getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name;
    }

    // Check equality by name and children comparison
    // TODO: Find a way to check parents(?) - currently causes loop
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            return (((Node) obj).name.equals(name) && ((Node) obj).children.equals(children));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
