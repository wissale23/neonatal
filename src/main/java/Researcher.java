import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Researcher extends Adult implements Pageable{

    public Researcher(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        // Build dropdown options from patients list
        StringBuilder options = new StringBuilder();
        List<Baby> patients = getPatients();
        for (int i = 0; i < patients.size(); i++) {
            Baby baby = patients.get(i);
            options.append("<option value=\"").append(i).append("\">")
                    .append(baby.getName()).append(" (ID: ").append(baby.getId()).append(")")
                    .append("</option>");
        }

        resp.getWriter().write(
                "<h1>Researcher Portal OOP Branch</h1>" +
                        "<p>Download glucose monitoring data:</p>" +
                        "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
                        "<label for=\"babySelect\">Select Baby: </label>" +
                        "<select name=\"babyIndex\" id=\"babySelect\" required>" +
                        options.toString() +
                        "</select><br><br>" +
                        "<button type=\"submit\" name=\"action\" value=\"download\">Download Data</button>" +
                        "</form>" +
                        "<p><a href=\"" + req.getContextPath() + "/logout\">Logout</a></p>"
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

            int babyIndex = Integer.parseInt(babyIndexParam);
            List<Baby> patients = getPatients();

            if (babyIndex < 0 || babyIndex >= patients.size()) {
                resp.sendError(400, "Invalid baby selection");
                return;
            }

            // Load data for selected baby
            Baby selectedBaby = patients.get(babyIndex);
            List<Double> timeData = selectedBaby.getTimeData();
            List<Double> rawData = selectedBaby.getRawData();
            List<Double> smoothData = selectedBaby.getSmoothData();

            // Set headers for file download
            String filename = "glucose_data_" + selectedBaby.getName().replaceAll("\\s+", "_") + ".csv";
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            // Write CSV content
            PrintWriter writer = resp.getWriter();
            writer.println("Time,Raw_Glucose_uM,Smoothed_Glucose_uM");

            int maxSize = Math.max(timeData.size(), Math.max(rawData.size(), smoothData.size()));
            for (int i = 0; i < maxSize; i++) {
                String time = i < timeData.size() ? String.valueOf(timeData.get(i)) : "";
                String raw = i < rawData.size() ? String.valueOf(rawData.get(i)) : "";
                String smooth = i < smoothData.size() ? String.valueOf(smoothData.get(i)) : "";
                writer.println(time + "," + raw + "," + smooth);
            }
            writer.flush();
        }
    }
}
