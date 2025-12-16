
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = {"/consultants", "/nurses"},
        loadOnStartup = 1
)
public class GlucoseServlet extends HttpServlet {

    // In-memory storage for user-submitted values (optional)
    private final List<Double> userRawValues = new ArrayList<>();
    private final List<Double> userSmoothValues = new ArrayList<>();

    // File paths for your original data
    private final String TIME_FILE = "/path/to/t_glu.txt";
    private final String RAW_FILE = "/path/to/glu_uM_unsmoothed.txt";
    private final String SMOOTH_FILE = "/path/to/glu_uM_smoothed.txt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("text/html");

        if ("/consultants".equals(path)) {
            resp.getWriter().write("Test for consultants endpoint");
        } else if ("/nurses".equals(path)) {

            // Load data from files
            List<Double> timeData = loadDataFromFile(TIME_FILE);
            List<Double> rawData = loadDataFromFile(RAW_FILE);
            List<Double> smoothData = loadDataFromFile(SMOOTH_FILE);

            // Combine with user-submitted values (optional)
            rawData.addAll(userRawValues);
            smoothData.addAll(userSmoothValues);

            // Convert Java lists to JavaScript arrays as strings
            String timeArray = timeData.toString();
            String rawArray = rawData.toString();
            String smoothArray = smoothData.toString();

            // Serve HTML page with embedded Chart.js
            resp.getWriter().write(
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <title>Nurses Dashboard</title>\n" +
                    "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <h2>Neonatal Glucose Levels</h2>\n" +
                    "  <canvas id=\"glucoseChart\" width=\"900\" height=\"400\"></canvas>\n" +
                    "  <br>\n" +
                    "  <label>Enter new glucose value:</label>\n" +
                    "  <input type='number' id='newValue' step='0.1'>\n" +
                    "  <button onclick='submitValue()'>Submit</button>\n" +
                    "  <script>\n" +
                    "    // Parse Java arrays into JS arrays\n" +
                    "    const labels = " + timeArray + ";\n" +
                    "    const rawData = " + rawArray + ";\n" +
                    "    const smoothData = " + smoothArray + ";\n" +
                    "\n" +
                    "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                    "    const chart = new Chart(ctx, {\n" +
                    "      type: 'line',\n" +
                    "      data: {\n" +
                    "        labels: labels,\n" +
                    "        datasets: [\n" +
                    "          { label: 'Raw Glucose', data: rawData, borderColor: 'rgb(255,160,160)', borderWidth: 3, fill: false },\n" +
                    "          { label: 'Smoothed Glucose', data: smoothData, borderColor: 'rgb(142,11,11)', borderWidth: 1.5, fill: false }\n" +
                    "        ]\n" +
                    "      },\n" +
                    "      options: {\n" +
                    "        responsive: true,\n" +
                    "        scales: { y: { min: 0, max: 10 }, x: { title: { display: true, text: 'Time (hours)' } } }\n" +
                    "      }\n" +
                    "    });\n" +
                    "\n" +
                    "    // Function to submit new glucose value\n" +
                    "    function submitValue() {\n" +
                    "      const val = document.getElementById('newValue').value;\n" +
                    "      fetch('/nurses', { method:'POST', body: val })\n" +
                    "        .then(resp => location.reload()); // reload chart with new data\n" +
                    "    }\n" +
                    "  </script>\n" +
                    "</body>\n" +
                    "</html>"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read user-submitted glucose value
        String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            double raw = Double.parseDouble(reqBody);
            userRawValues.add(raw);

            // Simple smoothing: moving average of last 3 values
            double smooth = raw;
            if (userRawValues.size() >= 3) {
                smooth = (userRawValues.get(userRawValues.size()-1)
                        + userRawValues.get(userRawValues.size()-2)
                        + userRawValues.get(userRawValues.size()-3)) / 3.0;
            }
            userSmoothValues.add(smooth);
            resp.getWriter().write("Value submitted: " + raw);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid number");
        }
    }

    // Helper method to read doubles from a file
    private List<Double> loadDataFromFile(String path) {
        List<Double> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    result.add(Double.parseDouble(line));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }
}
