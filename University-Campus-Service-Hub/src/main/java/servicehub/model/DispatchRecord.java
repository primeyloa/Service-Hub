package servicehub.model;

/**
 * Record of a dispatched service request: which resource was assigned, along
 * what route, and the estimated travel time.
 */
public class DispatchRecord {
    private final String requestId;
    private final String resourceId;
    private final String resourceType;
    private final String routeSummary;
    private final double travelTimeMin;
    private final String dispatchTime;

    public DispatchRecord(String requestId, String resourceId, String resourceType,
                          String routeSummary, double travelTimeMin, String dispatchTime) {
        this.requestId = requestId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.routeSummary = routeSummary;
        this.travelTimeMin = travelTimeMin;
        this.dispatchTime = dispatchTime;
    }

    public String getRequestId() { return requestId; }
    public String getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public String getRouteSummary() { return routeSummary; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public String getDispatchTime() { return dispatchTime; }

    @Override
    public String toString() {
        return String.format("%s -> %s (%s) via %s, ETA %.1f min @ %s",
                requestId, resourceId, resourceType, routeSummary, travelTimeMin, dispatchTime);
    }
}
