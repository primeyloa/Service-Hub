package servicehub.model;


public class Road {
    private String roadId;
    private String fromLocationId;
    private String toLocationId;
    private double distanceKm;
    private double travelTimeMin;
    private double conditionWeight;

    public Road(String roadId, String fromLocationId, String toLocationId, double distanceKm, double travelTimeMin, double conditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.travelTimeMin = travelTimeMin;
        this.conditionWeight = conditionWeight;
    }

    public String getRoadId() { return roadId; }
    public String getFromLocationId() { return fromLocationId; }
    public String getToLocationId() { return toLocationId; }
    public double getDistanceKm() { return distanceKm; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public double getConditionWeight() { return conditionWeight; }

    @Override
    public String toString() {
        return String.format("Road[%s: %s -> %s, %.2f km, cond: %.1f]", roadId, fromLocationId, toLocationId, distanceKm, conditionWeight);
    }
}
