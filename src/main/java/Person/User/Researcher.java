package Person.User;

import Display.LogoutOption;
import Person.Adult;
import Person.Baby;
import Servlets.BabyPatientList;
import Servlets.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Researcher extends Adult implements Pageable {

    public Researcher(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        // Build dropdown options from patients list
        StringBuilder options = new StringBuilder();
        for (int i = 0; i < BabyPatientList.getAll().size(); i++) {
            Baby b = BabyPatientList.getAll().get(i);
            options.append("<option value=\"").append(i).append("\">")
                    .append("ID: ").append(b.getId())
                    .append("</option>");
        }

        // Generative AI (Claude) was used to help write this section as I was unfamiliar with HTML syntax
        resp.getWriter().write(
                "<!DOCTYPE html>" +
                        "<html><head>" +
                        "<title>Researcher Portal</title>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                        "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>" +
                        "<style>" +
                        "body { font-family: 'Lato', sans-serif; }" +
                        ".sidebar { height:100%; width:0; position:fixed; z-index:1; top:0; left:0; background-color:#111; overflow-x:hidden; transition:0.5s; padding-top:60px; }" +
                        ".sidebar a { padding:8px 8px 8px 32px; font-size:25px; color:#818181; display:block; text-decoration:none; }" +
                        ".sidebar a:hover { color:#f1f1f1; }" +
                        ".sidebar .closebtn { position:absolute; top:0; right:25px; font-size:36px; }" +
                        ".openbtn { font-size:20px; cursor:pointer; background-color:#111; color:white; padding:10px 15px; border:none; }" +
                        ".openbtn:hover { background-color:#444; }" +
                        "#main { transition: margin-left .5s; padding:16px; }" +
                        // Header CSS
                        ".header { background-color: #003087; color: white; text-align: center; padding: 20px; font-size: 28px; font-weight: bold; }"
                        + "</style>"

                        + "</head><body>"
                        + "<div class='header'>Researcher Dashboard</div>" + // Header HTML

                        LogoutOption.generateLogoutSidebar() +

                        // Building the HTML form that controls the download
                        "<div id='main'>" +
                        "<p>Download glucose monitoring data:</p>" +
                        "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
                        "<label for=\"babySelect\">Select Baby: </label>" +
                        "<select name=\"babyIndex\" id=\"babySelect\" required>" +
                        options +
                        "</select><br><br>" +
                        "<button type=\"submit\" name=\"action\" value=\"download\">Download Data</button>" +
                        "</form>" +
                        "</div>" +

                        "<script>" +
                        "function openSidebar(){ document.getElementById('mySidebar').style.width='250px'; document.getElementById('main').style.marginLeft='250px'; }" +
                        "function closeSidebar(){ document.getElementById('mySidebar').style.width='0'; document.getElementById('main').style.marginLeft='0'; }" +
                        "</script>" +
                        "</body></html>"
        );
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        String role = (s == null) ? null : (String) s.getAttribute("role");
        if (!"researcher".equals(role)) {
            resp.sendError(403);
            return;
        }

        String action = req.getParameter("action");
        if ("download".equals(action)) {
            // Get selected baby index from dropdown
            String babyIndexParam = req.getParameter("babyIndex");
            if (babyIndexParam == null) {
                resp.sendError(400, "No baby selected");
                return;
            }

            // Person.Baby selection validation
            int babyIndex = Integer.parseInt(babyIndexParam);
            List<Baby> babies = BabyPatientList.getAll();

            if (babyIndex < 0 || babyIndex >= babies.size()) {
                resp.sendError(400, "Invalid baby selection");
                return;
            }

            // Load data for selected baby
            Baby selectedBaby = babies.get(babyIndex);
            List<Double> timeData = selectedBaby.getTimeData();
            List<Double> rawData = selectedBaby.getRawData();
            List<Double> smoothData = selectedBaby.getSmoothData();
            List<Double> estimatedData = new ArrayList<>();
            for (Double v : smoothData) {
                estimatedData.add((v - 1.5) / 3.5);
            }

            // Generative AI (Claude) was used to help write this section to understand how to generate a donwloadable file
            
            // Set headers for file download
            String filename = "glucose_data_" + selectedBaby.getId() + ".csv";
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            // Write CSV content
            PrintWriter writer = resp.getWriter();
            writer.println("Time,Raw_Skin_Glucose_uM,Smoothed_Skin_Glucose_uM,Estimated_Blood_Glucose_mM");

            int maxSize = Math.max(timeData.size(), Math.max(rawData.size(), smoothData.size()));
            for (int i = 0; i < maxSize; i++) {
                String time = i < timeData.size() ? String.valueOf(timeData.get(i)) : "";
                String raw = i < rawData.size() ? String.valueOf(rawData.get(i)) : "";
                String smooth = i < smoothData.size() ? String.valueOf(smoothData.get(i)) : "";
                String estimated = i < estimatedData.size() ? String.valueOf(estimatedData.get(i)) : "";
                writer.println(time + "," + raw + "," + smooth + "," + estimated);
            }
            writer.flush();
            writer.close();
        }
    }
}
