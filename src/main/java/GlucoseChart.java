import java.sql.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class GlucoseChart {
    private final Baby baby;

    // Instantiate Data and Inputs
    public GlucoseChart(Baby baby) {
        this.baby = baby;
    }

    // Plot Heel Prick Sample Inputs
    public String getSamples(){
        String sampleTriangles = "";
        for (int i = 0; i < baby.getSampleTimes().size(); i++) {
            sampleTriangles += "sample" + i + ": {\n" +
                    "  type: 'point',\n" +
                    "  xValue: " + baby.getSampleTimes().get(i) + ",\n" +
                    "  yValue: " + baby.getSampleValues().get(i) + ",\n" +
                    "  yScaleID: 'y2',\n" +
                    "  radius: 6,\n" +
                    "  pointStyle: 'triangle',\n" +
                    "  backgroundColor: 'rgb(220,0,0)',\n" +
                    "  borderColor: 'rgb(220,0,0)'\n" +
                    "},\n";
        }
        return sampleTriangles;
    }

    // Display Comments Box
    public String commentsInpLayout(List<String> comments) {

        String options = "";
        for (int i = 0; i < comments.size(); i++) {
            options += "<option value='" + i + "'>Comment " + (i + 1) + "</option>";
        }
    
        String commentsStrings = "[";
        for (int i = 0; i < comments.size(); i++) {
            commentsStrings += "\"" +
                comments.get(i)
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                + "\"";
    
            if (i < comments.size() - 1) {
                commentsStrings += ",";
            }
        }
        commentsStrings += "]";
    
        return "<script>" +
            "const comments = " + commentsStrings + ";" +
            "function showComment(index) {" +
            " if (index < 0) return;" +
            " document.getElementById('commentCanvas').innerText = comments[index];" +
            "}" +
            "</script>" +
    
            "<div id='commentCanvas' style='" +
            "margin-top:15px; padding:10px; border:2px solid black;" +
            "width:400px; min-height:60px; max-height:150px; overflow-y:auto;'>" +
            "Select a comment to view it</div>" +
    
            "<select onchange='showComment(Number(this.value))'>" +
            "<option value='-1' selected>See all comments</option>" +
            options +
            "</select>";
    }

    // Plot Feeding Times, Feeding Durations, and Feeding Descriptions
    public String getFeedings(){
        String feedingBars = "";
        for (int i = 0; i < baby.getFeedStarts().size(); i++) {
            double start = baby.getFeedStarts().get(i);
            double end = start + baby.getFeedDurations().get(i);
            String type = baby.getFeedTypes().get(i);

            feedingBars += "feed" + i + ": {\n" +
                    "  type: 'box',\n" +
                    "  xMin: " + start + ",\n" +
                    "  xMax: " + end + ",\n" +
                    "  yScaleID: 'y2',\n" +
                    "  yMin: 0,\n" +
                    "  yMax: 12,\n" +
                    "  backgroundColor: 'rgba(0,0,255,0.25)',\n" +
                    "  borderColor: 'rgb(0,0,255)',\n" +
                    "  borderWidth: 1,\n" +
                    "  drawTime: 'beforeDatasetsDraw',\n" +
                    "  label: {\n" +
                    "    display: true,\n" +
                    "    content: '" + type + "',\n" +
                    "    position: 'center',\n" +
                    "    color: 'rgb(0,0,255)',\n" +
                    "    font: { size: 11, weight: 'bold' }\n" +
                    "  }\n" +
                    " },\n";
        }
        return feedingBars;
    }


    public String logoutButton(HttpServletRequest req) {
        return "<a href='" + req.getContextPath() + "/home' "
                + "style='position:absolute; top:10px; left:10px;"
                + "background-color:#ffc0cb;"  
                + "border:2px solid black;"
                + "padding:5px 10px; border-radius:4px;"
                + "color:black; font-weight:bold; text-decoration:none; cursor:pointer;"
                + "transition: background-color 0.2s;'>"
                + "Home</a>";
    }
    

    // Display warning alert message box
    public String buildWarningHTML(List<Double> glucoseData) {
        
        double latestGlucose = glucoseData.get(glucoseData.size() - 1);
        WarningSystem warningSystem = new WarningSystem(baby.getLowerRange(), baby.getUpperRange());

        return AlertRenderer.buildAlertHTML(warningSystem, latestGlucose);
    }

    // Display Original Glucose Chart with Continuous Monitoring of Skin Glucose Data, and estimated Blood Glucose Data
    // Chart.js properties from https://www.chartjs.org/docs/latest/charts/line.html
    // Reference 1 - taken from https://www.w3schools.com/js/js_graphics_chartjs.asp
    public String generateHTML() {
        String timeArray = baby.getTimeData().toString();
        String rawArray = baby.getRawData().toString();
        String smoothArray = baby.getSmoothData().toString();
        String warningHTML = buildWarningHTML(baby.getRawData());

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

        return AlertRenderer.alertCSS +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                warningHTML +
                "  <h2>Glucose Levels</h2>\n" +
                "  <canvas id='glucoseChart' width='800' height='400'></canvas>\n" +
                "  <script>\n" +
                "    const labels = " + timeArray + ";\n" +
                "    const rawData = " + rawArray + ";\n" +
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
                "          y: {position: 'left',  min: 0, max: 120, title: {display: true, text: 'Skin Glucose (µM)', font: { size: 16, weight: 'bold' } } },\n" +
                "          y2: { position: 'right', min: 0, max: 12, title: {display: true, text: 'Blood Glucose (mM)', font: { size: 16, weight: 'bold' } } },\n" +
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
            // Plot Acceptable Range
                "              low: { type: 'box', yScaleID: 'y2', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Normal Range', display: true, color: '#1b5e20', font: { size: 12 } } },\n" +
                "              high: { type: 'box', yScaleID: 'y2',  yMin: UPPER, yMax: 12, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
            // Plot Heel Prick Sample inputs
                               getSamples() +
                               getFeedings() +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n";
    }
}

