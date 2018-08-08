package uoa.se306.travellingoliverproblem.schedule;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MinimalSchedule {
    private byte[] byteArray;

    public MinimalSchedule(Schedule schedule) {
        this.byteArray = schedule.toString().getBytes(StandardCharsets.US_ASCII);
    }

    public MinimalSchedule(ScheduleAStar schedule){
        this.byteArray = schedule.toString().getBytes(StandardCharsets.US_ASCII);
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
}
