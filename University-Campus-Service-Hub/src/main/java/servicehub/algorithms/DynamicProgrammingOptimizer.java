package servicehub.algorithms;
import servicehub.ds.ArrayList;
import servicehub.model.ServiceRequest; 


public class DynamicProgrammingOptimizer {
    private static final double BUDGET = 10500.0; // Team parameter: First 3 digits of average index * 100

    public static ArrayList<ServiceRequest> selectRequests(ArrayList<ServiceRequest> requests) {
        int n = requests.size();
        int budgetInt = (int) BUDGET;
        int[][] dp = new int[n + 1][budgetInt + 1];

        int[] costs = new int[n];
        int[] urgencies = new int[n];
        for (int i = 0; i < n; i++) {
            costs[i] = (int) requests.get(i).getCost();
            urgencies[i] = requests.get(i).getUrgency();
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

        ArrayList<ServiceRequest> selected = new ArrayList<>();
        int res = dp[n][budgetInt];
        int w = budgetInt;
        for (int i = n; i > 0 && res > 0; i--) {
            if (res != dp[i - 1][w]) {
                selected.add(requests.get(i - 1));
                res -= urgencies[i - 1];
                w -= costs[i - 1];
            }
        }
        return selected;
    }
}
