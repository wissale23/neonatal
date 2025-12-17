import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = {"/consultants", "/nurses","/researchers","/parents"},
        loadOnStartup = 1
)
public class Servlet extends HttpServlet {

    // In-memory storage for user-submitted values
    private final List<Double> userRawValues = new ArrayList<>();
    private final List<Double> userSmoothValues = new ArrayList<>();

    // Resource file paths
    private final String TIME_FILE = "/t_glu.txt";
    private final String RAW_FILE = "/glu_uM_unsmoothed.txt";
    private final String SMOOTH_FILE = "/glu_uM_smoothed.txt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("text/html");

        if ("/consultants".equals(path)) {
            resp.getWriter().write("Test for consultants endpoint");
        } else if("/researchers".equals(path)){
            resp.getWriter().write("Test for researchers endpoint");    
        } else if("/parents".equals(path)){
            resp.getWriter().write("Test for parents endpoint");       
        } else if ("/nurses".equals(path)) {
            // Load data from resources
            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Combine with user-submitted values
            rawData.addAll(userRawValues);
            smoothData.addAll(userSmoothValues);

            // Convert Java lists to JavaScript arrays
            String timeArray = timeData.toString();
            String rawArray = rawData.toString();
            String smoothArray = smoothData.toString();

            // Serve HTML page with Chart.js
            resp.getWriter().write(
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <title>Nurses Dashboard</title>\n" +
                    "  <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                    "  <script src=\"https://cdn.jsdelivr.net/npm/chartjs-plugin-annotation@3\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <h2>Neonatal Glucose Levels</h2>\n" +
                    "  <canvas id=\"glucoseChart\" width=\"800\" height=\"400\"></canvas>\n" +
                    "  <br>\n" +
                    "  <label>Enter new glucose value:</label>\n" +
                    "  <input type='number' id='newValue' step='0.1'>\n" +
                    "  <button onclick='submitValue()'>Submit</button>\n" +
                    "  <script>\n" +
                    "    const labels = " + timeArray + ";\n" +
                    "    const rawData = " + rawArray + ";\n" +
                    "    const smoothData = " + smoothArray + ";\n" +
                    "\n" +
                    
                    // Set range
                    "const LOWER = 2.6;\n" +
                    "const UPPER = 10.0;\n" +
                    
                    "    Chart.register(window['chartjs-plugin-annotation']);\n" +
                    "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                    "    const chart = new Chart(ctx, {\n" +
                    "      type: 'line',\n" +
                    "      data: {\n" +
                    "        labels: labels,\n" +
                    "        datasets: [\n" +
                    "          { label: 'Raw Glucose', data: rawData, borderColor: 'rgba(255,160,160)', borderWidth: 0.5, fill: false, order: 2, pointRadius: 0, pointHoverRadius: 0, borderCapStyle: 'round', borderJoinStyle: 'round' },\n" +
                    "          { label: 'Smoothed Glucose', data: smoothData, borderColor: 'rgb(142,11,11)', borderWidth: 0.25, fill: false, order: 1, pointRadius: 0, pointHoverRadius: 0, borderCapStyle: 'round', borderJoinStyle: 'round' }\n"+
                    "        ]\n" +
                    "      },\n" +
                    "      options: {\n" +
                    "        responsive: true,\n" +
                    "        scales: {\n" +
                    "          y: { min: 0, max: 40, title: {display: true, text: 'Skin Glucose (µM)' } },\n" +
                    "          x: { title: { display: true, text: 'Time (hours)' } }\n" +
                    "        },\n" +
                    "        plugins: {\n" +
                    "          annotation: {\n" +
                    "            annotations: {\n" +
                                   // Low values out of range
                    "              low: {\n" +
                    "                type: 'box', yMin: 0, yMax: LOWER,\n" +
                    "                backgroundColor: 'rgba(255,0,0,0.15)',\n" +
                    "                drawTime: 'beforeDatasetsDraw',\n" +
                    "                label: { content: 'Below Safe Range', display: true, color: '#8b0000', font: { size: 11 } }\n" +
                    "              },\n" +
                                   // Normal values in range
                    "              normal: {\n" +
                    "                type: 'box', yMin: LOWER, yMax: UPPER,\n" +
                    "                backgroundColor: 'rgba(144,238,144,0.35)',\n" +
                    "                drawTime: 'beforeDatasetsDraw',\n" +
                    "                label: { content: 'Normal Range (2.6–10.0 µM)', display: true, color: '#1b5e20', font: { size: 12, style: 'italic' } }\n" +
                    "              },\n" +
                                   // High values out of range
                    "              high: {\n" +
                    "                type: 'box', yMin: UPPER, yMax: 40,\n" +
                    "                backgroundColor: 'rgba(255,0,0,0.15)',\n" +
                    "                drawTime: 'beforeDatasetsDraw',\n" +
                    "                label: { content: 'Above Safe Range', display: true, color: '#8b0000', font: { size: 11 } }\n" +
                    "              }\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "});\n" +

                    "    function submitValue() {\n" +
                    "      const val = document.getElementById('newValue').value;\n" +
                    "      fetch('/nurses', { method:'POST', body: val })\n" +
                    "        .then(resp => location.reload());\n" +
                    "    }\n" +
                    "  </script>\n" +
                    "</body>\n" +
                    "</html>"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Read user-submitted glucose value
        String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            double raw = Double.parseDouble(reqBody);
            userRawValues.add(raw);

            // Simple smoothing (moving average of last 3 values)
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

    // Helper method to read doubles from resources in the WAR
    private List<Double> loadDataFromResource(String resourcePath) {
        List<Double> result = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(resourcePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                            result.add(Double.parseDouble(line));
                        } catch (NumberFormatException ignored) {
                            // skip headers or labels
                        }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
