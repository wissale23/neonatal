package Display;

import Chart.ParentChart;
import Person.Baby;
import Servlets.BabyPatientList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ParentalDisplay {

    // Range and faster playback
    private final double maxValue = 50.0;
    private final int intervalMs = 25;

    public static void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Pick which baby to show (defaults to Person.Baby A / id=1)
        int babyId = 1;
        try {
            String idStr = req.getParameter("babyId");   // e.g. /parents?babyId=2
            if (idStr == null) idStr = req.getParameter("id"); // fallback
            if (idStr != null) babyId = Integer.parseInt(idStr);
        } catch (Exception ignored) {}

        // Get baby + data from Servlet.BabyPatientList
        Baby baby = BabyPatientList.getBaby(babyId);

        ParentChart parentChart = new ParentChart(baby);
        resp.getWriter().write(parentPage(parentChart));

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return;
    }

    
    private static String parentPage(ParentChart parentChart) {

        return ""
                + "<!DOCTYPE html>\n"
                + "<html lang='en'>\n"
                + "<head>\n"
                + "  <meta charset='UTF-8'>\n"
                + "  <meta name='viewport' content='width=device-width, initial-scale=1'>\n"
                + "  <title>Parent Display</title>\n"
                + "  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>\n"

                + "  <style>\n"
                + "    body { font-family: 'Lato', sans-serif; margin: 0; }\n"

                // Sidebar CSS
                + ".sidebar { height:100%; width:0; position:fixed; z-index:1; top:0; left:0; background-color:#111; overflow-x:hidden; transition:0.5s; padding-top:60px; }"
                + ".sidebar a { padding:8px 8px 8px 32px; text-decoration:none; font-size:25px; color:#818181; display:block; transition:0.3s; }"
                + ".sidebar a:hover { color:#f1f1f1; }"
                + ".sidebar .closebtn { position:absolute; top:0; right:25px; font-size:36px; margin-left:50px; }"
                + ".openbtn { font-size:20px; cursor:pointer; background-color:#111; color:white; padding:10px 15px; border:none; }"
                + ".openbtn:hover { background-color:#444; }"
                + "#main { transition: margin-left .5s; padding:16px; }"
                + "@media screen and (max-height:450px){.sidebar{padding-top:15px;}.sidebar a{font-size:18px;}}"

                // Header CSS
                + "  .header { background-color: #003087; color: white; text-align: center; padding: 20px; font-size: 28px; font-weight: bold; }\n"
                + "  </style>\n"

                + "</head>\n"
                + "<body>\n"

                + "<div class='header'>Parent Display</div>\n"
                + Homepage.generateSidebar()           // sidebar HTML
                + "</div>"
                + parentChart.generateHTML()            // parent chart
                + "</div>"
                + "<script>"
                + "function openSidebar(){ document.getElementById('mySidebar').style.width='250px';"
                + "document.getElementById('main').style.marginLeft='250px'; }"
                + "function closeSidebar(){ document.getElementById('mySidebar').style.width='0';"
                + "document.getElementById('main').style.marginLeft='0'; }"
                + "</script>"
                + "<script>"
                + "function toggleAbout() {"
                + "var content = document.getElementById('about-content');"
                + "var icon = document.getElementById('about-icon');"
                + "content.classList.toggle('collapsed');"
                + "if (content.classList.contains('collapsed')) {icon.className='fa fa-plus';}"
                + "else {icon.className='fa fa-minus';}"
                + "}"
                + "</script>"
                + "</body>\n"
                + "</html>";
    }

}
