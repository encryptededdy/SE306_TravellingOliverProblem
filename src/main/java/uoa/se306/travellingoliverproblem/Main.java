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
import uoa.se306.travellingoliverproblem.parallel.BranchAndBoundRecursiveAction;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.HybridScheduler;
import uoa.se306.travellingoliverproblem.scheduler.SchedulerRunner;
import uoa.se306.travellingoliverproblem.visualiser.FXController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public class Main extends Application {

    private static ForkJoinPool forkJoinPool;
    private static FXController controller;
    private static Graph inputGraph;
    private static int processors = 1;
    private static boolean isParallelised = false;
    private static String outputFileName;

    // JavaFX start method (depends if visualisation enabled)
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.startProcessing(inputGraph, processors, isParallelised, outputFileName);
        primaryStage.setTitle("Visualisation");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 850));
        primaryStage.sizeToScene(); // JavaFX Bug RT-30647 workaround
        primaryStage.show();
    }

    public static void main(String[] args) {
        long totalStartTime = System.currentTimeMillis();
        int numOfCores = 1; //default 1 means that the code will run in sequential
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
                    System.exit(0);
                case "-l":
                case "--license":
                    // Print licenses
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(Main.class.getResource("/LICENSES.txt").getFile()));
                        String line = reader.readLine();
                        while (line != null) {
                            System.out.println(line);
                            line = reader.readLine();
                        }
                        reader.close();
                    } catch (IOException e) {
                        System.err.println("Couldn't find license file!");
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
                            isParallelised = numOfCores > 1;
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
            System.out.println("Number of threads to use: " + Integer.toString(numOfCores));
            System.out.println("The output file name will be: " + outputFileName);
            System.out.println();

            if (useVisuals) {
                launch();
            } else {
                if (isParallelised) {
                    // Run AStar
                    // TODO move this to schedulerrunner
                    long t1 = System.currentTimeMillis();
                    forkJoinPool = new ForkJoinPool(numOfCores, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true );
//                    HybridScheduler initialScheduler = new HybridScheduler(inputGraph, processors, isParallelised, 1);
//                    initialScheduler.getBestSchedule(); \\TODO run hybrid if certain size
                    Set<Schedule> schedules = new HashSet<>();
                    schedules.add(new Schedule(processors, inputGraph.getStartingNodes(), inputGraph.getAllNodes(), true));
                    BranchAndBoundRecursiveAction bab = new BranchAndBoundRecursiveAction(schedules, processors);
                    BranchAndBoundRecursiveAction.graph = inputGraph;
                    bab.invoke();
                    System.out.println("Took: " + ((double)System.currentTimeMillis() - (double)t1)/1000);
                    System.out.println(bab.getBestSchedule().getOverallTime());
                    System.exit(bab.isCompletedAbnormally() ? 1 : 0);

                }
                else {
                    SchedulerRunner.getInstance().startScheduler(inputGraph, processors, isParallelised);
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
}
