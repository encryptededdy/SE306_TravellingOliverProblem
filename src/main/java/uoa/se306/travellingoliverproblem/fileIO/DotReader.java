package uoa.se306.travellingoliverproblem.fileIO;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DotReader implements GraphFileReader {
    private FileInputStream dotfile;

    @Override
    public void openFile(File file) throws FileNotFoundException {
        dotfile = new FileInputStream(file);
    }

    @Override
    public Graph readFile() {
        GraphParser parser = new GraphParser(dotfile);
        Map<String, GraphNode> readNodes = parser.getNodes(); // Nodes in GraphParser format
        Map<String, GraphEdge> readEdges = parser.getEdges(); // Edges in GraphParser format
        String graphName = parser.getGraphId();

        Map<String, Node> convertedNodes = new HashMap<>();

        try {
            for (GraphNode node : readNodes.values()) {
                Integer weight = Integer.parseInt(node.getAttribute("Weight").toString());
                convertedNodes.put(node.getId(), new Node(node.getId(), weight));
            }

            for (GraphEdge edge : readEdges.values()) {
                Integer weight = Integer.parseInt(edge.getAttribute("Weight").toString());
                Node source = convertedNodes.get(edge.getNode1().getId());
                Node dest = convertedNodes.get(edge.getNode2().getId());
                Integer level = source.getLevel() + 1;
                if (level > dest.getLevel()) {
                    dest.setLevel(level);
                    calculateChildLevel(dest, level);
                }
                source.addChild(dest, weight);
                dest.addParent(source, weight);
            }
        } catch (NumberFormatException e) {
            throw new InvalidFileFormatException("Weight for edge/node missing or of invalid format");
        }

        // create the graph
        Set<Node> startNodes = new HashSet<>();
        int levels = 0;
        // find parentless nodes
        for (Node node : convertedNodes.values()) {
            if (node.getParents().isEmpty()) {
                startNodes.add(node);
            }
            if (node.getChildren().isEmpty() && node.getLevel() > levels) {
                levels = node.getLevel();
            }
        }

        if (startNodes.isEmpty()) {
            throw new InvalidFileFormatException("Cycle found in acyclic graph (or empty)");
        }

        return new Graph(startNodes, convertedNodes.values(), levels, graphName);
    }

    private void calculateChildLevel(Node currentNode, Integer level) {
        for (Node childrenNodes: currentNode.getChildren().keySet()) {
            if (level > childrenNodes.getLevel()) {
                childrenNodes.setLevel(level);
                calculateChildLevel(childrenNodes, level++);
            }
        }
    }
}
