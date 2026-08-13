package servicehub.model;

public class Resource {
    private String resourceId;
    private String resourceType;
    private String homeLocationId;
    private int capacity;
    private String availabilityStatus;

    public Resource(String resourceId, String resourceType, String homeLocationId, int capacity, String availabilityStatus) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
    }

    public String getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public String getHomeLocationId() { return homeLocationId; }
    public int getCapacity() { return capacity; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    @Override
    public String toString() {
        return String.format("Resource[%s: %s at %s, Cap: %d, Status: %s]", resourceId, resourceType, homeLocationId, capacity, availabilityStatus);
    }
}

