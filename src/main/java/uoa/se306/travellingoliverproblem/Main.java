package uoa.se306.travellingoliverproblem;

import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Please provide input task graph and number of processors");
        }

        if (args.length >= 2) {
            String filename = args[0];
            try {
                int processors = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of processors");
                System.exit(1);
            }
            GraphFileReader reader = new DotReader();
            try {
                reader.openFile(new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't open file");
                System.exit(1);
            }
            Graph graph = reader.readFile();

            System.out.println("Read graph with "+graph.getStartingNodes().size()+" starting nodes");
        }
        // TODO: The rest of the CLI options
    }
}
