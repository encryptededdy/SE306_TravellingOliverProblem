package uoa.se306.travellingoliverproblem.schedule;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MinimalSchedule {
    private byte[] byteArray;
    private float cost = Integer.MAX_VALUE;

    public MinimalSchedule(Schedule schedule) {
        byteArray = schedule.toString().getBytes(StandardCharsets.US_ASCII);
        cost = schedule.getCost();
    }

    // Note: Using this constructor will prevent cleanExistingSchedules from working. However, it should never be used anyway for storing into ExistingSchedules array
    public MinimalSchedule(ScheduledProcessor[] scheduledProcessorArray) { // build from arrays only
        byteArray = Arrays.stream(scheduledProcessorArray).map(ScheduledProcessor::toString).sorted().collect(Collectors.joining()).getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MinimalSchedule) {
            return Arrays.equals(byteArray, ((MinimalSchedule) obj).byteArray);
        } else {
            return false;
        }
    }

    public float getCost() {
        return cost;
    }
}
