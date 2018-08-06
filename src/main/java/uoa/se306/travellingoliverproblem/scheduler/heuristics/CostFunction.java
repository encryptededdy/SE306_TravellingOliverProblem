package uoa.se306.travellingoliverproblem.scheduler.heuristics;

public class CostFunction {
    private long totalIdleTime;
    private long totalNumberOfProcessors;
    private long dataReadyTime;
    private long totalCost;

    public long calculateCost(){
        totalIdleTime = calculateTotalIdleTime();
        totalNumberOfProcessors = calculateTotalNumberOfProcessors();
        dataReadyTime = calculateDataReadyTime();

        totalCost = totalIdleTime + totalNumberOfProcessors + dataReadyTime;

        return totalCost;
    }

    public long calculateTotalNumberOfProcessors(){

        return totalNumberOfProcessors;
    }

    public long calculateDataReadyTime(){

        return dataReadyTime;
    }

    public long calculateTotalIdleTime(){

        return totalIdleTime;
    }

}
