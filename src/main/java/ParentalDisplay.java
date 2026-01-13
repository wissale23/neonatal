import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class ParentalDisplay {

    // Range and faster playback
    private final double maxValue = 50.0;
    private final int intervalMs = 25;

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

    private String parentPage(ParentChart parentChart) {

        return ""
                + "<!DOCTYPE html>\n"
                + "<html lang='en'>\n"
                + "<head>\n"
                + "  <meta charset='UTF-8'>\n"
                + "  <title>Parent Display</title>\n"

                + "</style>"
                + "body { font-family: 'Lato', sans-serif; }"
                // Header CSS
                + ".header { background-color: #003087; color: white; text-align: center; padding: 20px; font-size: 28px; font-weight: bold; }"
                + "</style>"

                + "</head><body>"
                + "<div class='header'>Parent Display</div>"

                + parentChart.generateHTML()
                + "</body>\n"
                + "</html>";
    }

}
