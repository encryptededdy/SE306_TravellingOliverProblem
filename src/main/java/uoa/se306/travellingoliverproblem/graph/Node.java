package uoa.se306.travellingoliverproblem.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node implements Comparable<Node> {
    private String name;
    private int cost = 0;
    private int currentBottomLevel = 0;
    private Integer level = 1;
    private char identifier;

    // Integers for parents and children in hash map are the edge weight costs
    private Map<Node, Integer> children = new HashMap<>();
    private Map<Node, Integer> parents = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, int cost, int id) {
        this.name = name;
        this.cost = cost;

        if (id > 65) {
            throw new RuntimeException("Max 65 nodes supported");
        } else {
            this.identifier = (char)(id+58);
        }
    }

    public char getIdentifier() {
        return identifier;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public int getCost() {
        return cost;
    }

    public int getBottomLevel(){
        return currentBottomLevel;
    }

    public void calculateBottomLevel() {
        // Make sure this is run-once
        if (currentBottomLevel == 0) calculateCurrentBottomLevel(this, currentBottomLevel, new ArrayList<>());
    }

    /*
    This method will calculate the bottom level of this Node
    it is done by a recursive method
    */
    private void calculateCurrentBottomLevel(Node node, int totalCost, ArrayList<Node> visitedChildren) {
        visitedChildren.add(node);
        totalCost += node.getCost();
        Map<Node, Integer> mapOfChildren = node.getChildren();

        if(node.children.isEmpty()){
            if(currentBottomLevel < totalCost){
                currentBottomLevel = totalCost;
            }
            return;
        }
        for (Node child: mapOfChildren.keySet()) {
            if (!visitedChildren.contains(child)){
                this.calculateCurrentBottomLevel(child, totalCost, visitedChildren);
            }

        }
    }

    @Override
    public String toString() {
        return name;
    }

    // Check equality by name and children comparison
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

    @Override
    public int compareTo(Node o) {
        return name.compareTo(o.name);
    }
}
