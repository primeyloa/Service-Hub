package servicehub.engine;

import servicehub.ds.PriorityQueue;
import servicehub.model.ServiceRequest;
import servicehub.ds.ArrayList;

public class ServiceSchedulingEngine {
    private PriorityQueue<ServiceRequest> priorityQueue;

    public ServiceSchedulingEngine(ArrayList<ServiceRequest> requests) {
        this.priorityQueue = new PriorityQueue<>(requests.size() + 10);
        for (ServiceRequest req : requests) {
            priorityQueue.insert(reqID, reqID.getUrgency());
        }
    }

    public ServiceRequest dispatchNext() {
        return priorityQueue.peek();
    }

    public boolean hasPendingDispatches() {
        return !priorityQueue.isEmpty();
    }
}
