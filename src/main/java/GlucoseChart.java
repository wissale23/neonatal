import java.util.List;

public class GlucoseChart {

    private final List<Double> timeData;
    private final List<Double> rawData;
    private final List<Double> smoothData;
    private final double lower;
    private final double upper;
    private List<Double> sampleTimes;
    private List<Double> sampleValues;


    public GlucoseChart(List<Double> timeData, List<Double> rawData, List<Double> smoothData,
                        double lower, double upper,List<Double> sampleValues,List<Double> sampleTimes) {
        this.timeData = timeData;
        this.rawData = rawData;
        this.smoothData = smoothData;
        this.lower = lower;
        this.upper = upper;
        this.sampleValues = sampleValues;
        this.sampleTimes = sampleTimes;

    }

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
                        "  backgroundColor: '#000',\n" +
                        "  borderColor: '#000'\n" +
                        "},\n";
            }
        }
        return sampleTriangles;


    }



    public String generateHTML() {
        String timeArray = timeData.toString();
        String rawArray = rawData.toString();
        String smoothArray = smoothData.toString();

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Glucose Chart</title>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                "</head>\n" +
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
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
                "          { label: 'Raw Glucose', data: rawData, yAxisID: 'y', borderColor: 'rgba(255,160,160)', borderWidth: 0.5, fill: false, order: 2, pointRadius: 0 },\n" +
                "          { label: 'Smoothed Glucose', data: smoothData, yAxisID: 'y', borderColor: 'rgb(142,11,11)', borderWidth: 0.25, fill: false, order: 1, pointRadius: 0 },\n" +
                "          { label: 'Estimated Blood Glucose', data: rawData.map(v => (v - 1.5) / 3.5), yAxisID: 'y2', borderColor: 'rgba(255,160,160)', borderWidth: 6, fill: false, order: 3, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
                "          y: {position: 'left',  min: 0, max: 40, title: {display: true, text: 'Skin Glucose (µM)'} },\n" +
                "          y2: { position: 'right', min: 0, max: 8, title: {display: true, text: 'Blood Glucose (mM)'} },\n" +
                "          x: { type: 'linear', min: 11.500188, max: 13.697, title: { display: true, text: 'Time (hours)'}, ticks:{stepSize: 0.066} }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
                "              low: { type: 'box', yScaleID: 'y2', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Normal Range', display: true, color: '#1b5e20', font: { size: 12, style: 'italic' } } },\n" +
                "              high: { type: 'box', yScaleID: 'y2',  yMin: UPPER, yMax: 8, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                               this.getSamples() +

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
