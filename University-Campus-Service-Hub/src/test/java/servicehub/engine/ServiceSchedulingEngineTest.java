package servicehub.engine;

import org.junit.jupiter.api.Test;
import servicehub.ds.ArrayList;
import servicehub.model.ServiceRequest;

import static org.junit.jupiter.api.Assertions.*;

class ServiceSchedulingEngineTest {

    private ServiceRequest req(String id, int urgency, String time) {
        return new ServiceRequest(id, "L001", "L002", "Maintenance",
                urgency, time, "2026-07-01T20:00", "NEW");
    }

    private ArrayList<ServiceRequest> pending() {
        ArrayList<ServiceRequest> list = new ArrayList<>();
        list.add(req("Q1", 3, "2026-07-01T08:00"));
        list.add(req("Q2", 5, "2026-07-01T08:05"));
        list.add(req("Q3", 1, "2026-07-01T08:10"));
        list.add(req("Q4", 5, "2026-07-01T08:15"));
        return list;
    }

    @Test
    void fifoDispatchesInSubmissionOrder() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending(), ServiceSchedulingEngine.DispatchRule.FIFO);
        assertEquals("Q1", engine.dispatchNext().getRequestId());
        assertEquals("Q2", engine.dispatchNext().getRequestId());
        assertEquals("Q3", engine.dispatchNext().getRequestId());
        assertEquals("Q4", engine.dispatchNext().getRequestId());
        assertFalse(engine.hasPendingDispatches());
    }

    @Test
    void priorityDispatchesHighestUrgencyFirst() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending(), ServiceSchedulingEngine.DispatchRule.PRIORITY);
        assertEquals("Q2", engine.dispatchNext().getRequestId());
        assertEquals("Q4", engine.dispatchNext().getRequestId());
        assertEquals("Q1", engine.dispatchNext().getRequestId());
        assertEquals("Q3", engine.dispatchNext().getRequestId());
    }

    @Test
    void urgentDequeFrontLoadsCritical() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending(), ServiceSchedulingEngine.DispatchRule.URGENT_DEQUE);
        // critical (>=4) requests jump the front; the latest critical lands first
        assertEquals("Q4", engine.dispatchNext().getRequestId());
        assertEquals("Q2", engine.dispatchNext().getRequestId());
        assertEquals("Q1", engine.dispatchNext().getRequestId());
        assertEquals("Q3", engine.dispatchNext().getRequestId());
    }

    @Test
    void roundRobinFIFOWithinWindow() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending(), ServiceSchedulingEngine.DispatchRule.ROUND_ROBIN, 8);
        assertEquals("Q1", engine.dispatchNext().getRequestId());
        assertEquals("Q2", engine.dispatchNext().getRequestId());
    }

    @Test
    void roundRobinRespectsCapacity() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(new ArrayList<>(), ServiceSchedulingEngine.DispatchRule.ROUND_ROBIN, 2);
        engine.enqueue(req("Q1", 1, "t"));
        engine.enqueue(req("Q2", 1, "t"));
        assertThrows(IllegalStateException.class, () -> engine.enqueue(req("Q3", 1, "t")));
    }

    @Test
    void peekNextDoesNotRemove() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending(), ServiceSchedulingEngine.DispatchRule.PRIORITY);
        assertEquals("Q2", engine.peekNext().getRequestId());
        assertEquals("Q2", engine.peekNext().getRequestId());
        assertEquals(4, engine.pendingCount());
    }

    @Test
    void emptyEngineHasNoPending() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(new ArrayList<>(), ServiceSchedulingEngine.DispatchRule.FIFO);
        assertFalse(engine.hasPendingDispatches());
        assertThrows(IllegalStateException.class, engine::dispatchNext);
        assertNull(engine.peekNext());
    }

    @Test
    void nullRequestRejected() {
        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(new ArrayList<>(), ServiceSchedulingEngine.DispatchRule.FIFO);
        assertThrows(IllegalArgumentException.class, () -> engine.enqueue(null));
    }
}
