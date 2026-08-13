package com.campus.hub.algorithms;

import com.campus.hub.model.ServiceRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GreedyOptimizer {

    // Greedy choice: select requests with highest urgency-to-cost ratio within budget
    public static List<ServiceRequest> selectRequestsGreedy(List<ServiceRequest> requests, double budget) {
        List<ServiceRequest> sorted = new ArrayList<>(requests);
        sorted.sort((r1, r2) -> Double.compare((double) r2.getUrgency() / r2.getCost(), (double) r1.getUrgency() / r1.getCost()));

        List<ServiceRequest> selected = new ArrayList<>();
        double currentCost = 0.0;

        for (ServiceRequest req : sorted) {
            if (currentCost + req.getCost() <= budget) {
                selected.add(req);
                currentCost += req.getCost();
            }
        }
        return selected;
    }
}
