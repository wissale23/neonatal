package plot;

import org.jfree.chart.*;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.awt.*;

public class GlucosePlotFrame extends JFrame {

    public GlucosePlotFrame(String title, XYSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Time",
                "Skin Glucose (µM)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // — Smoothed (series 0) —
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesPaint(0, new Color(142, 11, 11)); // light red

        // — Raw (series 1) —
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesStroke(1, new BasicStroke(6.0f));
        renderer.setSeriesPaint(1, new Color(255, 160, 160)); // dark red

        plot.setRenderer(renderer);

        // — Acceptable range shading —
        double lower = 2.6;  // change to your real lower bound
        double upper = 10.0;  // change to your real upper bound

        IntervalMarker range = new IntervalMarker(lower, upper);
        range.setLabel("Normal Range");
        range.setLabelFont(new Font("SansSerif", Font.ITALIC, 12));
        range.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        range.setLabelTextAnchor(TextAnchor.TOP_LEFT);
        range.setPaint(new Color(144, 238, 144, 80)); // light green with transparency

        plot.addRangeMarker(range, Layer.BACKGROUND);

        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}