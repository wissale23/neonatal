// Unfinished
//import javax.servlet.http.*;
//import java.io.IOException;

//public class NurseServlet {
//  public void display(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        GlucoseGraph graph = new GlucoseGraph(data);
//        resp.getWriter().write(graph.generateHTML(true, false)); // nurse input
//    }
//}

import java.util.List;

public class NurseServlet {


    private double bloodGlucose;
    private double time;
    private List<Double> sampleTimes;
    private List<Double> sampleValues;


    public NurseServlet(double bloodGlucose, double time,List<Double> sampleValues,List<Double> sampleTimes) {
        this.bloodGlucose = bloodGlucose;
        this.time = time;
        this.sampleValues = sampleValues;
        this.sampleTimes = sampleTimes;
    }

    public double getGlucoseValue() {
        return this.bloodGlucose;
    }

    public double getTime() {
        return this.time;
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

    public String nursePage(List<Double> timeArrayString, List<Double> rawArrayString, List<Double> smoothDataString, double lower, double upper, String pathString) {

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
                "    const labels = " + timeArrayString + ";\n" +
                "    const rawData = " + rawArrayString + ";\n" +
                "    const smoothData = " + smoothDataString + ";\n" +
                "    const LOWER = " + lower + ";\n" +
                "    const UPPER = " + upper+ ";\n" +
                "    const GLUCOSE = " + this.getGlucoseValue() + ";\n" +
                "    const TIME = " + this.getTime()+ ";\n" +
                "\n" +
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
                "          { label: 'Raw Glucose', data: rawData, yAxisID: 'y', borderColor: 'rgba(255,160,160)', borderWidth: 0.5, fill: false, order: 2, pointRadius: 0 },\n" +
                "          { label: 'Smoothed Glucose', data: smoothData, yAxisID: 'y', borderColor: 'rgb(142,11,11)', borderWidth: 0.25, fill: false, order: 1, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
                "          y: {position: 'left',  min: 0, max: 40, title: {display: true, text: 'Skin Glucose (µM)'} },\n" +
                "          y2: { position: 'right', min: 0, max: 8, title: {display: true, text: 'Blood Glucose (mM)'} },\n" +
                "          x: { type: 'linear' , title: { display: true, text: 'Time (hours)'} }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
                "              low: { type: 'box', yScaleID: 'y2', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Normal Range', display: true, color: '#1b5e20', font: { size: 12, style: 'italic' } } },\n" +
                "              high: { type: 'box', yScaleID: 'y2',  yMin: UPPER, yMax: 8, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                               this.getSamples()+

                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n"


                + "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "margin: 20px auto;"
                + "text-align: center;'>"

                + "<h3 style='color: black;'>Entering blood glucose values(mM)</h3>"

                + "<form method='POST' action='" + pathString + "/nurses'>"
                + "<div>"
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Sample value: </span>"
                + "<input type='text' name='glucoseInp' step='0.001' value='" + this.getGlucoseValue() + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Time of day: </span>"
                + "<input type='text' name='timeInp' step='0.001' value='" + this.getTime() + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add sample</button>"

                + "</div>"
                + "</form>"
                + "</div>"
                + "</body></html>";

    }

}
