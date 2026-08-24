package servicehub.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Reusable line-chart component built on JFreeChart. Supports any number of
 * named (x, y) series, live display inside Swing, direct PNG image download
 * and CSV export of the plotted values.
 */
public class LineChart extends JPanel {

    private static final Color[] SERIES_COLORS = {
            Color.BLUE, Color.RED, new Color(0, 128, 0), new Color(255, 128, 0),
            new Color(128, 0, 128), Color.CYAN.darker(), Color.PINK.darker(),
            Color.BLACK
    };

    public static final int DEFAULT_WIDTH = 960;
    public static final int DEFAULT_HEIGHT = 400;

    private final XYSeriesCollection dataset = new XYSeriesCollection();
    private final JFreeChart chart;
    private final ChartPanel chartPanel;

    public LineChart(String title, String xAxisLabel, String yAxisLabel) {
        super(new BorderLayout());
        chart = ChartFactory.createXYLineChart(
                title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        styleChart();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        chartPanel.setMouseWheelEnabled(true);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void styleChart() {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220, 220, 220));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2.2f));
            plot.getRenderer().setSeriesPaint(i, SERIES_COLORS[i % SERIES_COLORS.length]);
        }
    }

    /**
     * Adds a named series. Arrays must be the same length and are copied,
     * so callers may reuse them afterwards.
     */
    public void addSeries(String name, double[] xs, double[] ys) {
        if (xs.length != ys.length) {
            throw new IllegalArgumentException("x/y lengths differ for series " + name);
        }
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < xs.length; i++) {
            series.add(xs[i], ys[i]);
        }
        dataset.addSeries(series);
        styleChart();
    }

    public void clearSeries() {
        dataset.removeAllSeries();
        styleChart();
    }

    public boolean isEmpty() {
        return dataset.getSeriesCount() == 0;
    }

    public String getChartTitle() {
        return chart.getTitle() == null ? "chart" : chart.getTitle().getText();
    }

    /**
     * Downloads the rendered chart as a PNG image file.
     *
     * @return the written file, or null when saving failed
     */
    public File savePng(File target) {
        try {
            File out = target;
            if (out.getName().lastIndexOf('.') < 0) {
                out = new File(target.getParentFile(), target.getName() + ".png");
            }
            ChartUtils.saveChartAsPNG(out, chart, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            return out;
        } catch (Exception e) {
            System.err.println("Could not save chart image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Exports the plotted values as CSV with columns series,x,y.
     */
    public boolean exportCsv(File target) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(target))) {
            writer.println("series,x,y");
            for (int s = 0; s < dataset.getSeriesCount(); s++) {
                XYSeries series = dataset.getSeries(s);
                for (int i = 0; i < series.getItemCount(); i++) {
                    writer.printf("%s,%s,%s%n",
                            series.getKey(),
                            series.getX(i),
                            series.getY(i));
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Could not export chart CSV: " + e.getMessage());
            return false;
        }
    }

    /** Convenience builder used by the experiments tab. */
    public static LineChart create(String title, String yAxisLabel,
                                   List<String> seriesNames, List<double[]> xs, List<double[]> ys) {
        LineChart c = new LineChart(title, "Input size", yAxisLabel);
        for (int i = 0; i < seriesNames.size(); i++) {
            c.addSeries(seriesNames.get(i), xs.get(i), ys.get(i));
        }
        return c;
    }

    /** Small demo window kept for standalone inspection. */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            LineChart demo = new LineChart("Linear vs binary search", "Input size", "Avg time (ns)");
            double[] x = {100, 500, 1000, 5000, 10000};
            demo.addSeries("LinearSearch", x, new double[]{900, 4500, 9200, 47000, 95000});
            demo.addSeries("BinarySearch", x, new double[]{300, 350, 400, 480, 550});
            javax.swing.JFrame frame = new javax.swing.JFrame("LineChart preview");
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.add(demo);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
