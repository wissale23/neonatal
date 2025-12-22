import java.util.List;

public class GlucoseChart {

    private final List<Double> timeData;
    private final List<Double> rawData;
    private final List<Double> smoothData;
    private final double lower;
    private final double upper;
    private List<Double> sampleTimes;
    private List<Double> sampleValues;
    private List<Double> feedingStarts;
    private List<Double> feedingDurations;
    private List<String> feedingTypes;

    // Instantiate Data and Inputs
    public GlucoseChart(List<Double> timeData, List<Double> rawData, List<Double> smoothData,
                        double lower, double upper,List<Double> sampleValues,List<Double> sampleTimes,
                        List<Double> feedingStarts, List<Double> feedingDurations, List<String> feedingTypes) {
        this.timeData = timeData;
        this.rawData = rawData;
        this.smoothData = smoothData;
        this.lower = lower;
        this.upper = upper;
        this.sampleValues = sampleValues;
        this.sampleTimes = sampleTimes;
        this.feedingStarts = feedingStarts;
        this.feedingDurations = feedingDurations;
        this.feedingTypes = feedingTypes;
    }

    // Plot Heel Prick Sample Inputs
    public String getSamples(){
        String sampleTriangles = "";
        if (sampleTimes != null && sampleValues != null) {
            for (int i = 0; i < sampleTimes.size(); i++) {
                sampleTriangles = sampleTriangles + "sample" + i + ": {\n" +
                        "  type: 'point',\n" +
                        "  xValue: " + sampleTimes.get(i) + ",\n" +
                        "  yValue: " + sampleValues.get(i) + ",\n" +
                        "  yScaleID: 'y2',\n" +
                        "  radius: 6,\n" +
                        "  pointStyle: 'triangle',\n" +
                        "  backgroundColor: 'rgb(220,0,0)',\n" +
                        "  borderColor: 'rgb(220,0,0)'\n" +
                        "},\n";
            }
        }
        return sampleTriangles;
    }

    // Plot Feeding Times, Durations, and Types
    public String getFeedings(){
        String feedingBars = "";
        if (feedingStarts != null && feedingDurations != null && feedingTypes != null) {
            for (int i = 0; i < feedingStarts.size(); i++) {
                feedingBars = feedingBars + "feed" + i + ": {\n" +
                        "  type: 'box',\n" +
                        "  xMin: " + feedingStarts.get(i) + ",\n" +
                        "  xMax: " + start + feedingDurations.get(i) + ",\n" +
                        "  backgroundColor: 'rgba(0,255,0,0.25)',\n" +
                        "  borderColor: 'rgb(0,0,0)',\n" +
                        "  borderWidth: 1,\n" +
                        "  drawTime: 'beforeDatasetsDraw',\n" +
                        "  label: {\n" +
                        "    display: true,\n" +
                        "    content: '" + feedingTypes.get(i) + "',\n" +
                        "    position: 'center',\n" +
                        "    color: '#003366',\n" +
                        "    font: { size: 11, weight: 'bold' }\n" +
                        "  }\n" +
                        " },\n";
            }
        }
        return feedingBars;
    }

    public String generateHTML() {
        String timeArray = timeData.toString();
        String rawArray = rawData.toString();
        String smoothArray = smoothData.toString();

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
            // Header
                "<head>\n" +
                "  <title>Glucose Chart</title>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                "</head>\n" +

            // Body
                "<body>\n" +
                "  <h2>Glucose Levels</h2>\n" +
                "  <canvas id='glucoseChart' width='800' height='400'></canvas>\n" +
                "  <script>\n" +
                "    const labels = " + timeArray + ";\n" +
                "    const rawData = " + rawArray + ";\n" +
                "    const smoothData = " + smoothArray + ";\n" +
                "    const LOWER = " + lower + ";\n" +
                "    const UPPER = " + upper+ ";\n" +
                "\n" +
            // Plot Chart
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
            // Plot Raw Skin Glucose, Filtered (Smoothed) Skin Glucose, Estimated Blood Glucose
                "          { label: 'Raw Skin Glucose', data: rawData, yAxisID: 'y', borderColor: 'rgb(252,168,168)', borderWidth: 3, fill: false, order: 2, pointRadius: 0 },\n" + 
                "          { label: 'Smoothed Skin Glucose', data: smoothData, yAxisID: 'y', borderColor: 'rgb(220,25,25)', borderWidth: 1.5, fill: false, order: 1, pointRadius: 0 },\n" +
                "          { label: 'Estimated Blood Glucose', data: smoothData.map(v => (v - 1.5) / 3.5), yAxisID: 'y2', borderColor: 'rgb(255,210,210)', borderWidth: 6, fill: false, order: 3, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
            // Define Axes
                "          y: {position: 'left',  min: 0, max: 90, title: {display: true, text: 'Skin Glucose (µM)'} },\n" +
                "          y2: { position: 'right', min: 0, max: 12, title: {display: true, text: 'Blood Glucose (mM)'} },\n" +
                "          x: { type: 'linear', min: 11.50, max: 14, title: { display: true, text: 'Time (hours)'}, ticks:{stepSize: 0.1} }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
            // Plot Acceptable Range
                "              low: { type: 'box', yScaleID: 'y2', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Normal Range', display: true, color: '#1b5e20', font: { size: 12 } } },\n" +
                "              high: { type: 'box', yScaleID: 'y2',  yMin: UPPER, yMax: 12, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
            // Plot Heel Prick Sample inputs
                               this.getSamples() +
                               this.getFeedings() +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>\n";
    }
}
