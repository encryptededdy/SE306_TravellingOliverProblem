package se306.travellingoliverproblem;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import uoa.se306.travellingoliverproblem.fileIO.DotReader;
import uoa.se306.travellingoliverproblem.fileIO.GraphFileReader;
import uoa.se306.travellingoliverproblem.schedule.Schedule;
import uoa.se306.travellingoliverproblem.scheduler.DFSScheduler;
import uoa.se306.travellingoliverproblem.scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

public class DirectoryAutoTest {
    private static int MAX_PROCESSORS = 4;
    private static int TIMEOUT_SECONDS = 6;

    @TestFactory
    public Collection<DynamicTest> testAllInDirectory() throws IOException {
        ArrayList<DynamicTest> dynamicTests = new ArrayList<>();

        // Find input files...
        ArrayList<File> dotfiles = new ArrayList<>();
        File[] files = new File("autoTest/").listFiles();

        if (files == null) {
            fail("Couldn't find directory for autoTest graphs");
        }

        for (File file : files) {
            if (file.isFile() && file.getName().contains(".dot")) {
                dotfiles.add(file);
            }
        }

        for (File file : dotfiles) {
            // Find number of processors
            Pattern procPattern = Pattern.compile("(\\d+)p_.+");
            Matcher procMatcher = procPattern.matcher(file.getName());
            Integer processors = null;
            if (procMatcher.find()) {
                processors = Integer.parseInt(procMatcher.group(1));
            }

            // Find optimal solution
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            Integer optimalSolution = null;
            while ((line = br.readLine()) != null) {
                Pattern optimalPattern = Pattern.compile("\"Total schedule length\"=(\\d+)");
                Matcher optimalMatcher = optimalPattern.matcher(line);
                if (optimalMatcher.find()) {
                    optimalSolution = Integer.parseInt(optimalMatcher.group(1));
                }
            }

            // If we couldn't find the optimal solution...
            if (optimalSolution == null) {
                System.out.println("Skipping " + file.getName() + ": no optimal solution defined");
            } else if (processors == null) {
                System.out.println("Skipping " + file.getName() + ": invalid filename format");
            } else if (processors > MAX_PROCESSORS) {
                System.out.println("Skipping " + file.getName() + ": max " + MAX_PROCESSORS + " processors");
            } else {
                // Actually build the test now!
                int finalProcessors = processors;
                int finalOptimalSolution = optimalSolution;

                // DFS test
                Executable execDFS = () -> {
                    GraphFileReader reader = new DotReader();
                    reader.openFile(file);
                    Scheduler scheduler = new DFSScheduler(reader.readFile(), finalProcessors);
                    Schedule output = assertTimeoutPreemptively(Duration.ofSeconds(TIMEOUT_SECONDS), scheduler::getBestSchedule);
                    output.checkValidity();
                    assertEquals(finalOptimalSolution, output.getOverallTime());
                    // Clear memory... just in case.
                    output = null;
                    scheduler = null;
                };
                DynamicTest dtDFS = DynamicTest.dynamicTest("DFS - " + file.getName().split(".dot")[0], execDFS);

                // A* test
                /*Executable execAStar = () -> {
                    GraphFileReader reader = new DotReader();
                    reader.openFile(file);
                    Scheduler scheduler = new AStarSearchScheduler(reader.readFile(), finalProcessors);
                    Schedule output = assertTimeoutPreemptively(Duration.ofSeconds(TIMEOUT_SECONDS), scheduler::getBestSchedule);
                    output.checkValidity();
                    assertEquals(finalOptimalSolution, output.getOverallTime());
                    // Clear memory... just in case.
                    output = null;
                    scheduler = null;
                };
                DynamicTest dtAStar = DynamicTest.dynamicTest("AStar - "+file.getName().split(".dot")[0], execAStar);*/

                dynamicTests.add(dtDFS);
                //dynamicTests.add(dtAStar);
            }
        }

        return dynamicTests;
    }
}
