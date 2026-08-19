package servicehub.ui;

import servicehub.algorithms.GraphAlgorithms;
import servicehub.ds.ArrayList;
import servicehub.ds.DynamicArray;
import servicehub.engine.EmpiricalBenchmarker;
import servicehub.engine.ServiceSchedulingEngine;
import servicehub.model.AuditEvent;
import servicehub.model.DispatchRecord;
import servicehub.model.Location;
import servicehub.model.Resource;
import servicehub.model.ServiceRequest;
import servicehub.service.CampusService;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Java Swing interface for the University Campus Service Hub.
 * Lets an examiner create service requests, dispatch available personnel and
 * resources, inspect routes, watch the scheduling queues and run the
 * empirical efficiency laboratory without editing source code.
 */
public class ServiceHubGUI extends JFrame {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final CampusService service = new CampusService();

    private JComboBox<String> sourceCombo;
    private JComboBox<String> destinationCombo;
    private JComboBox<String> categoryCombo;
    private JSpinner urgencySpinner;
    private JTextField deadlineField;

    private DefaultTableModel requestModel;
    private DefaultTableModel resourceModel;
    private DefaultTableModel auditModel;
    private DefaultTableModel benchmarkModel;

    private JTextArea routingOutput;
    private JTextArea queueOutput;
    private JTextArea optimisationOutput;

    private JComboBox<String> fromRouteCombo;
    private JComboBox<String> toRouteCombo;
    private JComboBox<String> ruleCombo;

