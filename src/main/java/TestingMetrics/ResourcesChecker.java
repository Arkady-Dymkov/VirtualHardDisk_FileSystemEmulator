package TestingMetrics;

/**
 * The type Resources checker.
 */
public class ResourcesChecker {
    private long startTime;

    /**
     * Gets memory.
     *
     * @return the memory
     */
    public double getMemory() {
        return (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(double)(1024 * 1024);
    }

    /**
     * Start time counting.
     */
    public void startTimeCounting() {
        startTime = System.nanoTime();
    }

    /**
     * End time counting long.
     *
     * @return time left since start time counting
     */
    public long endTimeCounting() {
        return (System.nanoTime() - startTime) / 1_000_000;
    }

}
