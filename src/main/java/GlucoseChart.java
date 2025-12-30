import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;


public class GlucoseChart {

    private final List<Double> timeData;
    private final List<Double> rawData;
    private final List<Double> smoothData;
    private double lower, upper;
    private List<Double> sampleValues, sampleTimes;
    private List<Double> feedingStarts, feedingDurations;
    private List<String> feedingTypes, comments;


    private final double defaultLower = 2.6;
    private final double defaultUpper = 10.0;
    private HttpSession session;
    private HttpServletRequest req;


    // Instantiate Data and Inputs
    public GlucoseChart(HttpSession session, HttpServletRequest req,List<Double> timeData, List<Double> rawData, List<Double> smoothData) {
        this.session = session;
        this.req = req;
        this.timeData = timeData;
        this.rawData = rawData;
        this.smoothData = smoothData;
        this.lower = getLimInp().get(0);
        this.upper = getLimInp().get(1);
        this.sampleValues = getGlucInp().get(0);
        this.sampleTimes = getGlucInp().get(1);
        this.feedingStarts = getFeedInp().get(0);
        this.feedingDurations = getFeedInp().get(1);
        this.feedingTypes = getFeedTypeInp();
        //this.comments = getComInp();
        
    }


    // Plot Heel Prick Sample Inputs
    public String getSamples(){
        String sampleTriangles = "";
        if (sampleTimes != null && sampleValues != null) {
            for (int i = 0; i < sampleTimes.size(); i++) {
                sampleTriangles = sampleTriangles + "sample" + i + ": {\n" +
                        "  type: 'point',\n" +
                        "  xValue: " + sampleTimes.get(i) + ",\n" +
                        "  yValue: " + sampleValues.get(i) + ",\n" +
                        "  yScaleID: 'y2',\n" +
                        "  radius: 6,\n" +
                        "  pointStyle: 'triangle',\n" +
                        "  backgroundColor: 'rgb(220,0,0)',\n" +
                        "  borderColor: 'rgb(220,0,0)'\n" +
                        "},\n";
            }
        }
        return sampleTriangles;
    }

    //adding new comment to select whenever there is a new one

    public static void addComment(HttpSession session, String username, String commentText) {

        if (commentText == null || commentText.isEmpty()) return;
        List<String> comments = getComments(session);
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        comments.add(time + "\n" + username + ": " + commentText);
    }



    public String commentsInpLayout(List<String> comments) {

        String options = "";
        for (int i = 0; i < comments.size(); i++) {
            options += "<option value='" + i + "'>Comment " + (i + 1) + "</option>";
        }
    
        String commentsJS = "[";
        for (int i = 0; i < comments.size(); i++) {
            commentsJS += "\"" +
                comments.get(i)
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                + "\"";
    
            if (i < comments.size() - 1) {
                commentsJS += ",";
            }
        }
        commentsJS += "]";
    
        return "<script>" +
            "const comments = " + commentsJS + ";" +
            "function showComment(index) {" +
            " if (index < 0) return;" +
            " document.getElementById('commentCanvas').innerText = comments[index];" +
            "}" +
            "</script>" +
    
            "<div id='commentCanvas' style='" +
            "margin-top:15px; padding:10px; border:2px solid black;" +
            "width:400px; min-height:60px; max-height:150px; overflow-y:auto;'>" +
            "Select a comment to view it</div>" +
    
            "<select onchange='showComment(Number(this.value))'>" +
            "<option value='-1' selected>See all comments</option>" +
            options +
            "</select>";
    }




