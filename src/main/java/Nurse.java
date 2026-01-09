import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


public class Nurse extends Adult implements Pageable {

    private final double defaultGlucose = 0.0;
    private final double defaultTime = 0.0;
    private final double defaultFeedStart = 0.0;
    private final double defaultFeedDuration = 0.0;
    private final String defaultFeedType = "";
    private final String defaultComment = "Add a comment";

    public Nurse(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    private String babyDropdown(int selectedId, String contextPath) {
        // Get all babies from your BabyStore or database
        List<Baby> babies = BabyPatientList.getAll();

        StringBuilder sb = new StringBuilder();
        sb.append("<form method='get' action='").append(contextPath).append("/nurse'>");
        sb.append("Select Baby: <select name='babyId' onchange='this.form.submit()'>");

        for (Baby b : babies) {
            sb.append("<option value='").append(b.getId()).append("'");
            if (b.getId() == selectedId) {
                sb.append(" selected");
            }
            sb.append(">").append(b.getName()).append("</option>");
        }

        sb.append("</select>");
        sb.append("</form><br>");
        return sb.toString();
    }

    public String glucoseInputLayout(String pathString,double glucoseValue, double time){
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "min-height:250px;"
                + "margin: 20px ;"
                + "text-align: center;'>"

                + "<h3 style='color: black;'>Entering blood glucose values(mM)</h3>"

                + "<form method='POST' action='" + pathString + "/nurses'>"
                + "<div>"
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Sample value: </span>"
                + "<input type='text' name='glucoseInp' step='0.001' value='" + glucoseValue + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Time of day: </span>"
                + "<input type='text' name='timeInp' step='0.001' value='" + time + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add sample</button>"
                            
                + "</div>"
                + "</form>"
                + "</div>";

    }    

    public String feedingInputLayout (String pathString,double feedStart, double feedDuration, String feedType){
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "min-height:250px;"
                + "margin: 20px ;"
                + "text-align: center;'>"

                + "<h3 style='color: black;'>Feeding Information</h3>"

                + "<form method='POST' action='" + pathString + "/nurses'>"
                + "<div>"
            
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Start of feeding: </span>"
                + "<input type='text' name='startInp' step='0.001' value='" + feedStart + "' style='width:100px; text-align:center;'/><br/><br/>"
            
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Duration of feeding: </span>"
                + "<input type='text' name='durInp' step='0.001' value='" + feedDuration + "' style='width:100px; text-align:center;'/><br/><br/>"
            
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Feeding Description: </span>"
                + "<input type='text' name='typeInp' step='0.001' value='" + feedType+ "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add feeding information</button>"
                            
                + "</div>"
                + "</form>"
                + "</div>";

    }


    public String nurseCommentBox(String pathString) {
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "min-height:250px;"
                + "margin: 20px ;"
                + "text-align: center;'>"
            
                + "<form method='POST' action='" + pathString + "/nurses'>"
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


    public String nursePage(GlucoseChart glucoseChart, HttpServletRequest req, int babyId,
                            double glucoseValue, double time,
                            double feedStart, double feedDuration, String feedType,
                            List<String> comments) {

        return "<html><head><title>Nurse Dashboard</title></head><body>"
                + "<h1>Nurse Dashboard</h1>"
                + babyDropdown(babyId, req.getContextPath())
                + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px; align-items:flex-start;'>"
                + glucoseInputLayout(req.getContextPath(), glucoseValue, time)
                + feedingInputLayout(req.getContextPath(), feedStart, feedDuration, feedType)
                + nurseCommentBox(req.getContextPath())
                + "</div>"
                + glucoseChart.generateHTML()
                + glucoseChart.commentsInpLayout(comments)
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
        double glucoseValue = getGlucValue(session).get(0);
        double time = getGlucValue(session).get(1);
        double feedStart = getFeedValue(session).get(0);
        double feedDuration = getFeedValue(session).get(1);
        String feedType = getFeedStr(session);
        List<String> comments = GlucoseChart.getComments(session);

        resp.setContentType("text/html");
        resp.getWriter().write(nursePage(glucoseChart, req, babyId,
                glucoseValue, time, feedStart, feedDuration, feedType, comments));
    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Baby baby = BabyPatientList.getBaby((int) session.getAttribute("babyId"));

        if (req.getParameter("glucoseInp") != null)
            baby.addSample(
                    Double.parseDouble(req.getParameter("timeInp")),
                    Double.parseDouble(req.getParameter("glucoseInp"))
            );

        if (req.getParameter("startInp") != null)
            baby.addFeeding(
                    Double.parseDouble(req.getParameter("startInp")),
                    Double.parseDouble(req.getParameter("durInp")),
                    req.getParameter("typeInp")
            );

        if (req.getParameter("commInp") != null)
            baby.addComment(
                    (String) session.getAttribute("username"),
                    req.getParameter("commInp")
            );

        resp.sendRedirect(req.getContextPath() + "/nurses");
    }

    // Get last glucose value from session
    public List<Double> getGlucValue(HttpSession session) {
        double glucose = defaultGlucose;
        double time = defaultTime;

        if (session != null) {
            List<Double> glucoseList = (List<Double>) session.getAttribute("glucoseList");
            List<Double> timeList = (List<Double>) session.getAttribute("timeList");

            if (glucoseList != null && !glucoseList.isEmpty())
                glucose = glucoseList.get(glucoseList.size() - 1);
            if (timeList != null && !timeList.isEmpty())
                time = timeList.get(timeList.size() - 1);
        }

        List<Double> result = new ArrayList<>();
        result.add(glucose);
        result.add(time);
        return result;
    }

    // Get last feeding values from session
    public List<Double> getFeedValue(HttpSession session) {
        double start = defaultFeedStart;
        double dur = defaultFeedDuration;

        if (session != null) {
            List<Double> startList = (List<Double>) session.getAttribute("startList");
            List<Double> durList = (List<Double>) session.getAttribute("durationList");

            if (startList != null && !startList.isEmpty())
                start = startList.get(startList.size() - 1);
            if (durList != null && !durList.isEmpty())
                dur = durList.get(durList.size() - 1);
        }

        List<Double> result = new ArrayList<>();
        result.add(start);
        result.add(dur);
        return result;
    }

    // Get last feed type
    public String getFeedStr(HttpSession session) {
        String type = defaultFeedType;
        if (session != null) {
            List<String> typeList = (List<String>) session.getAttribute("typeList");
            if (typeList != null && !typeList.isEmpty())
                type = typeList.get(typeList.size() - 1);
        }
        return type;
    }
}
