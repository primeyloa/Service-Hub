package servicehub.model;


public class ServiceRequest implements Comparable<ServiceRequest> {
    private String requestId;
    private String sourceLocationId;
    private String destinationLocationId;
    private String category;
    private int urgency;
    private String timeSubmitted;
    private String deadline;
    private String status;
    private double cost;

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId, String category, int urgency, String timeSubmitted, String deadline, String status, double cost) {
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

    @Override
    public int compareTo(ServiceRequest other) {
        // Higher urgency comes first (Max Heap behavior or descending order)
        return Integer.compare(other.urgency, this.urgency);
    }

    @Override
    public String toString() {
        return String.format("Request[%s: %s -> %s, Cat: %s, Urgency: %d, Cost: GHS %.2f, Status: %s]", requestId, sourceLocationId, destinationLocationId, category, urgency, cost, status);
    }
}
