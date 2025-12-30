import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


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
    public String consultCommentBox(String pathString) {
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "margin: 20px ;"
                + "text-align: center;'>"

                + "<form method='POST' action='" + pathString + "/consultants'>"
                + "<div>"

                + "<textarea name='commInp' "
                + "placeholder='Add a comment...' "
                + "style='width:100%; height:120px; "
                + "padding:8px; box-sizing:border-box; resize:vertical;'></textarea>"
                + "<br/><br/>"

                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add comment</button>"

                + "</div>"
                + "</form>"
                + "</div>";
    }

    public String consultPage(GlucoseChart glucoseChart,String pathString,double lower,double upper,List<String> comments){

        return glucoseChart.generateHTML()
                + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>"
                + this.rangeLayout(pathString,lower,upper)
                + this.consultCommentBox(pathString)
                + "</div>"
                + glucoseChart.commentsInpLayout(comments)
                + "</body></html>";

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Load data from files
        HttpSession session = req.getSession(false);

        List<Double> timeData = getPatients().get(0).getTimeData();
        List<Double> rawData = getPatients().get(0).getRawData();
        List<Double> smoothData = getPatients().get(0).getSmoothData();

        GlucoseChart glucoseChart = new GlucoseChart(session, req,timeData, rawData, smoothData);
        double lower = glucoseChart.getLimInp().get(0);
        double upper = glucoseChart.getLimInp().get(1);
        List<String> comments = GlucoseChart.getComments(session);


        // Consultants only view the file data, no user input
        resp.getWriter().write(consultPage(glucoseChart, req.getContextPath(), lower, upper,comments));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(true);

        //UPPER AND LOWER LIMITS
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
            e.printStackTrace();

        }

        // COMMENTS HANDLING
        String commentString = req.getParameter("commInp");
        String consultUsername = (String) session.getAttribute("username");
        if (consultUsername == null) consultUsername = "Unknown Consultant";

        GlucoseChart.addComment(session, consultUsername, commentString);

        resp.sendRedirect(req.getContextPath() + "/consultants");
    }

}  



    


