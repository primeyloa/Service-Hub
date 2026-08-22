package servicehub.algorithms;

import servicehub.ds.ArrayList;
import servicehub.model.ServiceRequest;

/**
 * Greedy optimisation for service-request selection.
 *
 * Greedy rule: pick requests by highest urgency-to-cost ratio first, taking
 * each request only if it still fits within the available budget.
 */
public class GreedyOptimizer {

    public static final double TEAM_BUDGET = 10500.0;

    /** Comparable wrapper enabling the project's custom merge sort. */
    private static final class RatioRequest implements Comparable<RatioRequest> {
        final ServiceRequest request;
        final double ratio;

        RatioRequest(ServiceRequest request) {
            this.request = request;
            double cost = request.getCost();
            this.ratio = cost <= 0 ? Double.POSITIVE_INFINITY : (double) request.getUrgency() / cost;
        }

        @Override
        public int compareTo(RatioRequest other) {
            // descending by ratio so the most "urgent per cedi" comes first
            return Double.compare(other.ratio, this.ratio);
        }
    }

    /**
     * Selects a budget-compatible set of requests using the greedy ratio rule.
     *
     * @param requests candidate requests
     * @param budget   maximum total cost that may be accepted (GHS)
     * @return the greedily selected requests
     */
    public static ArrayList<ServiceRequest> selectRequestsGreedy(ArrayList<ServiceRequest> requests, double budget) {
        ArrayList<ServiceRequest> result = new ArrayList<>();
        if (requests == null || budget <= 0) return result;

        int n = requests.size();
        RatioRequest[] sorted = new RatioRequest[n];
        for (int i = 0; i < n; i++) sorted[i] = new RatioRequest(requests.get(i));
        Sort.mergeSort(sorted);

        double currentCost = 0.0;
        for (RatioRequest rr : sorted) {
            ServiceRequest req = rr.request;
            if (currentCost + req.getCost() <= budget) {
                result.add(req);
                currentCost += req.getCost();
            }
        }
        return result;
    }

    /**
     * A concrete counterexample where the greedy ratio rule fails to find the
     * optimal 0/1 knapsack selection.
     */
    public static String greedyCounterExample() {
        StringBuilder sb = new StringBuilder();
        sb.append("Greedy failure counterexample (0/1 knapsack):\n");
        sb.append("Budget = GHS 10\n");
        sb.append("  X: urgency 6, cost 6 (ratio 1.00)\n");
        sb.append("  Y: urgency 5, cost 5 (ratio 1.00)\n");
        sb.append("  Z: urgency 5, cost 5 (ratio 1.00)\n");
        sb.append("Greedy (highest ratio first): X -> total urgency 6, cost 6\n");
        sb.append("Optimal: Y + Z -> total urgency 10, cost 10\n");
        sb.append("Conclusion: greedy ratio selection is not always optimal for 0/1 selection;\n");
        sb.append("the dynamic-programming knapsack should be used when optimality is required.\n");
        return sb.toString();
    }
}
