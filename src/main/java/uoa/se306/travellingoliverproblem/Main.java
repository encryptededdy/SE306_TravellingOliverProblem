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
        String output = "-output.dot";//do we need the .dot ?

        //If the user did not provide the necessary input.dot file AND the number of processors, print an error msg
        if (args.length < 2) {
            System.err.println("Please provide input task graph and number of processors");
        }

        if (args.length >= 2) {
            String fileName = args[0];

            try {
                int processors = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of processors");
                System.exit(1);
            }

            GraphFileReader reader = new DotReader();
            try {
                reader.openFile(new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't open file");
                System.exit(1);
            }
            Graph graph = reader.readFile();

            File file = new File(fileName);
            //gets the file name of the provided file path, gets rid of the file type and appends output.dot to it.
            String outputFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + output;

            //check if there are any valid optional arguments
            if (args.length > 2 ){
                for (int i = 2; i < args.length ; i++){
                    String temp = args[i];

                    if (temp.equals("-p")){
                        numOfCores = Integer.parseInt(args[i+1]);//will throw NumberFormatException if cant convert
                        i = i + 1;
                    }else if(temp.equals("-v")){
                        useVisuals = true;
                    }else if(temp.equals("-o")){
                        outputFileName =  args[i+1] + output;
                        i = i + 1;
                    }else{
                        System.err.println("Invalid optional argument(s)");
                        System.exit(1);
                    }
                }
            }

            //Testing purposes
            System.out.println("Read graph with "+graph.getStartingNodes().size()+" starting nodes");
            System.out.println("Number of cores to use: " + Integer.toString(numOfCores));
            System.out.println("Use visuals ? " + String.valueOf(useVisuals));
            System.out.println("The output file name will be: " + outputFileName);

        }
    }
}
