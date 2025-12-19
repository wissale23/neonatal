import java.util.List;

public class ConsultantServlet {


    private double lower;
    private double upper;

    public ConsultantServlet(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double getLower(){
        return this.lower;
    }

    public double getUpper(){
        return this.upper;
    }

    public void setLower(double lowNumber){
        this.lower = lowNumber;
    }

    public void setUpper(double upNumber){
        this.upper = upNumber;
    }

    public String consultPage(List<Double>  timeArrayString, List<Double>  rawArrayString, List<Double>  smoothDataString, String pathString){

        String warningHtml = buildWarningHtml(rawArrayString);

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>Glucose Chart</title>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                warningHtml +
                "  <h2>Glucose Levels</h2>\n" +
                "  <canvas id='glucoseChart' width='800' height='400'></canvas>\n" +
                "  <script>\n" +
                "    const labels = " + timeArrayString + ";\n" +
                "    const rawData = " + rawArrayString + ";\n" +
                "    const smoothData = " + smoothDataString + ";\n" +
                "    const LOWER = " + getLower() + ";\n" +
                "    const UPPER = " + getUpper() + ";\n" +
                "\n" +
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
                "          { label: 'Raw Glucose', data: rawData, borderColor: 'rgba(255,160,160)', borderWidth: 0.5, fill: false, order: 2, pointRadius: 0 },\n" +
                "          { label: 'Smoothed Glucose', data: smoothData, borderColor: 'rgb(142,11,11)', borderWidth: 0.25, fill: false, order: 1, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
                "          y: { min: 0, max: 40, title: {display: true, text: 'Skin Glucose (µM)'} },\n" +
                "          x: { title: { display: true, text: 'Time (hours)'} }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
                "              low: { type: 'box', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Normal Range', display: true, color: '#1b5e20', font: { size: 12, style: 'italic' } } },\n" +
                "              high: { type: 'box', yMin: UPPER, yMax: 40, backgroundColor: 'rgba(255,0,0,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } }\n" +
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

                + "<h3 style='color: black;'>Glucose Safety Range (&mu;M)</h3>"

                + "<form method='POST' action='" + pathString + "/consultants'>"
                + "<div>"
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Lower limit:</span>"
                + "<input type='text' name='lowerLimit' step='0.1' value='" + this.getLower() + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Upper limit:</span>"
                + "<input type='text' name='upperLimit' step='0.1' value='" + this.getUpper() + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Apply</button>"

                + "</div>"
                + "</form>"
                + "</div>"
                + "</body></html>";

    }

    private String buildWarningHtml(List<Double> glucoseData) {
        WarningSystem warningSystem = new WarningSystem(this);
        double latestGlucose = glucoseData.get(glucoseData.size() - 1);

        if (!warningSystem.isUnsafe(latestGlucose)) {
            return "";
        }
        return "<div style = 'background-color:#ffebee; color:#b71c1c; " +
                "padding:10px; border-radius:6px; margin-bottom:12px;'>" +
                "<strong>Alert:</strong>" +
                warningSystem.getWarningMessage(latestGlucose)+
                "<div>";
    }

}

