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
                // Reference 4 - moving average taken from
                "    // Function to compute moving average\n" +
                "    function movingAverage(data, k) {\n" +
                "      return data.map((_, i) => {\n" +
                "        if (i < k - 1) return null; // fill = NA, same as R example\n" +
                "        let sum = 0;\n" +
                "        for (let j = i - k + 1; j <= i; j++) {\n" +
                "          sum += data[j];\n" +
                "        }\n" +
                "        return sum / k;\n" +
                "      });\n" +
                "    }\n" +
                "\n" +
                // Estimated Blood Glucose passed through MA filter
                "    const estimatedBlood = smoothData.map(v => (v - 1.5) / 3.5);\n" +
                "    const windowSize = 5; // Adjust smoothing window (similar to k in R rollmean)\n" +
                "    const smoothedEstimatedBlood = movingAverage(estimatedBlood, windowSize);\n" +
                // Plot Chart
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        datasets: [\n" +
                // Plot only Estimated Blood Glucose to simplify
                "          { label: 'Estimated Blood Glucose',\n" +
                "            data: labels.map((t, i) => ({ x: t, y: estimatedBlood[i] })),\n" +
                "            yAxisID: 'y', borderColor: 'rgb(220,25,25)', borderWidth: 3,\n" +
                "            fill: false, order: 3, pointRadius: 0 }\n" +
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
                "              normal: { type: 'box', yScaleID: 'y', yMin: LOWER, yMax: UPPER,\n" +
                "                backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', display: true }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n";
    }
}
