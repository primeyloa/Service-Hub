package servicehub.model;

/**
 * A deployable resource on campus. Represents both personnel (e.g. a
 * maintenance technician) and assets (e.g. a shuttle van). A resource has a
 * home location, a capacity and an availability state.
 */
public class Resource {
    private final String resourceId;
    private final String resourceType;
    private final String homeLocationId;
    private final int capacity;
    private String availabilityStatus; // AVAILABLE, BUSY, MAINTENANCE, OFFLINE
    private String name;

    public Resource(String resourceId, String resourceType, String homeLocationId, int capacity, String availabilityStatus) {
        this(resourceId, resourceType, homeLocationId, capacity, availabilityStatus,
                resourceType + " " + resourceId);
    }

    public Resource(String resourceId, String resourceType, String homeLocationId, int capacity,
                    String availabilityStatus, String name) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = availabilityStatus;
        this.name = name;
    }

    public String getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public String getHomeLocationId() { return homeLocationId; }
    public int getCapacity() { return capacity; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(availabilityStatus);
    }

    @Override
    public String toString() {
        return String.format("%s [%s at %s, cap %d, %s]", name, resourceId, homeLocationId, capacity, availabilityStatus);
    }
}