    public ServiceHubGUI() {
        super("University Campus Service Hub - Dispatch Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);

        buildModels();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Requests & Dispatch", buildRequestsPanel());
        tabs.addTab("Resources", buildResourcesPanel());
        tabs.addTab("Routing & Network", buildRoutingPanel());
        tabs.addTab("Scheduling Queues", buildQueuesPanel());
        tabs.addTab("Optimisation", buildOptimisationPanel());
        tabs.addTab("Benchmarks", buildBenchmarksPanel());
        tabs.addTab("Audit Log", buildAuditPanel());

        setContentPane(tabs);
        refreshAll();
    }

    // ------------------------------------------------------------------ models

    private void buildModels() {
        requestModel = new DefaultTableModel(new String[]{
                "ID", "Source", "Destination", "Category", "Urgency",
                "Submitted", "Deadline", "Cost (GHS)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        resourceModel = new DefaultTableModel(new String[]{
                "ID", "Name", "Type", "Home Location", "Capacity", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        auditModel = new DefaultTableModel(new String[]{"Time", "Action", "Details"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        benchmarkModel = new DefaultTableModel(new String[]{
                "Algorithm", "Input Size", "Avg Time (ns)", "Memory (KB)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private String[] locationIds() {
        ArrayList<Location> locations = service.getLocations();
        String[] ids = new String[locations.size()];
        for (int i = 0; i < locations.size(); i++) {
            Location l = locations.get(i);
            ids[i] = l.getLocationId() + " - " + l.getName();
        }
        return ids;
    }

    private static final String[] CATEGORIES = {
            "Maintenance", "Repair", "Cleaning", "Medical Transport",
            "IT Support", "Shuttle", "Laundry", "Document Courier"
    };

    // ------------------------------------------------------------------ panels

    private JPanel buildRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Create a new service request"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Source (department/hall):"), g);
        g.gridx = 1;
        sourceCombo = new JComboBox<>(locationIds());
        sourceCombo.setPreferredSize(new java.awt.Dimension(320, 24));
        form.add(sourceCombo, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Destination (service point):"), g);
        g.gridx = 1;
        destinationCombo = new JComboBox<>(locationIds());
        destinationCombo.setPreferredSize(new java.awt.Dimension(320, 24));
        form.add(destinationCombo, g);

        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("Category:"), g);
        g.gridx = 1;
        categoryCombo = new JComboBox<>(CATEGORIES);
        categoryCombo.setPreferredSize(new java.awt.Dimension(320, 24));
        form.add(categoryCombo, g);

        g.gridx = 0; g.gridy = 3;
        form.add(new JLabel("Urgency (1-5):"), g);
        g.gridx = 1;
        urgencySpinner = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        form.add(urgencySpinner, g);

        g.gridx = 0; g.gridy = 4;
        form.add(new JLabel("Deadline (yyyy-MM-dd'T'HH:mm):"), g);
        g.gridx = 1;
        deadlineField = new JTextField();
        deadlineField.setPreferredSize(new java.awt.Dimension(320, 24));
        form.add(deadlineField, g);

        g.gridx = 0; g.gridy = 5;
        form.add(new JLabel(""), g);
        g.gridx = 1;
        JButton createBtn = new JButton("Create Request");
        createBtn.addActionListener(e -> createRequest());
        form.add(createBtn, g);

        panel.add(form, BorderLayout.NORTH);

        JTable table = new JTable(requestModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(90);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton dispatch = new JButton("Dispatch Next (Priority)");
        dispatch.addActionListener(e -> dispatchNext(ServiceSchedulingEngine.DispatchRule.PRIORITY));
        buttons.add(dispatch);
        JButton dispatchFifo = new JButton("Dispatch Next (FIFO)");
        dispatchFifo.addActionListener(e -> dispatchNext(ServiceSchedulingEngine.DispatchRule.FIFO));
        buttons.add(dispatchFifo);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshAll());
        buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildResourcesPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTable table = new JTable(resourceModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh Resources");
        refresh.addActionListener(e -> refreshAll());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(refresh);
        south.add(new JLabel("AVAILABLE personnel/assets are dispatched first."));
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRoutingPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Campus route network"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("From:"), g);
        g.gridx = 1;
        fromRouteCombo = new JComboBox<>(locationIds());
        fromRouteCombo.setPreferredSize(new java.awt.Dimension(340, 24));
        form.add(fromRouteCombo, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("To:"), g);
        g.gridx = 1;
        toRouteCombo = new JComboBox<>(locationIds());
        toRouteCombo.setPreferredSize(new java.awt.Dimension(340, 24));
        form.add(toRouteCombo, g);

        g.gridx = 1; g.gridy = 2;
        JPanel routeButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton routeBtn = new JButton("Shortest Route (Dijkstra)");
        routeBtn.addActionListener(e -> showRoute());
        routeButtons.add(routeBtn);
        JButton reachBtn = new JButton("Reachable (BFS)");
        reachBtn.addActionListener(e -> showReachable());
        routeButtons.add(reachBtn);
        JButton mstBtn = new JButton("Min Connection Network (Kruskal)");
        mstBtn.addActionListener(e -> showMst());
        routeButtons.add(mstBtn);
        form.add(routeButtons, g);

        panel.add(form, BorderLayout.NORTH);
        routingOutput = new JTextArea(14, 60);
        routingOutput.setEditable(false);
        routingOutput.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        panel.add(new JScrollPane(routingOutput), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildQueuesPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Dispatch rule:"));
        ruleCombo = new JComboBox<>(new String[]{
                "PRIORITY (heap, urgency first)",
                "FIFO (queue)",
                "URGENT_DEQUE (critical jump the line)",
                "ROUND_ROBIN (circular queue)"});
        top.add(ruleCombo);
        JButton showBtn = new JButton("Show Pending Queue");
        showBtn.addActionListener(e -> showQueue());
        top.add(showBtn);
        JButton stepBtn = new JButton("Dispatch One (this rule)");
        stepBtn.addActionListener(e -> stepQueue());
        top.add(stepBtn);
        panel.add(top, BorderLayout.NORTH);

        queueOutput = new JTextArea(20, 60);
        queueOutput.setEditable(false);
        queueOutput.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        panel.add(new JScrollPane(queueOutput), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildOptimisationPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton dpBtn = new JButton("Optimal selection (DP knapsack, GHS 10,500)");
        dpBtn.addActionListener(e -> runDp());
        top.add(dpBtn);
        JButton greedyBtn = new JButton("Greedy selection (urgency/cost ratio)");
        greedyBtn.addActionListener(e -> runGreedy());
        top.add(greedyBtn);
        JButton counterBtn = new JButton("Greedy counterexample");
        counterBtn.addActionListener(e -> optimisationOutput.setText(service.greedyCounterExample()));
        top.add(counterBtn);
        panel.add(top, BorderLayout.NORTH);

        optimisationOutput = new JTextArea(16, 70);
        optimisationOutput.setEditable(false);
        optimisationOutput.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        panel.add(new JScrollPane(optimisationOutput), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBenchmarksPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton runBtn = new JButton("Run Efficiency Benchmarks (100..20,000 inputs)");
        runBtn.addActionListener(e -> runBenchmarks());
        top.add(runBtn);
        JLabel note = new JLabel(" Results are written to reports/benchmark_results.csv and algorithm_runs.");
        note.setForeground(java.awt.Color.GRAY);
        top.add(note);
        panel.add(top, BorderLayout.NORTH);

        JTable table = new JTable(benchmarkModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTable table = new JTable(auditModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ------------------------------------------------------------------ actions

    private String selectedLocationId(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel == null) return null;
        return String.valueOf(sel).split(" - ")[0];
    }

    private void createRequest() {
        String src = selectedLocationId(sourceCombo);
        String dst = selectedLocationId(destinationCombo);
        if (src == null || dst == null) {
            JOptionPane.showMessageDialog(this, "Select valid locations.");
            return;
        }
        String id = "Q" + (900 + service.getRequests().size() + 1);
        int urgency = (Integer) urgencySpinner.getValue();
        String deadline = deadlineField.getText().trim();
        if (deadline.isEmpty()) {
            deadline = LocalDateTime.now().plusHours(6).format(TS);
        }
        ServiceRequest request = new ServiceRequest(
                id, src, dst, String.valueOf(categoryCombo.getSelectedItem()),
                urgency, LocalDateTime.now().format(TS), deadline, "NEW",
                ServiceRequest.defaultCost(urgency));
        service.addRequest(request);
        refreshAll();
        JOptionPane.showMessageDialog(this,
                "Created " + id + " (" + request.getCategory() + ", urgency " + urgency + ", GHS "
                        + request.getCost() + ") from " + service.locationName(src));
    }

    private void dispatchNext(ServiceSchedulingEngine.DispatchRule rule) {
        ArrayList<ServiceRequest> pending = service.pendingRequests();
        if (pending.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending (NEW) requests to dispatch.");
            return;
        }
        ArrayList<Resource> available = service.availableResources();
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available personnel/assets. Mark some resources AVAILABLE first.");
            return;
        }

        ServiceSchedulingEngine engine = new ServiceSchedulingEngine(pending, rule);
        ServiceRequest next = engine.dispatchNext();
        Resource resource = service.nearestResource(next);
        if (resource == null) {
            JOptionPane.showMessageDialog(this, "No available personnel/assets to dispatch " + next.getRequestId());
            return;
        }
        DispatchRecord record = service.dispatch(next, resource);
        service.reload();
        refreshAll();
        JOptionPane.showMessageDialog(this,
                "Dispatched " + record.getRequestId() + " -> " + record.getResourceId()
                        + " (" + record.getResourceType() + ")\nRoute: " + record.getRouteSummary()
                        + "\nETA: " + record.getTravelTimeMin() + " min");
    }

    private void showRoute() {
        String from = selectedLocationId(fromRouteCombo);
        String to = selectedLocationId(toRouteCombo);
        GraphAlgorithms.RouteResult result = service.getRoutingEngine().findShortestPath(from, to);
        StringBuilder sb = new StringBuilder();
        sb.append("Route from ").append(service.locationName(from)).append(" to ")
                .append(service.locationName(to)).append("\n\n");
        if (result.isReachable()) {
            sb.append("Path: ").append(result.pathToString()).append("\n");
            sb.append("Total travel time: ").append(String.format("%.1f min", result.totalWeight)).append("\n");
        } else {
            sb.append("No path found (locations may be disconnected).\n");
        }
        sb.append("\n--- Graph size ---\n");
        sb.append("Vertices: ").append(service.getRoutingEngine().getGraph().vertexCount()).append("\n");
        sb.append("Edges: ").append(service.getRoutingEngine().getGraph().edgeCount()).append("\n");
        routingOutput.setText(sb.toString());
    }

    private void showReachable() {
        String from = selectedLocationId(fromRouteCombo);
        DynamicArray<String> reachable = service.getRoutingEngine().reachableFrom(from);
        StringBuilder sb = new StringBuilder("Reachable from ").append(service.locationName(from)).append(" (BFS):\n\n");
        for (int i = 0; i < reachable.size(); i++) {
            String id = reachable.get(i);
            sb.append("  ").append(id).append(" - ").append(service.locationName(id)).append("\n");
        }
        sb.append("\nTotal reachable: ").append(reachable.size()).append(" of ")
                .append(service.getLocations().size());
        routingOutput.setText(sb.toString());
    }

    private void showMst() {
        GraphAlgorithms.MSTResult mst = service.getRoutingEngine().minimumConnectionNetwork();
        StringBuilder sb = new StringBuilder("Minimum connection network (Kruskal):\n\n");
        for (int i = 0; i < mst.edges.size(); i++) {
            GraphAlgorithms.Edge e = mst.edges.get(i);
            sb.append("  ").append(service.locationName(e.from)).append(" -- ")
                    .append(service.locationName(e.to)).append("  (").append(e.weight).append(")\n");
        }
        sb.append("\nTotal connection cost: ").append(String.format("%.1f", mst.totalWeight));
        routingOutput.setText(sb.toString());
    }

    private void showQueue() {
        ArrayList<ServiceRequest> pending = service.pendingRequests();
        ServiceSchedulingEngine engine = buildEngineFromCombo(pending);
        StringBuilder sb = new StringBuilder("Pending requests (")
                .append(pending.size()).append("), rule = ").append(engine.getRule()).append("\n\n");
        for (ServiceRequest req : pending) {
            sb.append("  ").append(req.getRequestId()).append("  ").append(req.getCategory())
                    .append("  U").append(req.getUrgency()).append("  @")
                    .append(service.locationName(req.getSourceLocationId())).append("\n");
        }
        sb.append("\nNext to dispatch: ");
        ServiceRequest next = engine.peekNext();
        sb.append(next == null ? "(none)" : next.getRequestId() + " [" + next.getCategory() + ", U" + next.getUrgency() + "]");
        queueOutput.setText(sb.toString());
    }

    private void stepQueue() {
        ArrayList<ServiceRequest> pending = service.pendingRequests();
        if (pending.isEmpty()) {
            queueOutput.setText("No pending requests.");
            return;
        }
        ServiceSchedulingEngine engine = buildEngineFromCombo(pending);
        ServiceRequest next = engine.dispatchNext();
        Resource resource = service.nearestResource(next);
        if (resource == null) {
            queueOutput.setText("No available personnel/assets to dispatch " + next.getRequestId());
            return;
        }
        DispatchRecord record = service.dispatch(next, resource);
        service.reload();
        queueOutput.setText("Dispatched " + next.getRequestId() + " -> " + record.getResourceId()
                + "\nRoute: " + record.getRouteSummary() + "\nETA: " + record.getTravelTimeMin() + " min"
                + "\n\nRemaining pending: " + service.pendingRequests().size());
        refreshAll();
    }

    private ServiceSchedulingEngine buildEngineFromCombo(ArrayList<ServiceRequest> pending) {
        ServiceSchedulingEngine.DispatchRule rule = switch (String.valueOf(ruleCombo.getSelectedItem()).split(" ")[0]) {
            case "FIFO" -> ServiceSchedulingEngine.DispatchRule.FIFO;
            case "URGENT_DEQUE" -> ServiceSchedulingEngine.DispatchRule.URGENT_DEQUE;
            case "ROUND_ROBIN" -> ServiceSchedulingEngine.DispatchRule.ROUND_ROBIN;
            default -> ServiceSchedulingEngine.DispatchRule.PRIORITY;
        };
        return new ServiceSchedulingEngine(pending, rule);
    }

    private void runDp() {
        optimisationOutput.setText(dpSelection());
    }

    private String dpSelection() {
        ArrayList<ServiceRequest> selected = service.optimizeWithDP(10500.0);
        StringBuilder sb = new StringBuilder("OPTIMAL selection via 0/1 knapsack DP (budget GHS 10,500):\n\n");
        double cost = 0;
        for (ServiceRequest req : selected) {
            sb.append("  ").append(req.getRequestId()).append("  ").append(req.getCategory())
                    .append("  U").append(req.getUrgency()).append("  GHS ")
                    .append(String.format("%.0f", req.getCost())).append("\n");
            cost += req.getCost();
        }
        sb.append("\nSelected: ").append(selected.size()).append(" requests, total cost GHS ")
                .append(String.format("%.0f", cost));
        return sb.toString();
    }

    private void runGreedy() {
        optimisationOutput.setText(greedySelection());
    }

    private String greedySelection() {
        ArrayList<ServiceRequest> selected = service.greedySelection(10500.0);
        StringBuilder sb = new StringBuilder("GREEDY selection (urgency/cost ratio, budget GHS 10,500):\n\n");
        double cost = 0;
        for (ServiceRequest req : selected) {
            sb.append("  ").append(req.getRequestId()).append("  ").append(req.getCategory())
                    .append("  U").append(req.getUrgency()).append("  GHS ")
                    .append(String.format("%.0f", req.getCost())).append("\n");
            cost += req.getCost();
        }
        sb.append("\nSelected: ").append(selected.size()).append(" requests, total cost GHS ")
                .append(String.format("%.0f", cost));
        sb.append("\n\nNote: greedy is fast but not always optimal - see the counterexample.");
        return sb.toString();
    }

    private void runBenchmarks() {
        setTitle("University Campus Service Hub - running benchmarks...");
        new SwingWorker<Void, Object>() {
            @Override
            protected Void doInBackground() {
                DynamicArray<EmpiricalBenchmarker.BenchmarkResult> results = EmpiricalBenchmarker.runAllBenchmarks();
                publish(results);
                return null;
            }

            @Override
            protected void process(java.util.List<Object> chunks) {
                benchmarkModel.setRowCount(0);
                for (Object chunk : chunks) {
                    @SuppressWarnings("unchecked")
                    DynamicArray<EmpiricalBenchmarker.BenchmarkResult> results =
                            (DynamicArray<EmpiricalBenchmarker.BenchmarkResult>) chunk;
                    for (int i = 0; i < results.size(); i++) {
                        EmpiricalBenchmarker.BenchmarkResult r = results.get(i);
                        benchmarkModel.addRow(new Object[]{
                                r.algorithm(), r.inputSize(), r.avgTimeNs(), r.memoryKb()});
                    }
                }
            }

            @Override
            protected void done() {
                setTitle("University Campus Service Hub - Dispatch Simulator");
                JOptionPane.showMessageDialog(ServiceHubGUI.this,
                        "Benchmarks complete. See the table and reports/benchmark_results.csv");
            }
        }.execute();
    }

    // ------------------------------------------------------------------ refresh

    private void refreshAll() {
        service.reload();

        requestModel.setRowCount(0);
        for (ServiceRequest r : service.getRequests()) {
            requestModel.addRow(new Object[]{
                    r.getRequestId(),
                    service.locationName(r.getSourceLocationId()),
                    r.getDestinationLocationId() == null ? "-"
                            : service.locationName(r.getDestinationLocationId()),
                    r.getCategory(), r.getUrgency(), r.getTimeSubmitted(), r.getDeadline(),
                    String.format("%.0f", r.getCost()), r.getStatus()});
        }

        resourceModel.setRowCount(0);
        for (Resource res : service.getResources()) {
            resourceModel.addRow(new Object[]{
                    res.getResourceId(), res.getName(), res.getResourceType(),
                    service.locationName(res.getHomeLocationId()), res.getCapacity(),
                    res.getAvailabilityStatus()});
        }

        auditModel.setRowCount(0);
        for (AuditEvent e : service.getAuditLog()) {
            auditModel.addRow(new Object[]{e.getTimestamp(), e.getAction(), e.getDetails()});
        }
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> new ServiceHubGUI().setVisible(true));
    }
}
