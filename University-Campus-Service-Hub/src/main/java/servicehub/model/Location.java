package servicehub.model;

public class Location {
    private String locationId;
    private String name;
    private String area;
    private String locationType;
    private double xCoord;
    private double yCoord;

    public Location(String locationId, String name, String area, String locationType, double xCoord, double yCoord) {
        this.locationId = locationId;
        this.name = name;
        this.area = area;
        this.locationType = locationType;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getLocationId() { return locationId; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public String getLocationType() { return locationType; }
    public double getXCoord() { return xCoord; }
    public double getYCoord() { return yCoord; }

    @Override
    public String toString() {
        return String.format("Location[%s: %s (%s, %s)]", locationId, name, area, locationType);
    }
}
