import java.util.List;

public class ParentChart {
    private final Baby baby;

    // Instantiate Data and Inputs
    public ParentChart(Baby baby) {
        this.baby = baby;
    }

    public String generateHTML() {
        String timeArray = baby.getTimeData().toString();
        String smoothArray = baby.getSmoothData().toString();

        // Set x axes range as scaled from time data
        double minTime = 0.0;
        double maxTime = 24.0;
        List<Double> times = baby.getTimeData();
        if (times != null && !times.isEmpty()) {
            minTime = times.get(0);
            maxTime = times.get(0);
            for (double t : times) {
                if (t < minTime) minTime = t;
                if (t > maxTime) maxTime = t;
            }
        }
        int xMin = (int) Math.floor(minTime);
        int xMax = (int) Math.ceil(maxTime);

        return "" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                "  <h2>Glucose Levels</h2>\n" +
                "  <canvas id='glucoseChart' width='800' height='400'></canvas>\n" +
                "  <script>\n" +
                "    const labels = " + timeArray + ";\n" +
                "    const smoothData = " + smoothArray + ";\n" +
                "    const LOWER = " + baby.getLowerRange() + ";\n" +
                "    const UPPER = " + baby.getUpperRange() + ";\n" +
                "\n" +
                // Plot Chart
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
                // Plot only Estimated Blood Glucose to simplify
                "          { label: 'Estimated Blood Glucose', data: smoothData.map(v => (v - 1.5) / 3.5), yAxisID: 'y', borderColor: 'rgb(220,25,25)', borderWidth: 3, fill: false, order: 3, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
                // Define Axes
                "          y: {position: 'left',  min: 0, max: 12, title: {display: true, text: 'Blood Glucose (mM)', font: { size: 16, weight: 'bold' } } },\n" +
                "          x: { type: 'linear', min: " + xMin + ", max: " + xMax + ",\n" +
                "            title: { display: true, text: 'Time (hours)', font: { size: 16, weight: 'bold' } },\n" +
                "            ticks: {\n" +
                "              stepSize: (0.5 / 6),\n" +
                "              callback: function(value) {\n" +
                "                if (Math.abs(value % 0.5) < 1e-6) {\n" +
                "                  return value.toFixed(1);\n" +
                "                }\n" +
                "                return '';\n" +
                "              }\n" +
                "            },\n" +
                "            grid: { drawTicks: true }\n" +
                "          }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
                // Plot Acceptable Range only, with no labels to simplify
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', display: true, color: '#1b5e20', font: { size: 12 } } },\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n";
    }
}
