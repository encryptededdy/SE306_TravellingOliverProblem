package uoa.se306.travellingoliverproblem.schedule;

public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException(String error) {
        System.out.println(error);
    }
}
