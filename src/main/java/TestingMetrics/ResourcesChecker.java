package TestingMetrics;

public class ResourcesChecker {
    private long startTime;

    public double getMemory() {
        return (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(double)(1024 * 1024);
    }

    public void startTimeCounting() {
        startTime = System.nanoTime();
    }

    public long endTimeCounting() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

    public void printMemoryUsage() {
        System.out.println(getMemory());
    }
}
