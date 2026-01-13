import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class Parent extends Adult implements Pageable {

    // Range and faster playback
    private final double maxValue = 50.0;
    private final int intervalMs = 25;

    public Parent(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Pick which baby to show (defaults to Baby A / id=1)
        int babyId = 1;
        try {
            String idStr = req.getParameter("babyId");   // e.g. /parents?babyId=2
            if (idStr == null) idStr = req.getParameter("id"); // fallback
            if (idStr != null) babyId = Integer.parseInt(idStr);
        } catch (Exception ignored) {}

        // Get baby + data from BabyPatientList
        Baby baby = BabyPatientList.getBaby(babyId);

        ParentChart parentChart = new ParentChart(baby);
        resp.getWriter().write(parentPage(parentChart));

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return;
    }

    private String jsArray(List<Double> values) {
        return values == null ? "[]" : values.toString();
    }

    private String tidy(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-9) return String.valueOf((long) Math.round(v));
        return String.valueOf(v);
    }

    private String parentPage(ParentChart parentChart) {

        return ""
                + "<!DOCTYPE html>\n"
                + "<html lang='en'>\n"
                + "<head>\n"
                + "  <meta charset='UTF-8'>\n"
                + "  <title>Glucose Levels</title>\n"
                + "  <style>\n"
                + "    body { font-family: Arial, sans-serif; margin: 40px; }\n"
                + "    h2 { text-align: center; }\n"
                + "    canvas { display: block; margin: auto; }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>\n"
                + parentChart.generateHTML()
                + "</body>\n"
                + "</html>";
    }

}
