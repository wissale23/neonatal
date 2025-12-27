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

    public String rangeLayout(String pathString){
        return "<div style='background-color: #fedae6; "
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
                + "</div>";
    }    

    public String consultPage(HttpSession session, List<Double>  timeArrayString, List<Double>  rawArrayString, List<Double>  smoothDataString, List<Double> sampleValues, List<Double> sampleTimes, List<Double> feedStarts, List<Double> feedDurations, List<String> feedTypes,List<String> comments, String pathString){

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString,lower, upper,sampleValues,sampleTimes,feedStarts,feedDurations,feedTypes,comments);

        return glucoseChart.generateHTML()
                + this.rangeLayout(pathString)
                + glucoseChart.commentsInpLayout()
                + "</body></html>";

    }

}

