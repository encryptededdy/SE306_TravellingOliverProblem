package uoa.se306.travellingoliverproblem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uoa.se306.travellingoliverproblem.fileIO.DotFileWriter;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.graph.Graph;
import uoa.se306.travellingoliverproblem.scheduler.SchedulerRunner;
import uoa.se306.travellingoliverproblem.visualiser.FXController;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private static FXController controller;
    private static Graph inputGraph;
    private static int processors = 1;
    private static String outputFileName;

    // JavaFX start method (depends if visualisation enabled)
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.startProcessing(inputGraph, processors, outputFileName);
        primaryStage.setTitle("Visualisation");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.sizeToScene(); // JavaFX Bug RT-30647 workaround
        primaryStage.show();
    }

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
                processors = Integer.parseInt(args[1]);
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
            inputGraph = reader.readFile();

            File file = new File(fileName);
            //gets the file name of the provided file path, gets rid of the file type and appends output.dot to it.
            //this will be the default output file name
            outputFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "-output.dot";

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
            System.out.println("Read graph with " + inputGraph.getStartingNodes().size() + " starting nodes");
            System.out.println("Number of processors to schedule: " + Integer.toString(processors));
            System.out.println("Number of cores to use: " + Integer.toString(numOfCores));
            System.out.println("Use visuals? " + String.valueOf(useVisuals));
            System.out.println("The output file name will be: " + outputFileName);
            System.out.println();

            if (useVisuals) {
                launch();
            } else {
                SchedulerRunner.getInstance().startScheduler(inputGraph, processors);
                final String tempOutputFileName = outputFileName;
                // run the following after the scheduler has finished.
                SchedulerRunner.getInstance().setThreadListener(() -> {
                    SchedulerRunner.getInstance().printResult();
                    SchedulerRunner.getInstance().getSchedule().checkValidity();
                    DotFileWriter fileWriter = new DotFileWriter(inputGraph, SchedulerRunner.getInstance().getSchedule(), tempOutputFileName);
                    fileWriter.outputSchedule();
                    System.exit(0);
                });
            }
        }
    }
}
