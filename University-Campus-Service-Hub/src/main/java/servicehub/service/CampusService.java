package servicehub.service;

import servicehub.DatabaseManager;
import servicehub.algorithms.DynamicProgrammingOptimizer;
import servicehub.algorithms.GraphAlgorithms;
import servicehub.algorithms.GreedyOptimizer;
import servicehub.ds.ArrayList;
import servicehub.ds.HashMap;
import servicehub.ds.HashTable;
import servicehub.engine.RoutingEngine;
import servicehub.engine.ServiceSchedulingEngine;
import servicehub.model.AuditEvent;
import servicehub.model.DispatchRecord;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.Road;
import servicehub.model.ServiceRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Facade tying the database, engines and algorithms together for the Campus
 * Service Hub. The GUI and the CLI both talk to this class.
 */
public class CampusService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private ArrayList<Location> locations;
    private ArrayList<Road> roads;
    private ArrayList<Resource> resources;
    private ArrayList<ServiceRequest> requests;
    private ArrayList<AuditEvent> auditLog;

    private final HashMap<String, String> locationNames = new HashMap<>();
    private final HashTable<String, String> locationAreas = new HashTable<>();
    private RoutingEngine routingEngine;

    public CampusService() {
        reload();
    }

    public void reload() {
        locations = DatabaseManager.loadLocations();
        roads = DatabaseManager.loadRoads();
        resources = DatabaseManager.loadResources();
        requests = DatabaseManager.loadServiceRequests();
        auditLog = DatabaseManager.loadAuditEvents();

        locationNames.clear();
        locationAreas.clear();
        for (int i = 0; i < locations.size(); i++) {
            Location l = locations.get(i);
            locationNames.put(l.getLocationId(), l.getName());
            locationAreas.put(l.getLocationId(), l.getArea());
        }
        routingEngine = new RoutingEngine(roads);
    }

    public ArrayList<Location> getLocations() { return locations; }
    public ArrayList<Road> getRoads() { return roads; }
    public ArrayList<Resource> getResources() { return resources; }
    public ArrayList<ServiceRequest> getRequests() { return requests; }
    public ArrayList<AuditEvent> getAuditLog() { return auditLog; }
    public RoutingEngine getRoutingEngine() { return routingEngine; }

    public String locationName(String id) {
        String name = locationNames.get(id);
        return name != null ? name : id;
    }

    public String locationArea(String id) {
        String area = locationAreas.get(id);
        return area != null ? area : "";
    }

    public ArrayList<Resource> availableResources() {
        ArrayList<Resource> result = new ArrayList<>();
        for (int i = 0; i < resources.size(); i++) {
            Resource r = resources.get(i);
            if (r.isAvailable()) result.add(r);
        }
        return result;
    }

    /**
     * Picks the available resource whose home location is nearest (by shortest
     * travel time) to the request source.
     */
    public Resource nearestResource(ServiceRequest request) {
        ArrayList<Resource> available = availableResources();
        if (available.isEmpty()) return null;
        Resource best = available.get(0);
        double bestEta = routingEngine.findShortestPath(best.getHomeLocationId(), request.getSourceLocationId()).totalWeight;
        for (int i = 1; i < available.size(); i++) {
            Resource r = available.get(i);
            GraphAlgorithms.RouteResult route = routingEngine.findShortestPath(r.getHomeLocationId(), request.getSourceLocationId());
            if (route.isReachable() && route.totalWeight < bestEta) {
                bestEta = route.totalWeight;
                best = r;
            }
        }
        return best;
    }

    /**
     * Picks the best available resource for a request using the greedy
     * urgency/cost rule: prefer higher-urgency requests served by the nearest
     * available resource by travel time.
     */
    public DispatchRecord dispatch(ServiceRequest request, Resource resource) {
        String from = resource.getHomeLocationId();
        String to = request.getSourceLocationId();
        GraphAlgorithms.RouteResult route = routingEngine.findShortestPath(from, to);
        String routeSummary = route.isReachable() ? route.pathToString() : "NO ROUTE (disconnected)";
        double eta = route.isReachable() ? route.totalWeight : Double.MAX_VALUE;

        resource.setAvailabilityStatus("BUSY");
        DatabaseManager.updateResourceStatus(resource.getResourceId(), "BUSY");
        request.setStatus("ASSIGNED");
        DatabaseManager.updateServiceRequestStatus(request.getRequestId(), "ASSIGNED");

        DispatchRecord record = new DispatchRecord(
                request.getRequestId(), resource.getResourceId(), resource.getResourceType(),
                routeSummary, eta, now());

        audit("DISPATCH", request.getRequestId() + " -> " + resource.getResourceId()
                + " [route " + routeSummary + ", ETA " + eta + " min]");
        return record;
    }

    public void addRequest(ServiceRequest request) {
        requests.add(request);
        DatabaseManager.insertServiceRequest(request);
        audit("CREATE", request.getRequestId() + " " + request.getCategory()
                + " U" + request.getUrgency() + " @" + request.getSourceLocationId());
    }

    public ServiceRequest nextRequest() {
        if (requests.isEmpty()) return null;
        return requests.get(0);
    }

    public ArrayList<ServiceRequest> pendingRequests() {
        ArrayList<ServiceRequest> result = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ServiceRequest r = requests.get(i);
            String status = r.getStatus();
            if (status == null || "NEW".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
                result.add(r);
            }
        }
        return result;
    }

    public ArrayList<ServiceRequest> optimizeWithDP(double budget) {
        return DynamicProgrammingOptimizer.selectRequests(requests, budget);
    }

    public ArrayList<ServiceRequest> greedySelection(double budget) {
        return GreedyOptimizer.selectRequestsGreedy(requests, budget);
    }

    public String greedyCounterExample() {
        return GreedyOptimizer.greedyCounterExample();
    }

    private void audit(String action, String details) {
        AuditEvent event = new AuditEvent(action, details, now());
        auditLog.add(event);
        DatabaseManager.insertAuditEvent(event);
    }

    private static String now() {
        return LocalDateTime.now().format(TS);
    }
}
