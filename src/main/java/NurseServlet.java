// Unfinished
//import javax.servlet.http.*;
//import java.io.IOException;

//public class NurseServlet {
//  public void display(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        GlucoseGraph graph = new GlucoseGraph(data);
//        resp.getWriter().write(graph.generateHTML(true, false)); // nurse input
//    }
//}

import java.util.List;

public class NurseServlet {


    private double bloodGlucose;
    private double time;
    private List<Double> sampleTimes;
    private List<Double> sampleValues;


    public NurseServlet(List<Double> sampleValues,List<Double> sampleTimes) {

        this.sampleValues = sampleValues;
        this.sampleTimes = sampleTimes;
    }

    
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
                        "  backgroundColor: '#000',\n" +
                        "  borderColor: '#000'\n" +
                        "},\n";
            }
        }
        return sampleTriangles;


    }

    public String nursePage(List<Double> timeArrayString, List<Double> rawArrayString, List<Double> smoothDataString, double lower, double upper, String pathString) {

        GlucoseChart glucoseChart = new GlucoseChart(timeArrayString, rawArrayString, smoothDataString,lower, upper);

        return glucoseChart.generateHTML(this.getSamples()) + "<div style='background-color: #fedae6; "
                + "border: 2px solid black;"
                + "padding: 20px;"
                + "border-radius: 10px;"
                + "width: 300px;"
                + "margin: 20px auto;"
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
                + "</div>"
                + "</body></html>";

    }

}
