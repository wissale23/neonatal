import javax.servlet.http.HttpSession;
import java.util.List;

public class Consultant extends Adult implements Pageable {

    public Consultant(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }


    public String rangeLayout(String pathString,double lower,double upper){
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

    public String consultPage(GlucoseChart glucoseChart,String pathString,double lower,double upper){

        return glucoseChart.generateHTML()
                + this.rangeLayout(pathString,lower,upper)
                + glucoseChart.commentsInpLayout()
                + "</body></html>";

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Load data from files
        HttpSession session = req.getSession(false);

        List<Double> timeData = loadDataFromResource(TIME_FILE);
        List<Double> rawData = loadDataFromResource(RAW_FILE);
        List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);
        
        GlucoseChart glucoseChart = new GlucoseChart(session, req,timeData, rawData, smoothData,feedTypes);
        double lower = glucoseChart.getLimInp().get(0);
        double upper = glucoseChart.getLimInp().get(1);
        
            // Consultants only view the file data, no user input
        resp.getWriter().write(consultPage(glucoseChart, req.getContextPath(), lower, upper));
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



    


