package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotReader implements GraphFileReader {
    private FileReader dotfile;

    @Override
    public void openFile(File file) throws FileNotFoundException {
        dotfile = new FileReader(file);
    }

    @Override
    public Graph readFile() {
        // setup regex patterns
        Pattern nodePattern = Pattern.compile("(\\w+)\\s+\\[Weight=(\\d+)\\];");
        Pattern edgePattern = Pattern.compile("(\\w+) [-−]> (\\w+)\\s+\\[Weight=(\\d+)\\];");

        // Temp storage of output nodes
        Map<String, Node> foundNodes = new HashMap<>();

        BufferedReader br = new BufferedReader(dotfile);
        String line;
        int lineno = 1;
        try {
            if (!br.readLine().matches("digraph \\w+ \\{")) {
                throw new InvalidFileFormatException("digraph definition not found");
            }
            while ((line = br.readLine()) != null) {
                lineno++;
                if (line.contains("}")) {
                    break;
                }
                Matcher nodeMatcher = nodePattern.matcher(line);
                Matcher edgeMatcher = edgePattern.matcher(line);
                if (edgeMatcher.find()) {
                    // Found an edge
                    try {
                        String sourceNodeName = edgeMatcher.group(1);
                        String destnNodeName = edgeMatcher.group(2);
                        Integer edgeWeight = Integer.parseInt(edgeMatcher.group(3));

                        Node sourceNode = foundNodes.get(sourceNodeName);
                        Node destnNode = foundNodes.get(destnNodeName);

                        sourceNode.addChild(destnNode, edgeWeight);
                        destnNode.addParent(sourceNode, edgeWeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new InvalidFileFormatException(lineno, line);
                    }
                } else if (nodeMatcher.find()) {
                    // Found a node
                    try {
                        String nodeName = nodeMatcher.group(1);
                        Integer nodeWeight = Integer.parseInt(nodeMatcher.group(2));
                        Node node = new Node(nodeName, nodeWeight);
                        foundNodes.put(nodeName, node);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new InvalidFileFormatException(lineno, line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // create the graph
        Set<Node> startNodes = new HashSet<>();

        // find parentless nodes
        for (Node node : foundNodes.values()) {
            if (node.getParents().isEmpty()) {
                startNodes.add(node);
            }
        }

        if (startNodes.isEmpty()) {
            throw new InvalidFileFormatException("Cycle found in acyclic graph (or empty)");
        }

        Graph graph = new Graph(startNodes, foundNodes.values());
        return graph;
    }
}
