package servicehub.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LineChart extends JPanel {

    // Sample data coordinates to plot: (x, y)
    private final int[] xPoints = {50, 150, 250, 350, 450, 550};
    private final int[] yPoints = {400, 300, 350, 200, 150, 100};

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(50, 50, 50, 450);   // Y-Axis
        g2d.drawLine(50, 450, 600, 450); // X-Axis

        // Draw Graph Line
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < xPoints.length - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        // Draw Data Point Dots
        g2d.setColor(Color.RED);
        for (int i = 0; i < xPoints.length; i++) {
            g2d.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Native Java Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new LineChart());
        frame.setSize(700, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
