import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class Researcher extends Adult implements Pageable{

    public Researcher(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        HttpSession s = req.getSession(false);
        List<Integer> allowed = (s == null) ? null : (List<Integer>) s.getAttribute("allowedBabyIds");

        if (allowed == null || allowed.isEmpty()) {
            resp.getWriter().write(
                    "<h1>Researcher Portal</h1>" +
                            "<p>No babies assigned.</p>" +
                            "<p><a href=\"" + req.getContextPath() + "/logout\">Logout</a></p>"
            );
            return;
        }

        StringBuilder options = new StringBuilder();
        for (Integer id : allowed) {
            options.append("<option value=\"").append(id).append("\">")
                    .append("ID: ").append(id)
                    .append("</option>");
        }

        resp.getWriter().write(
                "<h1>Researcher Portal</h1>" +
                        "<p>Download glucose monitoring data:</p>" +
                        "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
                        "<label for=\"babySelect\">Select Baby: </label>" +
                        "<select name=\"babyId\" id=\"babySelect\" required>" +
                        options +
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
        if (!"download".equals(action)) return;

        List<Integer> allowed = (List<Integer>) s.getAttribute("allowedBabyIds");
        if (allowed == null || allowed.isEmpty()) {
            resp.sendError(403);
            return;
        }

        String babyIdParam = req.getParameter("babyId");
        if (babyIdParam == null) {
            resp.sendError(400, "No baby selected");
            return;
        }

        int babyId;
        try {
            babyId = Integer.parseInt(babyIdParam);
        } catch (Exception e) {
            resp.sendError(400, "Invalid baby selection");
            return;
        }

        if (!allowed.contains(babyId)) {
            resp.sendError(403);
            return;
        }

        Baby selectedBaby = BabyPatientList.getBaby(babyId);
        List<Double> timeData = selectedBaby.getTimeData();
        List<Double> rawData = selectedBaby.getRawData();
        List<Double> smoothData = selectedBaby.getSmoothData();

        String filename = "glucose_data_baby_" + babyId + ".csv";
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        java.io.PrintWriter writer = resp.getWriter();
        writer.println("Time,Raw_Glucose_uM,Smoothed_Glucose_uM");

        int maxSize = Math.max(timeData.size(), Math.max(rawData.size(), smoothData.size()));
        for (int i = 0; i < maxSize; i++) {
            String time = i < timeData.size() ? String.valueOf(timeData.get(i)) : "";
            String raw = i < rawData.size() ? String.valueOf(rawData.get(i)) : "";
            String smooth = i < smoothData.size() ? String.valueOf(smoothData.get(i)) : "";
            writer.println(time + "," + raw + "," + smooth);
        }
        writer.flush();
        writer.close();
    }

}
