import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Nurse extends Adult implements Pageable {

    private final double defaultGlucose = 0.0;
    private final int defaultHour = 00;
    private final int defaultMinute = 00;
    private final int defaultFeedDuration = 00;
    private final String defaultFeedType = "";
    private final String defaultComment = "Add a comment";

    public Nurse(String name, int id, String endpoint) {
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
                .append("/nurses'>");

        sb.append("<label style='margin-right:8px; font-size:16px;'>SELECT BABY:</label>");
        sb.append(
                "<select name='babyId' "
                        + "onchange='this.form.submit()' "
                        + "style='"
                        + "font-size:16px;"
                        + "padding:6px 12px;"
                        + "border:2px solid #342;"
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

    //Display the blood glucose heel pricks input
    public String glucoseInputLayout(String pathString, double glucoseValue, double hour, double minute) {

        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "min-height:250px;"
                + "margin: 20px ;"
                + "text-align: center;'>"
    
                + "<h3 style='color: black;'>Blood Glucose Heel Prick (mM)</h3>"
    
                + "<form method='POST' action='" + pathString + "/nurses'>"
                + "<div>"
            
                + "<span style='display:inline-block; width:110px; text-align:right; color:black;'>Sample value: </span>"
                + "<input type='number' name='glucoseInp' step='0.01' value='" 
                + glucoseValue + "' style='width:100px; text-align:center;'/>"
                + "<br/><br/>"
    
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Time of day: </span>"
                + "<input type='number' name='glucHourInp' min='0' max='23' step='1' "
                + "value='" + String.format("%02d", (int) hour) + "' style='width:45px; text-align:center;'/>"
                + " : "
                + "<input type='number' name='glucMinInp' min='0' max='59' step='1' "
                + "value='" + String.format("%02d", (int) minute) + "' style='width:45px; text-align:center;'/>"
                + "<br/><br/>"
    
                + "<div style='display:flex; justify-content:center; gap:10px;'>"
                + "<button type='submit' name='action' value='add' "
                + "style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add sample</button>"
                + "<button type='submit' name='action' value='undo' "
                + "style='background-color:#ff8c8c; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Undo last</button>"
                + "</div>"
    
                + "</div>"
                + "</form>"
                + "</div>";
    }


    public String feedingTypeDropdown(String feedType) {
        return "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Feeding Description: </span>"
                + "<input list='feedOptions' name='typeInp' value='" + feedType + "' "
                + "style='width:150px; text-align:center;'/>"
                + "<datalist id='feedOptions'>"
                + "<option value='Breastfeeding'>"
                + "<option value='Expressed breast milk'>"
                + "<option value='Fortified breast milk'>"
                + "<option value='Formula'>"
                + "<option value='Donor breast milk'>"
                + "<option value='Other'>"
                + "</datalist>";
    }




    // Display the feeding information input
    public String feedingInputLayout(String pathString, double feedStartHour, double feedStartMinute, double feedDuration, String feedType) {

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
                + "<input type='number' name='startHour' min='0' max='23' value='" + String.format("%02d", (int) feedStartHour) + "' style='width:50px; text-align:center;'/> : "
                + "<input type='number' name='startMinute' min='0' max='59' value='" + String.format("%02d", (int) feedStartMinute) + "' style='width:50px; text-align:center;'/><br/><br/>"

                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Duration of feeding (mins): </span>"
                + "<input type='text' name='durInp' step='0.01' value='" + String.format("%02d", (int) feedDuration) + "' style='width:100px; text-align:center;'/><br/><br/>"

                + feedingTypeDropdown(feedType)
                // Buttons side by side
                + "<div style='display:flex; justify-content:center; gap:10px;margin-top:15px;'>"
                + "<button type='submit' name='action' value='add' "
                + "style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add feeding information</button>"
                + "<button type='submit' name='action' value='undo' "
                + "style='background-color:#ff8c8c; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Undo last</button>"
                + "</div>"
    
                + "</div>"
                + "</form>"
                + "</div>";
    }


    //Display the comment box for nurses
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




    //Display the nurse page with the logout button, baby dropdown, glucose chart, heel pricks input, feeding input and comment box
    // added monitoring chart
    public String nursePage(GlucoseChart glucoseChart, MonitoringChart monitoringChart, HttpServletRequest req, int babyId,
                            double glucoseValue, double hour, double minute,
                            double feedStartHour, double feedStartMinute, double feedDuration, String feedType,
                            List<String> comments) {

        return "<!DOCTYPE html>"
                + "<html><head>"
                + "<title>Nurse Dashboard</title>"
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
                + "<div class='header'>Nurse Dashboard</div>" // Header HTML
                + glucoseChart.parentViewButton(req)
                + babyDropdown(babyId, req.getContextPath())

                // sidebar display
                + LogoutOption.generateLogoutSidebar()

                // Glucose Chart display
                + glucoseChart.generateHTML()

                // inputs display
                + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>"
                + glucoseInputLayout(req.getContextPath(), glucoseValue, hour, minute)
                + feedingInputLayout(req.getContextPath(), feedStartHour, feedStartMinute,feedDuration, feedType)
                + nurseCommentBox(req.getContextPath())
                + "</div>"

                // added monitoring chart
                + "<div style='display:flex; justify-content:center; margin: 25px 0 10px 0;'>"

                // monitoring simulation
                + monitoringChart.generateHTML()

                // comments display
                + glucoseChart.commentsInpLayout(comments)
                + "</div>"

                + "<script>"
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

        // Create Monitoring chart for selected baby
        MonitoringChart monitoringChart = new MonitoringChart(baby);

        // Get latest values for input forms
        List<Double> glucValues = getGlucValue(session);
        double glucoseValue = glucValues.get(0);
        double hour = glucValues.get(1);
        double minute = glucValues.get(2);

        List<Double> feedValues = getFeedValue(session);
        double feedStartHour = feedValues.get(0);
        double feedStartMinute = feedValues.get(1);
        double feedDuration = feedValues.get(2);
        
        String feedType = getFeedStr(session);
        List<String> comments = baby.getComments();

        resp.setContentType("text/html");
        resp.getWriter().write(nursePage(glucoseChart, monitoringChart, req, babyId,
                glucoseValue, hour,minute, feedStartHour,feedStartMinute, feedDuration, feedType, comments));
    }


    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Baby baby = BabyPatientList.getBaby((int) session.getAttribute("babyId"));

        String action = req.getParameter("action"); 
        

        if (req.getParameter("glucoseInp") != null) {
            if ("undo".equals(action)) {
                baby.removeLastSample();
            } else {
                double hour = Double.parseDouble(req.getParameter("glucHourInp"));
                double minute = Double.parseDouble(req.getParameter("glucMinInp"));
        
                double timeValue = hour + (minute / 60.0);
                baby.addSample(
                        timeValue,
                        Double.parseDouble(req.getParameter("glucoseInp"))
                );
            }
        }

        if (req.getParameter("startHour") != null) {
            if ("undo".equals(action)) {
                baby.removeLastFeeding(); 
            } else {
                double feedHour = Double.parseDouble(req.getParameter("startHour"));
                double feedMinute = Double.parseDouble(req.getParameter("startMinute"));   
                double feedValue = feedHour + (feedMinute / 60.0);

                double feedDuration = Double.parseDouble(req.getParameter("durInp")) / 60.0;
                String feedType = req.getParameter("typeInp");

                baby.addFeeding(
                        feedValue,
                        feedDuration,
                        feedType
                );

            }
        }

        if (req.getParameter("commInp") != null)
            baby.addComment(
                    (String) session.getAttribute("username"),
                    req.getParameter("commInp")
            );
        resp.sendRedirect(req.getContextPath() + "/nurses?babyId=" + baby.getId());
    }

    // Get last glucose value from session
    public List<Double> getGlucValue(HttpSession session) {
        double glucose = defaultGlucose;
        double hour = defaultHour;
        double minute = defaultMinute;
        

        if (session != null) {
            List<Double> glucoseList = (List<Double>) session.getAttribute("glucoseList");
            List<Double> timeList = (List<Double>) session.getAttribute("timeList");

            if (glucoseList != null && !glucoseList.isEmpty())
                glucose = glucoseList.get(glucoseList.size() - 1);
            if (timeList != null && !timeList.isEmpty()) {
                double time = timeList.get(timeList.size() - 1);
                hour = (int) time;
                minute = (int) ((time - hour) * 60);
            }

        }

        List<Double> result = new ArrayList<>();
        result.add(glucose);
        result.add((double) hour);    
        result.add((double) minute);
        return result;
    }

    // Get last feeding values from session
    public List<Double> getFeedValue(HttpSession session) {
        double startHour = defaultHour;
        double startMinute = defaultMinute;
        double duration = defaultFeedDuration;

        if (session != null) {
            List<Double> startList = (List<Double>) session.getAttribute("startList"); 
            List<Double> durList = (List<Double>) session.getAttribute("durationList"); 
    
            if (startList != null && !startList.isEmpty()) {
                double start = startList.get(startList.size() - 1);
                startHour = (int) start;
                startMinute = (int) ((start - startHour) * 60);
            }
    
            if (durList != null && !durList.isEmpty()) {
                duration = durList.get(durList.size() - 1) / 60.0; 
            }
        }

        List<Double> result = new ArrayList<>();
        result.add(startHour);
        result.add(startMinute);
        result.add(duration);
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
