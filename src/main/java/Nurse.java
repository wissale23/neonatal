import javax.servlet.http.HttpSession;
import java.util.List;

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

    public String commentBox(String pathString) {
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

    


    public String nursePage(GlucoseChart glucoseChart, String pathString,double glucoseValue, double time,double feedStart, double feedDuration, String feedType) {

        return glucoseChart.generateHTML() 
               + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>" 
               + this.glucoseInputLayout(pathString, glucoseValue, time) 
               + this.feedingInputLayout(pathString, feedStart, feedDuration, feedType)
               + this.commentBox(pathString)
               + "</div>"
               + glucoseChart.commentsInpLayout()
            
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
        
        resp.getWriter().write(nursePage(glucoseChart, req.getContextPath(), glucoseValue,time,feedStart,feedDuration,feedType));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);

        String glucoseString = req.getParameter("glucoseInp");
        String timeString = req.getParameter("timeInp");
        String startString = req.getParameter("startInp");
        String durString = req.getParameter("durInp");
        String typeString = req.getParameter("typeInp");
        String commentString = req.getParameter("commInp");    
                
        List<Double> times = (List<Double>) session.getAttribute("timeList");
        List<Double> glucoseValues = (List<Double>) session.getAttribute("glucoseList");
        List<Double> feedStarts = (List<Double>) session.getAttribute("startList");
        List<Double> feedDurations = (List<Double>) session.getAttribute("durationList");
        List<String> feedTypes = (List<String>) session.getAttribute("typeList");
        List<String> comments = (List<String>) session.getAttribute("commentsList");

                
        if (times == null) {
            times = new ArrayList<>();
            glucoseValues = new ArrayList<>();
            session.setAttribute("timeList", times);
            session.setAttribute("glucoseList", glucoseValues);
        }

        try {
            if (glucoseString != null && !glucoseString.isEmpty() &&
            timeString != null && !timeString.isEmpty()) {

            times.add(Double.parseDouble(timeString));
            glucoseValues.add(Double.parseDouble(glucoseString));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); // later again
        }

        if (feedStarts == null) {
            feedStarts = new ArrayList<>();
            feedDurations = new ArrayList<>();
            feedTypes = new ArrayList<>();
            session.setAttribute("startList", feedStarts);
            session.setAttribute("durationList", feedDurations);
            session.setAttribute("typeList", feedTypes);
        }

        try {
            if (startString != null && !startString.isEmpty() &&
                durString != null && !durString.isEmpty() && 
                typeString != null && !typeString.isEmpty()) {

                feedStarts.add(Double.parseDouble(startString));
                feedDurations.add(Double.parseDouble(durString));
                feedTypes.add(typeString);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace(); // later again
        }
                        
           
        if (comments == null) {
            comments = new ArrayList<>();
            session.setAttribute("commentsList", comments);
        }

            
        if (commentString != null && !commentString.isEmpty()) {
            String nurseUsername = (String) session.getAttribute("username");    

            comments.add(nurseUsername + ": " + commentString);
        }
         

        resp.sendRedirect(req.getContextPath() + "/nurses");
        return;
    }
      

    public List<Double> getGlucValue(HttpSession session){
        
        double glucose = defaultGlucose;
        double time = defaultTime;
        if (session != null) {

            Object gl = session.getAttribute("glucoseInp");
            Object tm = session.getAttribute("timeInp");
        
            if (gl != null){
                glucose = (double) gl;
            }
            if (tm != null) {
                time = (double) tm;
            }
        }   
        List<Double>  result = new ArrayList<>();
        result.add(glucose); 
        result.add(time);         
    
        return result;      
    
    }   

    public List<Double> getFeedValue(HttpSession session){
        double feedStart = defaultFeedStart;
        double feedDur = defaultFeedDuration;
        if (session != null) {
        
            Object fs = session.getAttribute("startInp");
            Object fd = session.getAttribute("durInp");
                
            if (fs != null) {
                feedStart = (double) fs;
            }
            if (fd != null) {
                feedDur = (double) fd;
            }
        }           
        List<Double>  result = new ArrayList<>();
        result.add(feedStart); 
        result.add(feedDur);         
    
        return result; 
        
    }

    public String getFeedStr(HttpSession session){
        
        String feedType = defaultFeedType;
        if (session != null) {
        
            Object ft = session.getAttribute("typeInp");

            if (ft != null) {
                feedType = (String) ft;
            }
            
        }                 
    
        return feedType; 
        
    }

    
}
