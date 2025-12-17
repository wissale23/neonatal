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
            // Load data from files
            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Consultants only view the file data, no user input
            GlucoseChart chart = new GlucoseChart(timeData, rawData, smoothData, 2.6, 10.0);
            resp.getWriter().write(chart.generateHTML());

        } else if ("/nurses".equals(path)) {
            // Load data from files
            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Nurses can add their own raw values
            rawData.addAll(userRawValues);

            GlucoseChart chart = new GlucoseChart(timeData, rawData, smoothData, 2.6, 10.0);
            resp.getWriter().write(chart.generateHTML());
                
        } else if("/researchers".equals(path)){
            resp.getWriter().write("Test for researchers endpoint");
                
        } else if("/parents".equals(path)){
            resp.getWriter().write("Test for parents endpoint");

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
