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
        resp.getWriter().write(
                "<h1>Researcher Portal [OOP Branch]</h1>" +
                        "<p>Download glucose monitoring data:</p>" +
                        "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
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
            // Load all data files
            List<Double> timeData = getPatients().get(0).getTimeData();
            List<Double> rawData = getPatients().get(0).getRawData();
            List<Double> smoothData = getPatients().get(0).getSmoothData();

            // Set headers for file download
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"glucose_data.csv\"");

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
