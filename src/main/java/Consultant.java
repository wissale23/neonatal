import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Consultant extends Adult implements Pageable {

    // Default upper and lower blood glucose limit
    private final double defaultLower = 2.6;
    private final double defaultUpper = 10;

    public Consultant(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    private String babyDropdown(int selectedId, String contextPath) {

        // Get all babies from the Baby Patient List
        List<Baby> babies = BabyPatientList.getAll();

        StringBuilder sb = new StringBuilder();

        // Design of Dropdown
        sb.append(
                "<div style='"
                        + "background:#e3f2fd;"
                        + "border:3px solid #1565c0;"
                        + "border-radius:10px;"
                        + "padding:15px 25px;"
                        + "margin:20px auto;"
                        + "width:380px;"
                        + "max-width:90%;"
                        + "text-align:center;"
                        + "font-size:16px;"
                        + "font-weight:bold;"
                        + "color:#0d47a1;"
                        + "box-shadow: 0 4px 8px rgba(0,0,0,0.1);"
                        + "transition: all 0.2s ease-in-out;'>"
        );

// Form
        sb.append("<form method='get' action='")
                .append(contextPath)
                .append("/consultants'>");
        sb.append("<label style='margin-right:8px; font-size:16px;'>SELECT BABY:</label>");
        sb.append(
                "<select name='babyId' "
                        + "onchange='this.form.submit()' "
                        + "style='"
                        + "font-size:16px;"
                        + "padding:6px 12px;"
                        + "border:2px solid #1565c0;"
                        + "border-radius:6px;"
                        + "background:#ffffff;"
                        + "color:#0d47a1;"
                        + "cursor:pointer;"
                        + "transition: all 0.2s ease-in-out;'>"
        );


        for (Baby b : BabyPatientList.getAll()) {
            sb.append("<option value='")
                    .append(b.getId()).append("'")
                    .append(b.getId() == selectedId ? " selected" : "")
                    .append(">")
                    .append(b.getName())
                    .append("</option>");
        }

        sb.append("</select>");
        sb.append("</form>");
        sb.append("</div>");

        return sb.toString();
    }
    //Display blood glucose safety range input
    public String rangeLayout(String pathString,double lower,double upper){
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "margin: 20px;"
                + "text-align: center;'>"

                + "<h3 style='color: black;'>Blood Glucose Safety Range (mM)</h3>"

                + "<form method='POST' action='" + pathString + "/consultants'>"
                + "<div>"
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Lower limit: </span>"
                + "<input type='text' name='lowerLimit' step='0.1' value='" + lower + "' style='width:100px; text-align:center;'/><br/><br/>" // lower limit input
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Upper limit: </span>"
                + "<input type='text' name='upperLimit' step='0.1' value='" + upper + "' style='width:100px; text-align:center;'/><br/><br/>" // upper limit input


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Apply</button>"

                + "</div>"
                + "</form>"
                + "</div>";

    }
    // Display comment box for consultant
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

    //Display consultant page, with the logout button, glucose chart, safety range and comments input
    public String consultPage(GlucoseChart glucoseChart,HttpServletRequest req,double lower,double upper,List<String> comments, int babyId){

        return "<!DOCTYPE html>"
                + "<html><head>"
                + "<title>Consultant Dashboard</title>"
                // Sidebar display
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>"
                + "<style>"
                + "body { font-family: 'Lato', sans-serif; }"
                + ".sidebar { height:100%; width:0; position:fixed; z-index:1; top:0; left:0; background-color:#111; overflow-x:hidden; transition:0.5s; padding-top:60px; }"
                + ".sidebar a { padding:8px 8px 8px 32px; font-size:25px; color:#818181; display:block; text-decoration:none; }"
                + ".sidebar a:hover { color:#f1f1f1; }"
                + ".sidebar .closebtn { position:absolute; top:0; right:25px; font-size:36px; }"
                + ".openbtn { font-size:20px; cursor:pointer; background-color:#111; color:white; padding:10px 15px; border:none; }"
                + "#main { padding:16px; }"
                // Header CSS
                + ".header { background-color: #003087; color: white; text-align: center; padding: 20px; font-size: 28px; font-weight: bold; }"
                + "</style>"

                + "</head><body>"
                + "<div class='header'>Consultant Dashboard</div>" // Header HTML
                + glucoseChart.parentViewButton(req) // parent view button
                + babyDropdown(babyId, req.getContextPath())  // Baby dropdown
                + LogoutOption.generateLogoutSidebar() //Logout sidebar
                + "</div>"
                + glucoseChart.generateHTML() // Display glucose chart
                + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>"
                // Range and Comment Inputs
                + rangeLayout(req.getContextPath(), lower, upper) // safety range input
                + consultCommentBox(req.getContextPath()) // comment box input
                + "</div>"
                + glucoseChart.commentsInpLayout(comments) // comments display
                + "</div>"
                + "<script>"
                // Sidebar display
                + "function openSidebar(){"
                + " document.getElementById('mySidebar').style.width='250px';"
                + "}"
                + "function closeSidebar(){"
                + " document.getElementById('mySidebar').style.width='0';"
                + "}"
                + "</script>"
                + "</body></html>";
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        // Get babyId from dropdown or session, default to first baby
        int babyId = 1;
        if (req.getParameter("babyId") != null) {
            babyId = Integer.parseInt(req.getParameter("babyId"));
        } else if (session.getAttribute("babyId") != null) {
            babyId = (int) session.getAttribute("babyId");
        }
        session.setAttribute("babyId", babyId);

        // Get selected baby
        Baby baby = BabyPatientList.getBaby(babyId);

        // Create chart for selected baby
        GlucoseChart glucoseChart = new GlucoseChart(baby);

        // Get latest values for input forms
        double lower = baby.getLowerRange() != 0 ? baby.getLowerRange() : defaultLower;
        double upper = baby.getUpperRange() != 0 ? baby.getUpperRange() : defaultUpper;
        List<String> comments = baby.getComments();

        resp.setContentType("text/html");
        // Consultants only view the file data, no user input
        resp.getWriter().write(consultPage(glucoseChart, req, lower, upper, comments, babyId));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Baby baby = BabyPatientList.getBaby((int) session.getAttribute("babyId"));

        // Update limits
        String lowerStr = req.getParameter("lowerLimit");
        String upperStr = req.getParameter("upperLimit");

        try {
            double lower = lowerStr != null && !lowerStr.isEmpty() ? Double.parseDouble(lowerStr) : baby.getLowerRange();
            double upper = upperStr != null && !upperStr.isEmpty() ? Double.parseDouble(upperStr) : baby.getUpperRange();
            baby.setRanges(lower, upper);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Update comments
        String commentStr = req.getParameter("commInp");
        String consultUsername = (String) session.getAttribute("username");
        if (consultUsername == null) consultUsername = "Unknown Consultant";
        if (commentStr != null && !commentStr.isEmpty()) baby.addComment(consultUsername, commentStr);

        resp.sendRedirect(req.getContextPath() + "/consultants?babyId=" + baby.getId());
    }

}




    


