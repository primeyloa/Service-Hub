package servicehub.model;

public class AuditEvent {
    private int eventId;
    private String action;
    private String details;
    private String timestamp;

    public AuditEvent(int eventId, String action, String details, String timestamp) {
        this.eventId = eventId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public AuditEvent(String action, String details, String timestamp) {
        this(-1, action, details, timestamp);
    }

    public int getEventId() { return eventId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, action, details);
    }
}
