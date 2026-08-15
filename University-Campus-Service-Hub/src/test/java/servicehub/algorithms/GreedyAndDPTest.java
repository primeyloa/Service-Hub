package servicehub.algorithms;

import org.junit.jupiter.api.Test;
import servicehub.ds.ArrayList;
import servicehub.model.ServiceRequest;

import static org.junit.jupiter.api.Assertions.*;

class GreedyAndDPTest {

    private ServiceRequest req(String id, int urgency, double cost) {
        return new ServiceRequest(id, "L001", "L002", "Maintenance",
                urgency, "2026-07-01T08:00", "2026-07-01T12:00", "NEW", cost);
    }

    private ArrayList<ServiceRequest> sampleRequests() {
        ArrayList<ServiceRequest> list = new ArrayList<>();
        list.add(req("Q1", 5, 600));
        list.add(req("Q2", 3, 300));
        list.add(req("Q3", 4, 500));
        list.add(req("Q4", 2, 200));
        return list;
    }

    @Test
    void greedyStaysWithinBudget() {
        ArrayList<ServiceRequest> selected = GreedyOptimizer.selectRequestsGreedy(sampleRequests(), 700);
        double total = 0;
        for (int i = 0; i < selected.size(); i++) total += selected.get(i).getCost();
        assertTrue(total <= 700);
        assertFalse(selected.isEmpty());
    }

    @Test
    void greedyEmptyBudget() {
        ArrayList<ServiceRequest> selected = GreedyOptimizer.selectRequestsGreedy(sampleRequests(), 0);
        assertEquals(0, selected.size());
        assertEquals(0, GreedyOptimizer.selectRequestsGreedy(null, 100).size());
    }

    @Test
    void greedyCounterExampleDemonstrated() {
        ArrayList<ServiceRequest> list = new ArrayList<>();
        list.add(req("X", 6, 6));
        list.add(req("Y", 5, 5));
        list.add(req("Z", 5, 5));
        ArrayList<ServiceRequest> greedy = GreedyOptimizer.selectRequestsGreedy(list, 10);
        ArrayList<ServiceRequest> optimal = DynamicProgrammingOptimizer.selectRequests(list, 10);
        double greedyUrgency = urgencySum(greedy);
        double optimalUrgency = urgencySum(optimal);
        assertTrue(optimalUrgency > greedyUrgency, "Greedy=" + greedyUrgency + " Optimal=" + optimalUrgency);
        assertEquals(10, optimalUrgency);
    }

    private double urgencySum(ArrayList<ServiceRequest> list) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) sum += list.get(i).getUrgency();
        return sum;
    }

    @Test
    void dpSelectsFeasibleOptimal() {
        ArrayList<ServiceRequest> selected = DynamicProgrammingOptimizer.selectRequests(sampleRequests(), 700);
        double totalCost = 0;
        double totalUrgency = 0;
        for (int i = 0; i < selected.size(); i++) {
            totalCost += selected.get(i).getCost();
            totalUrgency += selected.get(i).getUrgency();
        }
        assertTrue(totalCost <= 700);
        // best feasible: Q1(5,600)+Q4(2,200)=800 too much; Q1+Q2=900 too much;
        // Q1+... : Q1(600)+Q2(300)=900 no; Q1(600) alone=5; Q3(500)+Q4(200)=700 => 6 urgency
        assertEquals(700, totalCost, 1e-9);
        assertEquals(6, totalUrgency, 1e-9);
    }

    @Test
    void dpEmptyInputReturnsEmpty() {
        assertEquals(0, DynamicProgrammingOptimizer.selectRequests(new ArrayList<>(), 100).size());
        assertEquals(0, DynamicProgrammingOptimizer.selectRequests(null, 100).size());
    }

    @Test
    void dpMaximisesUrgencyAgainstGreedy() {
        ArrayList<ServiceRequest> list = sampleRequests();
        ArrayList<ServiceRequest> greedy = GreedyOptimizer.selectRequestsGreedy(list, 700);
        ArrayList<ServiceRequest> optimal = DynamicProgrammingOptimizer.selectRequests(list, 700);
        assertTrue(urgencySum(optimal) >= urgencySum(greedy));
    }
}
