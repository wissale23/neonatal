import java.util.List;

public class NurseServlet {


    private double bloodGlucose;
    private double time;
    private double feedingStart;
    private double feedingDuration;
    private String feedingType;




    public NurseServlet(double bloodGlucose, double time, double feedingStart, double feedingDuration,String feedingType) {
        this.bloodGlucose = bloodGlucose;
        this.time = time;
        this.feedingStart = feedingStart;
        this.feedingDuration = feedingDuration;
        this.feedingType = feedingType;

    }

    public double getGlucoseValue() {
        return this.bloodGlucose;
    }

    public double getTime() {
        return this.time;
    }
    
    public double getFeedStart(){
        return this.feedingStart;
    } 
    
    public double getFeedDur(){
        return this.feedingDuration;
    } 

    public String getFeedType(){
        return this.feedingType;
    }   


    

    public String glucoseInputLayout(String pathString){
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
                + "<input type='text' name='glucoseInp' step='0.001' value='" + this.getGlucoseValue() + "' style='width:100px; text-align:center;'/><br/><br/>"
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Time of day: </span>"
                + "<input type='text' name='timeInp' step='0.001' value='" + this.getTime() + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add sample</button>"
                            
                + "</div>"
                + "</form>"
                + "</div>";

    }    

    public String feedingInputLayout (String pathString){
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
                + "<input type='text' name='startInp' step='0.001' value='" + this.getFeedStart() + "' style='width:100px; text-align:center;'/><br/><br/>"
            
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Duration of feeding: </span>"
                + "<input type='text' name='durInp' step='0.001' value='" + this.getFeedDur() + "' style='width:100px; text-align:center;'/><br/><br/>"
            
                + "<span style='display:inline-block; width:110px; text-align:right;color:black;'>Feeding Description: </span>"
                + "<input type='text' name='typeInp' step='0.001' value='" + this.getFeedType() + "' style='width:100px; text-align:center;'/><br/><br/>"


                + "<button type='submit' style='background-color:#ffc0cb; border:2px solid black; padding:5px 10px; border-radius:4px; color:black; font-weight:bold;'>Add feeding information</button>"
                            
                + "</div>"
                + "</form>"
                + "</div>";

    }

    public String commentInputLayout(String pathString) {
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

    


    public String nursePage(List<Double> timeArrayString, List<Double> rawArrayString, List<Double> smoothDataString, double lower, double upper, List<Double> sampleValues,List<Double> sampleTimes,List<Double> feedingStarts,List<Double> feedingDurations,List<String> feedingTypes,List<String> comments, String pathString) {

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString,lower, upper,sampleValues,sampleTimes,feedingStarts,feedingDurations,feedingTypes,comments);

        return glucoseChart.generateHTML() 
               + "<div style='display:flex; justify-content:center; gap:30px; margin-top:20px;'>" 
               + this.glucoseInputLayout(pathString) 
               + this.feedingInputLayout(pathString)
               + this.commentInputLayout(pathString)
               + "</div>"
            
               + "</body></html>";

    }

}
