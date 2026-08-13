package servicehub.model;

public class AlgorithmRun {
    private int runId;
    private String algorithmName;
    private int inputSize;
    private long timeNs;
    private long memoryKb;
    private String dateRun;

    public AlgorithmRun(int runId, String algorithmName, int inputSize, long timeNs, long memoryKb, String dateRun) {
        this.runId = runId;
        this.algorithmName = algorithmName;
        this.inputSize = inputSize;
        this.timeNs = timeNs;
        this.memoryKb = memoryKb;
        this.dateRun = dateRun;
    }

    public AlgorithmRun(String algorithmName, int inputSize, long timeNs, long memoryKb, String dateRun) {
        this(-1, algorithmName, inputSize, timeNs, memoryKb, dateRun);
    }

    public int getRunId() { return runId; }
    public String getAlgorithmName() { return algorithmName; }
    public int getInputSize() { return inputSize; }
    public long getTimeNs() { return timeNs; }
    public long getMemoryKb() { return memoryKb; }
    public String getDateRun() { return dateRun; }
}
