package servicehub.algorithms;

import servicehub.ds.ArrayList;
import servicehub.model.ServiceRequest;

/**
 * Dynamic-programming 0/1 knapsack selection of service requests.
 * Maximises total urgency subject to a GHS budget (team parameter).
 */
public class DynamicProgrammingOptimizer {

    /** Team parameter: budget in GHS (first three digits of the team average index * 100). */
    public static final double TEAM_BUDGET = 10500.0;

    /**
     * Selects requests with the default team budget.
     */
    public static ArrayList<ServiceRequest> selectRequests(ArrayList<ServiceRequest> requests) {
        return selectRequests(requests, TEAM_BUDGET);
    }

    /**
     * Selects requests maximising total urgency under the given budget using
     * tabulated 0/1 knapsack DP.
     *
     * @param requests candidate requests (must have non-negative costs)
     * @param budget   GHS budget
     * @return optimal request set (urgency-maximising)
     */
    public static ArrayList<ServiceRequest> selectRequests(ArrayList<ServiceRequest> requests, double budget) {
        ArrayList<ServiceRequest> selected = new ArrayList<>();
        if (requests == null || requests.size() == 0 || budget <= 0) return selected;

        int n = requests.size();
        int budgetInt = (int) budget;
        int[][] dp = new int[n + 1][budgetInt + 1];

        int[] costs = new int[n];
        int[] urgencies = new int[n];
        for (int i = 0; i < n; i++) {
            costs[i] = (int) Math.round(requests.get(i).getCost());
            urgencies[i] = requests.get(i).getUrgency();
            if (costs[i] < 0) throw new IllegalArgumentException("Request costs must be non-negative");
        }

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= budgetInt; w++) {
                if (costs[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - costs[i - 1]] + urgencies[i - 1]);
                } else {
                    dp[i][w] = dp[i - 1][w];
                }
            }
        }

        // Reconstruct selected items
        int w = budgetInt;
        for (int i = n; i > 0; i--) {
            if (costs[i - 1] <= w && dp[i][w] != dp[i - 1][w]) {
                selected.add(requests.get(i - 1));
                w -= costs[i - 1];
            }
        }
        return selected;
    }
}
