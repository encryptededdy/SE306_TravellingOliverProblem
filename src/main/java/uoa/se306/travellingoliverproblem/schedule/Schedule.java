package uoa.se306.travellingoliverproblem.schedule;

public class Schedule {
    private ScheduledProcessor[] processors;

    public Schedule(int processorCount) {
        processors = new ScheduledProcessor[processorCount];
        for (int i = 0; i < processorCount; i++) {
            processors[i] = new ScheduledProcessor();
        }
    }

    public ScheduledProcessor[] getProcessors() {
        return processors;
    }
}
