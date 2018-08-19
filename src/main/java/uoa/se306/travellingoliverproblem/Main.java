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
import uoa.se306.travellingoliverproblem.scheduler.SchedulerType;
import uoa.se306.travellingoliverproblem.visualiser.FXController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ForkJoinPool;

public class Main extends Application {

    private static ForkJoinPool forkJoinPool;
    private static SchedulerType schedulerType;
    private static Graph inputGraph;
    private static int processors = 1;
    private static boolean isParallelised = false;
    public static String outputFileName;

    // JavaFX start method (depends if visualisation enabled)
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();
        FXController controller = loader.getController();
        controller.startProcessing(inputGraph, processors, isParallelised, outputFileName, schedulerType);
        primaryStage.setTitle("Visualisation");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 850));
        primaryStage.sizeToScene(); // JavaFX Bug RT-30647 workaround
        primaryStage.show();
    }

    public static void main(String[] args) {
        long totalStartTime = System.currentTimeMillis();
        int numOfThreads = 1; //default 1 means that the code will run in sequential
        boolean useVisuals = false;

        System.out.println("The Travelling Oliver Problem scheduler uses various open-source libraries, the licenses for which are distributed with this application.\n" +
                "To view these licenses, launch the application with no parameters other than -l or --license\n");

        if (args.length == 0) {
            System.err.println("Please provide input task graph and number of processors.\nType -h or --help for help.");
            System.exit(1);
        } else if (args.length == 1) {
            String temp = args[0];
            switch (temp) {
                case "-h":
                case "--help":
                    System.out.println("java -jar scheduler.jar [INPUT.dot] [P] [OPTION]");
                    System.out.println("INPUT.dot                   A path to the file with a task graph containing integer weights in dot format.");
                    System.out.println("P                           Number of processors to schedule the INPUT graph on.");
                    System.out.println();
                    System.out.println("OPTIONAL ARGUMENTS:");
                    System.out.println("-v --visualisation          Enables GUI visuals.");
                    System.out.println("-p --parallel [INTEGER]     The amount of cores to use for execution in parallel.");
                    System.out.println("-o --output [STRING]        Specifies the name for the output file.");
                    System.out.println("-s --scheduler [STRING]     Specifies the scheduler type to use (AStar, DFS, Hybrid)  (default: Auto pick).");
                    System.exit(0);
                case "-l":
                case "--license":
                    // Print licenses
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/TOP_LICENSES.txt")));
                        String line = reader.readLine();
                        while (line != null) {
                            System.out.println(line);
                            line = reader.readLine();
                        }
                        reader.close();
                    } catch (IOException e) {
                        System.err.println("Couldn't find license file!");
                        e.printStackTrace();
                    }
                    System.exit(0);
                default:
                    System.err.println("Please provide input task graph and number of processors.\nType -h or --help for help.");
                    System.exit(1);
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

            //if it's an empty file name the default output file will be output.dot
            if (file.getName().equals(".dot")){
                outputFileName = "output.dot";
            } else{
                //gets the file name of the provided file path, gets rid of the file type and appends .dot to it.
                //this will be the default output file name. (output(graphNameFileName).dot) (if the digraph has no name
                outputFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".dot";
            }

            //check if there are any valid optional arguments
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    String temp = args[i];

                    switch (temp) {
                        case "-p":
                        case "--parallel":
                            try {
                                numOfThreads = Integer.parseInt(args[i + 1]);//will throw NumberFormatException if cant convert
                                if (numOfThreads > 1) {
                                    // Set up fork join pool to have numOfCores threads
                                    forkJoinPool = new ForkJoinPool(numOfThreads, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null ,true);
                                    isParallelised = true;
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid number of threads.\nType -h or --help for help.");
                                System.exit(1);
                            }
                            i++;
                            break;
                        case "-v":
                        case "--visualisation":
                            useVisuals = true;
                            break;
                        case "-o":
                        case "--output":
                            outputFileName = args[i + 1] + ".dot";
                            i++;
                            break;
                        case "-s":
                        case "--scheduler":
                            String typeString = "";
                            if (args.length > i + 1) {
                                typeString = args[i + 1].toUpperCase();
                            }
                            i++;
                            try {
                                schedulerType = SchedulerType.valueOf(typeString);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Invalid scheduler type.\nType -h or --help for help.");
                                System.exit(1);
                            }
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
            System.out.println("Number of threads to use: " + Integer.toString(numOfThreads));
            System.out.println("The output file name will be: " + outputFileName);
            System.out.println();

            if (useVisuals) {
                launch();
            } else {
                SchedulerRunner.getInstance().startScheduler(inputGraph, processors, isParallelised, schedulerType);
                final String tempOutputFileName = outputFileName;
                // run the following after the scheduler has finished.
                SchedulerRunner.getInstance().setThreadListener(() -> {
                    SchedulerRunner.getInstance().printResult();
                    SchedulerRunner.getInstance().getSchedule().checkValidity();
                    DotFileWriter fileWriter = new DotFileWriter(inputGraph, SchedulerRunner.getInstance().getSchedule(), tempOutputFileName);
                    fileWriter.outputSchedule();
                    System.out.println("Total time (incl startup, I/O etc.): " + (System.currentTimeMillis() - totalStartTime) + " ms");
                    System.exit(0);
                });
            }
        }
    }
}