    // Plot Feeding Times, Feeding Durations, and Feeding Descriptions
    public String getFeedings(){
        String feedingBars = "";
        if (feedingStarts != null && feedingDurations != null && feedingTypes != null) {
            for (int i = 0; i < feedingStarts.size(); i++) {
                double start = feedingStarts.get(i);
                double end = start + feedingDurations.get(i);
                String type = feedingTypes.get(i);
                feedingBars = feedingBars + "feed" + i + ": {\n" +
                        "  type: 'box',\n" +
                        "  xMin: " + start + ",\n" +
                        "  xMax: " + end + ",\n" +
                        "  yScaleID: 'y2',\n" +
                        "  yMin: 0,\n" +
                        "  yMax: 12,\n" +
                        "  backgroundColor: 'rgba(0,0,255,0.25)',\n" +
                        "  borderColor: 'rgb(0,0,255)',\n" +
                        "  borderWidth: 1,\n" +
                        "  drawTime: 'beforeDatasetsDraw',\n" +
                        "  label: {\n" +
                        "    display: true,\n" +
                        "    content: '" + type + "',\n" +
                        "    position: 'center',\n" +
                        "    color: 'rgb(0,0,255)',\n" +
                        "    font: { size: 11, weight: 'bold' }\n" +
                        "  }\n" +
                        " },\n";
            }
        }
        return feedingBars;
    }

    // Display warning alert message box
    public String buildWarningHTML(List<Double> glucoseData) {
        
        double latestGlucose = glucoseData.get(glucoseData.size() - 1);
        WarningSystem warningSystem = new WarningSystem(lower, upper);

        return AlertRenderer.buildAlertHTML(warningSystem, latestGlucose);
    }


    public String generateHTML() {
        String timeArray = timeData.toString();
        String rawArray = rawData.toString();
        String smoothArray = smoothData.toString();
        String warningHTML = buildWarningHTML(rawData);

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
            // Header
                "<head>\n" +
                AlertRenderer.alertCSS +
                "  <title>Glucose Chart</title>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                "</head>\n" +

            // Body
                "<body>\n" +
                warningHTML +
                "  <h2>Glucose Levels</h2>\n" +
                "  <canvas id='glucoseChart' width='800' height='400'></canvas>\n" +
                "  <script>\n" +
                "    const labels = " + timeArray + ";\n" +
                "    const rawData = " + rawArray + ";\n" +
                "    const smoothData = " + smoothArray + ";\n" +
                "    const LOWER = " + lower + ";\n" +
                "    const UPPER = " + upper+ ";\n" +
                "\n" +
            // Plot Chart
                "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                "    const chart = new Chart(ctx, {\n" +
                "      type: 'line',\n" +
                "      data: {\n" +
                "        labels: labels,\n" +
                "        datasets: [\n" +
            // Plot Raw Skin Glucose, Filtered (Smoothed) Skin Glucose, Estimated Blood Glucose
                "          { label: 'Raw Skin Glucose', data: rawData, yAxisID: 'y', borderColor: 'rgb(252,168,168)', borderWidth: 3, fill: false, order: 2, pointRadius: 0 },\n" + 
                "          { label: 'Smoothed Skin Glucose', data: smoothData, yAxisID: 'y', borderColor: 'rgb(220,25,25)', borderWidth: 1.5, fill: false, order: 1, pointRadius: 0 },\n" +
                "          { label: 'Estimated Blood Glucose', data: smoothData.map(v => (v - 1.5) / 3.5), yAxisID: 'y2', borderColor: 'rgb(255,210,210)', borderWidth: 6, fill: false, order: 3, pointRadius: 0 }\n" +
                "        ]\n" +
                "      },\n" +
                "      options: {\n" +
                "        responsive: true,\n" +
                "        scales: {\n" +
            // Define Axes
                "          y: {position: 'left',  min: 0, max: 90, title: {display: true, text: 'Skin Glucose (µM)'} },\n" +
                "          y2: { position: 'right', min: 0, max: 12, title: {display: true, text: 'Blood Glucose (mM)'} },\n" +
                "          x: { type: 'linear', min: 11.0, max: 14.0,\n" +
                "            title: { display: true, text: 'Time (hours)' },\n" +
                "            ticks: {\n" +
                "              stepSize: (0.5 / 6),\n" +
                "              callback: function(value) {\n" +
                "                if (Math.abs(value % 0.5) < 1e-6) {\n" +
                "                  return value.toFixed(1);\n" +
                "                }\n" +
                "                return '';\n" +
                "              }\n" +
                "            },\n" +
                "            grid: { drawTicks: true }\n" +
                "          }\n" +
                "        },\n" +
                "        plugins: {\n" +
                "          annotation: {\n" +
                "            annotations: {\n" +
            // Plot Acceptable Range
                "              low: { type: 'box', yScaleID: 'y2', yMin: 0, yMax: LOWER, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
                "              normal: { type: 'box', yScaleID: 'y2', yMin: LOWER, yMax: UPPER, backgroundColor: 'rgba(144,238,144,0.35)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Normal Range', display: true, color: '#1b5e20', font: { size: 12 } } },\n" +
                "              high: { type: 'box', yScaleID: 'y2',  yMin: UPPER, yMax: 12, backgroundColor: 'rgba(216,216,216,0.15)', drawTime: 'beforeDatasetsDraw', label: { content: 'Blood Glucose: Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } } },\n" +
            // Plot Heel Prick Sample inputs
                               this.getSamples() +
                               this.getFeedings() +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    });\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>\n";
    }

