package uoa.se306.travellingoliverproblem.fileIO;

import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.graph.Node;

import java.io.*;
import java.util.*;
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
        Pattern nodePattern = Pattern.compile("(\\w+)\\s+\\[[\\S\\s]*Weight=(\\d+)[\\S\\s]*\\][\\S\\s]*");
        Pattern edgePattern = Pattern.compile("(\\w+) [-−]> (\\w+)\\s+\\[[\\S\\s]*Weight=(\\d+)[\\S\\s]*\\][\\S\\s]*");
        Pattern graphDefinitionPattern = Pattern.compile("digraph\\s+(.+)\\s+\\{");

        // Temp storage of output nodes
        Map<String, Node> foundNodes = new HashMap<>();

        BufferedReader br = new BufferedReader(dotfile);
        String line;
        String graphName = "";
        int lineNo = 1;
        try {
            line = br.readLine();
            Matcher definitionMatcher = graphDefinitionPattern.matcher(line);
            if (!definitionMatcher.find()) {
                throw new InvalidFileFormatException("digraph definition not found");
            }

            graphName = definitionMatcher.group(1);

            StringBuilder builder = new StringBuilder();

            ArrayList<String> semicolonSeperated = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.contains("}")) {
                    break;
                }

                // Build up array of strings from the file (split by semicolon)
                if (line.contains(";")) {
                    String[] split = line.split(";");
                    builder.append(split[0]);
                    semicolonSeperated.add(builder.toString());
                    // go through anything in the middle
                    for (int i = 1; i < split.length - 1; i++) {
                        semicolonSeperated.add(split[i]);
                    }
                    // tail
                    builder = new StringBuilder();
                    builder.append(line.split(";")[split.length - 1]);
                } else {
                    builder.append(line);
                }
            }

            // Go through semicolonSeperated

            for (String subString : semicolonSeperated) {
                Matcher nodeMatcher = nodePattern.matcher(subString);
                Matcher edgeMatcher = edgePattern.matcher(subString);
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
                        throw new InvalidFileFormatException(lineNo, line);
                    }
                } else if (nodeMatcher.find()) {
                    // Found a node
                    try {
                        String nodeName = nodeMatcher.group(1);
                        Integer nodeWeight = Integer.parseInt(nodeMatcher.group(2));
                        if (foundNodes.containsKey(nodeName))
                            throw new InvalidFileFormatException("Duplicate node: " + nodeName);
                        Node node = new Node(nodeName, nodeWeight);
                        foundNodes.put(nodeName, node);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new InvalidFileFormatException(lineNo, line);
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

        Graph graph = new Graph(startNodes, foundNodes.values(), graphName);
        return graph;
    }
}
