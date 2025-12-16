package app;

import loader.DataLoader;
import plot.GlucosePlotFrame;
import org.jfree.data.xy.*;

import javax.swing.*;
import java.util.List;

public class GlucoseApp {

    public static void main(String[] args) {

        try {
            // Load data from resources
            List<Double> time = DataLoader.load("t_glu.txt");
            List<Double> raw = DataLoader.load("glu_uM_unsmoothed.txt");
            List<Double> smooth = DataLoader.load("glu_uM_smoothed.txt");

            // Create series
            XYSeries smoothSeries = new XYSeries("Smoothed Skin Glucose"); // Series 0
            XYSeries rawSeries = new XYSeries("Raw Skin Glucose"); // Series 1

            for (int i = 0; i < time.size(); i++) {
                rawSeries.add(time.get(i), raw.get(i));
                smoothSeries.add(time.get(i), smooth.get(i));
            }

            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(rawSeries);
            dataset.addSeries(smoothSeries);

            // Launch UI
            SwingUtilities.invokeLater(() ->
                    new GlucosePlotFrame("Neonatal Glucose Levels", dataset)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}