    public List<Double> getLimInp() {
        double upper = defaultUpper;
        double lower = defaultLower;
    
        if (session != null) {
            Object low = session.getAttribute("lowerLimit");
            Object upp = session.getAttribute("upperLimit");
    
            if (low != null){
                lower = (double) low;
            }
            if (upp != null) {
                upper = (double) upp;
            }
            
        }
    
        List<Double> result = new ArrayList<>();
        result.add(lower);
        result.add(upper);
        return result;
    }

    
    public List<List<Double>> getGlucInp() {

        List<Double> times = new ArrayList<>();
        List<Double> glucoseValues = new ArrayList<>();
    
        if (session != null) {
            Object t = session.getAttribute("timeList");
            Object g = session.getAttribute("glucoseList");
    
            if (t instanceof List<?>) {
                times = (List<Double>) t;
            }
            if (g instanceof List<?>) {
                glucoseValues = (List<Double>) g;
            }
        }
    
        req.setAttribute("timeList", times);
        req.setAttribute("glucoseList", glucoseValues);
    
        List<List<Double>> result = new ArrayList<>();
        result.add(glucoseValues); 
        result.add(times);         
    
        return result;
    }

    public List<List<Double>> getFeedInp(){
        
        List<Double> feedStarts = new ArrayList<>(); 
        List<Double> feedDurations = new ArrayList<>();
        
        if (session != null) {
            Object fs = session.getAttribute("startList");
            Object fd = session.getAttribute("durationList");
           

            if (fs instanceof List<?>) {
                feedStarts = (List<Double>) fs;
            }
            if (fd instanceof List<?>){
                feedDurations = (List<Double>) fd;     
            }
        }
        req.setAttribute("startList", feedStarts); 
        req.setAttribute("durationList", feedDurations);
        
        List<List<Double>> result = new ArrayList<>();
        result.add(feedStarts); 
        result.add(feedDurations);         
    
        return result;
    }

    public List<String> getFeedTypeInp(){
        
        List<String> feedTypes = new ArrayList<>();
    

        if (session != null) {

            Object ft = session.getAttribute("typeList");

            if (ft instanceof List<?>) {
                feedTypes = (List<String>) ft;
            }
        }
        req.setAttribute("typeList", feedTypes);
        return feedTypes;

    }  

}    
/*
     public List<String> getComInp(){
        
        List<String> comments = new ArrayList<>();
    

        if (session != null) {

            Object com = session.getAttribute("commentsList");    

            if (com instanceof List<?>) {
                comments = (List<String>) com;
            }
        }
        req.setAttribute("commentsList",comments);    
        return comments;

    }    

*/
                

