package servicehub.model;

/**
 * A service request raised from a department, hall or institution on campus.
 */
public class ServiceRequest implements Comparable<ServiceRequest> {
    private final String requestId;
    private final String sourceLocationId;
    private final String destinationLocationId;
    private final String category;
    private final int urgency;          // 1 (low) .. 5 (critical)
    private final String timeSubmitted;
    private final String deadline;
    private String status;              // NEW, PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    private double cost;

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId,
                          String category, int urgency, String timeSubmitted, String deadline,
                          String status, double cost) {
        this.requestId = requestId;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.category = category;
        this.urgency = urgency;
        this.timeSubmitted = timeSubmitted;
        this.deadline = deadline;
        this.status = status;
        this.cost = cost;
    }

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId,
                          String category, int urgency, String timeSubmitted, String deadline, String status) {
        this(requestId, sourceLocationId, destinationLocationId, category, urgency, timeSubmitted,
                deadline, status, defaultCost(urgency));
    }

    /** Team heuristic: base fee plus urgency loading. */
    public static double defaultCost(int urgency) {
        return 200.0 + urgency * 100.0;
    }

    public String getRequestId() { return requestId; }
    public String getSourceLocationId() { return sourceLocationId; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public String getCategory() { return category; }
    public int getUrgency() { return urgency; }
    public String getTimeSubmitted() { return timeSubmitted; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    @Override
    public int compareTo(ServiceRequest other) {
        // Higher urgency comes first (max-heap ordering)
        return Integer.compare(other.urgency, this.urgency);
    }

    @Override
    public String toString() {
        return String.format("%s: %s -> %s [%s, U%d, GHS%.0f, %s]",
                requestId, sourceLocationId, destinationLocationId,
                category, urgency, cost, status);
    }
}
