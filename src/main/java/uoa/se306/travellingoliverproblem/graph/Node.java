package uoa.se306.travellingoliverproblem.graph;

import java.util.*;

public class Node implements Comparable<Node> {
    private String name;
    private Integer cost = 0;
    private int currentBottomLevel = 0;

    // Integers for parents and children in hash map are the edge weight costs
    private Map<Node, Integer> children = new HashMap<>();
    private Map<Node, Integer> parents = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, int cost) {
        this.name = name;
        this.cost = cost;
        calculateCurrentBottomLevel();
    }

    public void addChild(Node node, int cost) {
        children.put(node, cost);
    }

    public void addParent(Node node, int cost) {
        parents.put(node, cost);
    }

    /*
    This method will calculate the bottom level of this Node
    it is done by a recursive method
     */
    public void calculateCurrentBottomLevel(){
        // set of children that have been marked as visited
        Set<Node> visitedChildren = new HashSet<>();

        int bottomLevel = 0;
        // if it's at the leaf node(bottom level node)
        if(this.children.isEmpty()){
            if(this.currentBottomLevel < bottomLevel){
                currentBottomLevel = bottomLevel;
            }
            return;
        }
        for(Map.Entry<Node, Integer> entry: children.entrySet()){
        }
        // create stack for visited nodes
        // loop through all nodes

        //recursiveFunction (Node child)
        // add weight to global counter (totalCost)
        //if this child node, has no more children nodes
        //  if (currentGlobalBottomLevel < totalCost)
        //  currentGlobalLevel = totalCost
        //  return;
        //else
        //  get map child.getChildren()
        //  get any one of those children nodes and pass into this recursiveFunction()
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

    public int getBottomLevel(){
        return currentBottomLevel;
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

    @Override
    public int compareTo(Node o) {
        return name.compareTo(o.name);
    }
}
