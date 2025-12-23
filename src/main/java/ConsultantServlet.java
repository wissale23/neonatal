import javax.servlet.http.HttpSession;
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

    public String consultPage(HttpSession session, List<Double>  timeArrayString, List<Double>  rawArrayString, List<Double>  smoothDataString, List<Double> sampleValues, List<Double> sampleTimes, List<Double> feedStarts, List<Double> feedDurations, List<String> feedTypes,List<String> comments, String pathString){

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString,lower, upper,sampleValues,sampleTimes,feedStarts,feedDurations,feedTypes,comments);

        return glucoseChart.generateHTML()+ "<div style='background-color: #fedae6; "
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
                + "<input type='text' name='lowerLimit' step='0.1' value='" + lower + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Upper limit: </span>"
                + "<input type='text' name='upperLimit' step='0.1' value='" + upper + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Apply</button>"

                + "</div>"
                + "</form>"
                + "</div>"
                + "</body></html>";

    }




    private static final String alertCSS =
            "<style>" +
                    ".alert-bubble {" +
                    "position: sticky;" +
                    "top: 15px;" +
                    "margin: 0 auto 20px auto;" +
                    "width: fit-content;" +
                    "max-width: 90%;" +
                    "background-color: #ffebee;" +
                    "color: #b71c1c;" +
                    "padding: 14px 20px;" +
                    "border-radius: 12px;" +
                    "box-shadow: 0 4px 10px rgba(0,0,0,0.15);" +
                    "font-weight: 600;" +
                    "z-index: 1000;" +
                    "display: flex;" +
                    "align-items: center;" +
                    "animation: fadeIn 0.5s ease-out;" +
                    "}" +

                    ".alert-high {" +
                    "background-color: #ffebee;" +
                    "border: 2px solid #b71c1c;" +
                    "color: #b71c1c;" +
                    "}" +

                    ".alert-low {" +
                    "background-color: #e3f2fd;" +
                    "border: 2px solid #0d47a1;" +
                    "color: #0d47a1;" +
                    "}" +

                    ".alert-close {" +
                    "margin-left: 15px;" +
                    "cursor: pointer;" +
                    "font-weight: bold;" +
                    "border: none;" +
                    "background: none;" +
                    "font-size: 16px;" +
                    "}" +

                    "@keyframes fadeIn {" +
                    "from {opacity: 0; transform: translateY(-10px);}" +
                    "to {opacity: 1; transform: translateY(0);}" +
                    "}" +

            "</style>";

    private String buildWarningHtml(List<Double> glucoseData) {
        WarningSystem warningSystem = new WarningSystem(this);
        double latestGlucose = glucoseData.get(glucoseData.size() - 1);

        if (!warningSystem.isUnsafe(latestGlucose)) {
            return "";
        }

        String alertCategory = warningSystem.isAboveRange(latestGlucose)
                ? "alert-bubble alert-high"
                : "alert-bubble alert-low";

        return "<div class='"+ alertCategory +"'>" +
                warningSystem.getWarningMessage(latestGlucose)+
                "<button class='alert-close'" +
                "onclick=\"this.parentElement.style.display='none'\">&times</button>" +
                "</div>";
    }

}

