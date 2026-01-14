import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;


public class Consultant extends Adult implements Pageable {

    private final double defaultLower = 2.6;
    private final double defaultUpper = 10;

    public Consultant(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    private String babyDropdown(int selectedId, HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        List<Integer> allowed = (session == null) ? null : (List<Integer>) session.getAttribute("allowedBabyIds");

        List<Baby> babies = BabyPatientList.getAll();
        StringBuilder sb = new StringBuilder();

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

        sb.append("<form method='get' action='")
                .append(req.getContextPath())
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

        for (Baby b : babies) {
            if (allowed != null && !allowed.contains(b.getId())) continue;

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
                + "<input type='text' name='lowerLimit' step='0.1' value='" + lower + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Upper limit: </span>"
                + "<input type='text' name='upperLimit' step='0.1' value='" + upper + "' style='width:100px; text-align:center;'/><br/><br/>"


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
                + "</head><body>"
                + glucoseChart.logoutButton(req)
                + "<h1 style='text-align:center;'>Consultant Dashboard</h1>"
                + babyDropdown(babyId, req)
                + "</div>"
                + glucoseChart.generateHTML()
                + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>"
                + rangeLayout(req.getContextPath(), lower, upper)
                + consultCommentBox(req.getContextPath())
                + "</div>"
                + glucoseChart.commentsInpLayout(comments)
                + "</body></html>";
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        List<Integer> allowed = (List<Integer>) session.getAttribute("allowedBabyIds");
        if (allowed == null) {
            allowed = new ArrayList<>();
            for (Baby b : BabyPatientList.getAll()) allowed.add(b.getId());
        }

        if (allowed.isEmpty()) {
            resp.setContentType("text/html");
            resp.getWriter().write("<h1 style='text-align:center;'>Consultant Dashboard</h1><p style='text-align:center;'>No babies assigned.</p><p style='text-align:center;'><a href='" + req.getContextPath() + "/logout'>Logout</a></p>");
            return;
        }

        int babyId = allowed.get(0);

        String param = req.getParameter("babyId");
        if (param != null) {
            try { babyId = Integer.parseInt(param); } catch (Exception ignored) {}
        } else if (session.getAttribute("babyId") != null) {
            babyId = (int) session.getAttribute("babyId");
        }

        if (!allowed.contains(babyId)) babyId = allowed.get(0);

        session.setAttribute("babyId", babyId);

        Baby baby = BabyPatientList.getBaby(babyId);
        GlucoseChart glucoseChart = new GlucoseChart(baby);

        double lower = baby.getLowerRange() != 0 ? baby.getLowerRange() : defaultLower;
        double upper = baby.getUpperRange() != 0 ? baby.getUpperRange() : defaultUpper;
        List<String> comments = baby.getComments();

        resp.setContentType("text/html");
        resp.getWriter().write(consultPage(glucoseChart, req, lower, upper, comments, babyId));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        List<Integer> allowed = (List<Integer>) session.getAttribute("allowedBabyIds");
        if (allowed == null) {
            allowed = new ArrayList<>();
            for (Baby b : BabyPatientList.getAll()) allowed.add(b.getId());
        }

        Integer currentBabyId = (Integer) session.getAttribute("babyId");
        if (currentBabyId == null || !allowed.contains(currentBabyId)) {
            resp.sendError(403);
            return;
        }

        Baby baby = BabyPatientList.getBaby(currentBabyId);

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




    


