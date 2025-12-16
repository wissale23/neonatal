import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        urlPatterns = {"/consultants", "/nurses"},
        loadOnStartup = 1
)

public class Servlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("text/html");

        if ("/consultants".equals(path)) {
            resp.getWriter().write("Test for consultants endpoint");
        } else if ("/nurses".equals(path)) {
            // Serve HTML page with interactive chart
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
                            "  <script>\n" +
                            "    const ctx = document.getElementById('glucoseChart').getContext('2d');\n" +
                            "    const chart = new Chart(ctx, {\n" +
                            "      type: 'line',\n" +
                            "      data: {\n" +
                            "        labels: [], // time points\n" +
                            "        datasets: [\n" +
                            "          {\n" +
                            "            label: 'Raw Glucose',\n" +
                            "            data: [],\n" +
                            "            borderColor: 'rgb(255,160,160)',\n" +
                            "            borderWidth: 3,\n" +
                            "            fill: false\n" +
                            "          },\n" +
                            "          {\n" +
                            "            label: 'Smoothed Glucose',\n" +
                            "            data: [],\n" +
                            "            borderColor: 'rgb(142,11,11)',\n" +
                            "            borderWidth: 1.5,\n" +
                            "            fill: false\n" +
                            "          }\n" +
                            "        ]\n" +
                            "      },\n" +
                            "      options: {\n" +
                            "        responsive: true,\n" +
                            "        scales: {\n" +
                            "          y: { min: 0, max: 10 }, // adjust to your glucose range\n" +
                            "          x: { title: { display: true, text: 'Time (hours)' } }\n" +
                            "        },\n" +
                            "        plugins: {\n" +
                            "          legend: { position: 'top' },\n" +
                            "          annotation: {\n" +
                            "            annotations: {\n" +
                            "              rangeBand: {\n" +
                            "                type: 'box',\n" +
                            "                yMin: 3,\n" +
                            "                yMax: 6,\n" +
                            "                backgroundColor: 'rgba(144,238,144,0.3)'\n" +
                            "              }\n" +
                            "            }\n" +
                            "          }\n" +
                            "        }\n" +
                            "      }\n" +
                            "    });\n" +
                            "\n" +
                            "    // Example: dynamically add points every 2s (replace with real fetch)\n" +
                            "    let t = 0;\n" +
                            "    setInterval(() => {\n" +
                            "      const raw = Math.random()*4 + 2; // simulate raw\n" +
                            "      const smooth = raw * 0.8 + 1;     // simulate smoothed\n" +
                            "      chart.data.labels.push(t++);\n" +
                            "      chart.data.datasets[0].data.push(raw);\n" +
                            "      chart.data.datasets[1].data.push(smooth);\n" +
                            "      chart.update();\n" +
                            "    }, 2000);\n" +
                            "  </script>\n" +
                            "</body>\n" +
                            "</html>"
            );
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody = (String)req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        resp.setContentType("text/html");
        resp.getWriter().write("Thank you client! " + reqBody);
    }
}