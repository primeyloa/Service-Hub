package servicehub.engine;

import servicehub.ds.ArrayList;
import servicehub.ds.CircularQueue;
import servicehub.ds.Deque;
import servicehub.ds.PriorityQueue;
import servicehub.ds.Queue;
import servicehub.model.ServiceRequest;

import java.util.Comparator;

/**
 * Service scheduling engine modelling four dispatch policies with the custom
 * data-structure library: FIFO queue, circular queue, deque (urgent insertion)
 * and a priority queue/heap (urgency-first dispatch).
 */
public class ServiceSchedulingEngine {

    public enum DispatchRule {
        FIFO,           // Queue
        PRIORITY,       // PriorityQueue (heap)
        URGENT_DEQUE,   // Deque (urgent requests jump the front)
        ROUND_ROBIN     // CircularQueue (bounded rolling window)
    }

    private final Queue<ServiceRequest> fifoQueue = new Queue<>();
    private final PriorityQueue<ServiceRequest> priorityQueue;
    private final Deque<ServiceRequest> urgentDeque = new Deque<>();
    private final CircularQueue<ServiceRequest> roundRobinWindow;

    private final DispatchRule rule;

    /** Comparator: higher urgency first, then earlier submission, then id. */
    private static final Comparator<ServiceRequest> URGENCY_COMPARATOR =
            Comparator.comparingInt(ServiceRequest::getUrgency).reversed()
                    .thenComparing(ServiceRequest::getTimeSubmitted)
                    .thenComparing(ServiceRequest::getRequestId);

    public ServiceSchedulingEngine(ArrayList<ServiceRequest> requests, DispatchRule rule, int windowCapacity) {
        this.rule = rule;
        this.priorityQueue = new PriorityQueue<>(Math.max(10, requests == null ? 0 : requests.size() + 10), URGENCY_COMPARATOR);
        this.roundRobinWindow = new CircularQueue<>(Math.max(1, windowCapacity));

        if (requests != null) {
            for (ServiceRequest req : requests) {
                enqueue(req);
            }
        }
    }

    public ServiceSchedulingEngine(ArrayList<ServiceRequest> requests, DispatchRule rule) {
        this(requests, rule, Math.max(16, requests == null ? 16 : requests.size()));
    }

    public void enqueue(ServiceRequest request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null");
        switch (rule) {
            case FIFO -> fifoQueue.enqueue(request);
            case PRIORITY -> priorityQueue.insert(request);
            case URGENT_DEQUE -> {
                // Critical/urgent requests (urgency >= 4) jump the front
                if (request.getUrgency() >= 4) {
                    urgentDeque.addFirst(request);
                } else {
                    urgentDeque.addLast(request);
                }
            }
            case ROUND_ROBIN -> roundRobinWindow.enqueue(request);
        }
    }

    /**
     * Peeks at the next request that would be dispatched without removing it.
     * Returns {@code null} when there is nothing pending.
     */
    public ServiceRequest peekNext() {
        if (!hasPendingDispatches()) return null;
        return switch (rule) {
            case FIFO -> fifoQueue.peek();
            case PRIORITY -> priorityQueue.peek();
            case URGENT_DEQUE -> urgentDeque.peekFirst();
            case ROUND_ROBIN -> roundRobinWindow.peek();
        };
    }

    /**
     * Dispatches (removes and returns) the next request under the configured rule.
     */
    public ServiceRequest dispatchNext() {
        return switch (rule) {
            case FIFO -> fifoQueue.dequeue();
            case PRIORITY -> priorityQueue.extract();
            case URGENT_DEQUE -> urgentDeque.removeFirst();
            case ROUND_ROBIN -> roundRobinWindow.dequeue();
        };
    }

    /**
     * Inserts an urgent request at the front of the deque (jumping the queue).
     * Only meaningful under {@link DispatchRule#URGENT_DEQUE}.
     */
    public void insertUrgentAtFront(ServiceRequest request) {
        urgentDeque.addFirst(request);
    }

    public boolean hasPendingDispatches() {
        return switch (rule) {
            case FIFO -> !fifoQueue.isEmpty();
            case PRIORITY -> !priorityQueue.isEmpty();
            case URGENT_DEQUE -> !urgentDeque.isEmpty();
            case ROUND_ROBIN -> !roundRobinWindow.isEmpty();
        };
    }

    public int pendingCount() {
        return switch (rule) {
            case FIFO -> fifoQueue.size();
            case PRIORITY -> priorityQueue.size();
            case URGENT_DEQUE -> urgentDeque.size();
            case ROUND_ROBIN -> roundRobinWindow.size();
        };
    }

    public DispatchRule getRule() {
        return rule;
    }
}
