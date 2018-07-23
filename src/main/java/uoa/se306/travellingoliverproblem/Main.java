package uoa.se306.travellingoliverproblem;

import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        int numOfCores = 1; //default 1 means that the code will run in sequential
        boolean useVisuals = false;

        if (args.length == 0) {
            System.err.println("Please provide input task graph and number of processors.\nType -h or --help for help.");
        } else if (args.length == 1) {
            String temp = args[0];
            if (temp.equals("-h") || temp.equals("--help")) {
                System.out.println("java −jar scheduler.jar [INPUT.dot] [P] [OPTION]");
                System.out.println("INPUT.dot                   A path to the file with a task graph containing integer weights in dot format.");
                System.out.println("P                           Number of processors to schedule the INPUT graph on.");
                System.out.println();
                System.out.println("OPTIONAL ARGUMENTS:");
                System.out.println("-v --visualisation          Enables GUI visuals.");
                System.out.println("-p --parallel [INTEGER]     The amount of cores to use for execution in parallel.");
                System.out.println("-o --output [STRING]        Specifies the name for the output file.");
                System.exit(0);
            } else {
                System.err.println("Please provide input task graph and number of processors.\nType -h or --help for help.");
            }
        } else {
            String fileName = args[0];

            try {
                int processors = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of processors.\nType -h or --help for help.");
                System.exit(1);
            }

            GraphFileReader reader = new DotReader();
            try {
                reader.openFile(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't open file.\nType -h or --help for help.");
                System.exit(1);
            }
            Graph graph = reader.readFile();

            File file = new File(fileName);
            //gets the file name of the provided file path, gets rid of the file type and appends output.dot to it.
            //this will be the default output file name
            String outputFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "-output.dot";

            //check if there are any valid optional arguments
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    String temp = args[i];

                    switch (temp) {
                        case "-p":
                        case "--parallel":
                            numOfCores = Integer.parseInt(args[i + 1]);//will throw NumberFormatException if cant convert

                            i = i + 1;
                            break;
                        case "-v":
                        case "--visualisation":
                            useVisuals = true;
                            break;
                        case "-o":
                        case "--output":
                            outputFileName = args[i + 1] + ".dot";
                            i = i + 1;
                            break;
                        default:
                            System.err.println("Invalid optional argument(s).\nType -h or --help for help.");
                            System.exit(1);
                    }
                }
            }
            //Testing purposes
            System.out.println("Read graph with " + graph.getStartingNodes().size() + " starting nodes");
            System.out.println("Number of cores to use: " + Integer.toString(numOfCores));
            System.out.println("Use visuals ? " + String.valueOf(useVisuals));
            System.out.println("The output file name will be: " + outputFileName);
        }
    }
}
