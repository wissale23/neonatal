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

    
    public Nurse (String name, int id, String endpoint) {
        super(name, id, endpoint);
    }
    

    public String glucoseInputLayout(String pathString,double glucoseValue, double time){
        return "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
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


    public String nursePage(GlucoseChart glucoseChart, String pathString,double glucoseValue, double time,double feedStart, double feedDuration, String feedType,List<String> comments) {

        return glucoseChart.generateHTML() 
               + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>" 
               + this.glucoseInputLayout(pathString, glucoseValue, time) 
               + this.feedingInputLayout(pathString, feedStart, feedDuration, feedType)
               + this.nurseCommentBox(pathString)
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

        double glucoseValue = getGlucValue(session).get(0);
        double time = getGlucValue(session).get(1);
        double feedStart = getFeedValue(session).get(0);
        double feedDuration = getFeedValue(session).get(1);
        String feedType = getFeedStr(session);
        List<String> comments = GlucoseChart.getComments(session);

        resp.getWriter().write(nursePage(glucoseChart, req.getContextPath(), glucoseValue,time,feedStart,feedDuration,feedType,comments));
    }

    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(true);
    
        try {
            //GLUCOSE INPUT 
            String glucoseString = req.getParameter("glucoseInp");
            String timeString = req.getParameter("timeInp");
    
            if (glucoseString != null && !glucoseString.isEmpty() &&
                timeString != null && !timeString.isEmpty()) {
    
                List<Double> times = (List<Double>) session.getAttribute("timeList");
                List<Double> glucoseValues = (List<Double>) session.getAttribute("glucoseList");
                if (times == null) {
                    times = new ArrayList<>();
                    glucoseValues = new ArrayList<>();
                    session.setAttribute("timeList", times);
                    session.setAttribute("glucoseList", glucoseValues);
                }
    
                times.add(Double.parseDouble(timeString));
                glucoseValues.add(Double.parseDouble(glucoseString));
            }
    
            // FEEDING INPUT 
            String startString = req.getParameter("startInp");
            String durString = req.getParameter("durInp");
            String typeString = req.getParameter("typeInp");
    
            if ((startString != null && !startString.isEmpty()) ||
                (durString != null && !durString.isEmpty()) ||
                (typeString != null && !typeString.isEmpty())) {
    
                List<Double> feedStarts = (List<Double>) session.getAttribute("startList");
                List<Double> feedDurations = (List<Double>) session.getAttribute("durationList");
                List<String> feedTypes = (List<String>) session.getAttribute("typeList");
                if (feedStarts == null) {
                    feedStarts = new ArrayList<>();
                    feedDurations = new ArrayList<>();
                    feedTypes = new ArrayList<>();
                    session.setAttribute("startList", feedStarts);
                    session.setAttribute("durationList", feedDurations);
                    session.setAttribute("typeList", feedTypes);
                }
    
                if (startString != null && !startString.isEmpty()) feedStarts.add(Double.parseDouble(startString));
                if (durString != null && !durString.isEmpty()) feedDurations.add(Double.parseDouble(durString));
                if (typeString != null && !typeString.isEmpty()) feedTypes.add(typeString);
            }
    
            // COMMENTS 
            String commentString = req.getParameter("commInp");
            String consultUsername = (String) session.getAttribute("username");
            if (consultUsername == null) consultUsername = "Unknown Consultant";
    
            GlucoseChart.addComment(session, consultUsername, commentString);
    
        
                // REDIRECT 
            resp.sendRedirect(req.getContextPath() + "/nurses");
        
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    

    
    public List<Double> getGlucValue(HttpSession session) {
        double glucose = defaultGlucose;
        double time = defaultTime;
    
        if (session != null) {
            List<Double> glucoseList = (List<Double>) session.getAttribute("glucoseList");
            List<Double> timeList = (List<Double>) session.getAttribute("timeList");
    
            if (glucoseList != null && !glucoseList.isEmpty()) {
                glucose = glucoseList.get(glucoseList.size() - 1); 
            }
            if (timeList != null && !timeList.isEmpty()) {
                time = timeList.get(timeList.size() - 1);
            }
        }
    
        List<Double> result = new ArrayList<>();
        result.add(glucose);
        result.add(time);
        return result;

    }   

    public List<Double> getFeedValue(HttpSession session) {
        double feedStart = defaultFeedStart;
        double feedDur = defaultFeedDuration;
    
        if (session != null) {
            List<Double> startList = (List<Double>) session.getAttribute("startList");
            List<Double> durationList = (List<Double>) session.getAttribute("durationList");
    
            if (startList != null && !startList.isEmpty()) {
                feedStart = startList.get(startList.size() - 1);
            }
            if (durationList != null && !durationList.isEmpty()) {
                feedDur = durationList.get(durationList.size() - 1);
            }
        }
    
        List<Double> result = new ArrayList<>();
        result.add(feedStart);
        result.add(feedDur);
        return result;
     }

    public String getFeedStr(HttpSession session) {
        String feedType = defaultFeedType;
        if (session != null) {
            List<String> typeList = (List<String>) session.getAttribute("typeList");
            if (typeList != null && !typeList.isEmpty()) {
                feedType = typeList.get(typeList.size() - 1);
            }
        }
        return feedType;
    }
}
