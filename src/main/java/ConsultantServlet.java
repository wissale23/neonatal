import java.util.List;

public class ConsultantServlet {


    private double lower;
    private double upper;

    public ConsultantServlet(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double getLower() {
        return this.lower;
    }

    public double getUpper() {
        return this.upper;
    }

    public void setLower(double lowNumber) {
        this.lower = lowNumber;
    }

    public void setUpper(double upNumber) {
        this.upper = upNumber;
    }

    public String consultPage(List<Double> timeArrayString, List<Double> rawArrayString, List<Double> smoothDataString, List<Double> sampleValues, List<Double> sampleTimes, String pathString) {

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString, lower, upper, sampleValues, sampleTimes);

        return glucoseChart.generateHTML() + "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "margin: 20px auto;"
                + "text-align: center;'>"

                + "<h3 style='color: black;'>Blood Glucose Safety Range (mM)</h3>"

                + "<form method='POST' action='" + pathString + "/consultants'>"
                + "<div>"
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Lower limit: </span>"
                + "<input type='text' name='lowerLimit' step='0.1' value='" + this.getLower() + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Upper limit: </span>"
                + "<input type='text' name='upperLimit' step='0.1' value='" + this.getUpper() + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Apply</button>"

                + "</div>"
                + "</form>"
                + "</div>"
                + "</body></html>";

    }
}