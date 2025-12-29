import javax.servlet.http.HttpSession;
import java.util.List;

public class Consultant extends Adult implements Pageable {


    private double lower;
    private double upper;

    public Consultant(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }


    public double getLower(){
        return this.lower;
    }

    public double getUpper(){
        return this.upper;
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

    public String consultPage(HttpSession session, List<Double>  timeArrayString, List<Double>  rawArrayString, List<Double>  smoothDataString, List<Double> sampleValues, List<Double> sampleTimes, List<Double> feedStarts, List<Double> feedDurations, List<String> feedTypes,List<String> comments, String pathString,double lower,double upper){

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString,lower, upper,sampleValues,sampleTimes,feedStarts,feedDurations,feedTypes,comments);

        return glucoseChart.generateHTML()
                + this.rangeLayout(pathString)
                + glucoseChart.commentsInpLayout()
                + "</body></html>";

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp,double lower,double upper,List<Double> glucoseValues, List<Double> times,List<Double> feedStarts,List<Double> feedDurations,List<String> feedTypes,List<String> comments) throws IOException {
        // Load data from files

        List<Double> timeData = loadDataFromResource(TIME_FILE);
        List<Double> rawData = loadDataFromResource(RAW_FILE);
        List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Consultants only view the file data, no user input
        resp.getWriter().write(consultPage(session, timeData,rawData,smoothData,glucoseValues,times,feedStarts,feedDurations,feedTypes,comments, req.getContextPath(), lower, upper));
    }

    public void doPost(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String lowerString = req.getParameter("lowerLimit");
        String upperString = req.getParameter("upperLimit");
                
        try {
            if (lowerString != null && !lowerString.isEmpty()) {
                session.setAttribute("lowerLimit", Double.parseDouble(lowerString));
            }
            if (upperString != null && !upperString.isEmpty()) {
                session.setAttribute("upperLimit", Double.parseDouble(upperString));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); // change this later to link that displays error message

        }

        resp.sendRedirect(req.getContextPath() + "/consultants");
        return;    
    }
          
}        



    

}
