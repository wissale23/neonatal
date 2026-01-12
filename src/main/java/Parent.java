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

        List<Double> timeData = baby.getTimeData();
        List<Double> rawData = baby.getRawData();
        List<Double> smoothData = baby.getSmoothData();

        // Get dynamic safe range set by consultant (NOT fixed defaults)
        double lowerBlood = baby.getLowerRange();
        double upperBlood = baby.getUpperRange();

        double lower = lowerBlood * 3.5 + 1.5; // convert to skin
        double upper = upperBlood * 3.5 + 1.5; // convert to skin


        resp.getWriter().write(generateHTML(timeData, rawData, smoothData, lower, upper));
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return;
    }

    // ParentChart moved into Parent
    private String generateHTML(List<Double> timeData,
                                List<Double> rawData,
                                List<Double> smoothData,
                                double lower,
                                double upper) {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"utf-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        html.append("  <title>Parents</title>\n");
        html.append("  <style>\n");
        html.append("    body { font-family: Arial, sans-serif; padding: 18px; }\n");
        html.append("    .wrap { max-width: 900px; }\n");
        html.append("    .value { font-size: 44px; margin: 12px 0 14px 0; }\n");
        html.append("    .time { font-size: 16px; opacity: 0.7; margin-left: 10px; }\n");
        html.append("    .bar { position: relative; width: 900px; max-width: 100%; height: 18px; border-radius: 10px; overflow: hidden; border: 1px solid rgba(0,0,0,0.15); }\n");
        html.append("    .seg { height: 100%; float: left; }\n");
        html.append("    .marker { position: absolute; top: -6px; width: 3px; height: 30px; background: #111; border-radius: 2px; transform: translateX(-50%); }\n");


        // Orange on BOTH sides, green in the middle
        html.append("    .low { background: rgba(255,165,0,0.22); }\n");
        html.append("    .normal { background: rgba(60,160,110,0.20); }\n");
        html.append("    .high { background: rgba(255,165,0,0.22); }\n");

        // replaced .labels rule
        html.append("    .axis { position: relative; width: 900px; max-width: 100%; height: 26px; margin-top: 8px; }\n");
        html.append("    .tick { position: absolute; top: 0; width: 1px; height: 8px; background: rgba(0,0,0,0.35); }\n");
        html.append("    .tickLabel { position: absolute; top: 10px; transform: translateX(-50%); font-size: 12px; opacity: 0.75; }\n");
        html.append("    .rangeText { margin-top: 8px; font-size: 13px; opacity: 0.75; }\n");

        html.append("    .labels { width: 900px; max-width: 100%; display: flex; justify-content: space-between; margin-top: 8px; font-size: 13px; opacity: 0.75; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");

        html.append("<body>\n");
        html.append("  <div class=\"wrap\">\n");
        html.append("    <h2>Baby's Skin Glucose</h2>\n");

        // Time shown next to current value
        html.append("    <div class=\"value\">Current: <b id=\"currentValue\">--</b><span class=\"time\" id=\"timeLabel\">--</span></div>\n");

        html.append("    <div class=\"bar\" id=\"bar\">\n");
        html.append("      <div class=\"seg low\" id=\"segLow\"></div>\n");
        html.append("      <div class=\"seg normal\" id=\"segNormal\"></div>\n");
        html.append("      <div class=\"seg high\" id=\"segHigh\"></div>\n");
        html.append("      <div class=\"marker\" id=\"marker\"></div>\n");
        html.append("    </div>\n");

        // replaceed labels bock in HTML w/ an axis
        html.append("    <div class=\"axis\" id=\"axis\"></div>\n");
        html.append("    <div class=\"rangeText\">Safe range: ")
                .append(tidy(lower)).append(" – ").append(tidy(upper))
                .append(" &micro;M</div>\n");

        html.append("  </div>\n");
        html.append("  <script>\n");
        html.append("    const timeData = ").append(jsArray(timeData)).append(";\n");
        html.append("    const rawData = ").append(jsArray(rawData)).append(";\n");
        html.append("    const smoothData = ").append(jsArray(smoothData)).append(";\n");
        html.append("    const LOWER = ").append(lower).append(";\n");
        html.append("    const UPPER = ").append(upper).append(";\n");
        html.append("    const MAXV = ").append(maxValue).append(";\n");
        html.append("    const INTERVAL_MS = ").append(intervalMs).append(";\n\n");

        html.append("    const segLow = document.getElementById('segLow');\n");
        html.append("    const segNormal = document.getElementById('segNormal');\n");
        html.append("    const segHigh = document.getElementById('segHigh');\n");
        html.append("    const marker = document.getElementById('marker');\n");
        html.append("    const valueEl = document.getElementById('currentValue');\n");
        html.append("    const timeEl = document.getElementById('timeLabel');\n");
        html.append("    const bar = document.getElementById('bar');\n\n");

        // added ticks correctly spaced
        html.append("    const axis = document.getElementById('axis');\n");
        html.append("    function buildAxis() {\n");
        html.append("      axis.innerHTML = '';\n");
        html.append("      const step = 5;\n");
        html.append("      for (let v = 0; v <= MAXV + 1e-9; v += step) {\n");
        html.append("        const p = (v / MAXV) * 100;\n");
        html.append("        const tick = document.createElement('div');\n");
        html.append("        tick.className = 'tick';\n");
        html.append("        tick.style.left = p + '%';\n");
        html.append("        axis.appendChild(tick);\n");
        html.append("        const lab = document.createElement('div');\n");
        html.append("        lab.className = 'tickLabel';\n");
        html.append("        lab.style.left = p + '%';\n");
        html.append("        lab.textContent = v;\n");
        html.append("        axis.appendChild(lab);\n");
        html.append("      }\n");
        html.append("    }\n");
        html.append("    buildAxis();\n\n");


        html.append("    function clamp(x, a, b) { return Math.max(a, Math.min(b, x)); }\n");
        html.append("    function pickSeries() {\n");
        html.append("      if (Array.isArray(smoothData) && smoothData.length > 0) return smoothData;\n");
        html.append("      if (Array.isArray(rawData) && rawData.length > 0) return rawData;\n");
        html.append("      return [];\n");
        html.append("    }\n\n");

        html.append("    const lowEnd = clamp(LOWER, 0, MAXV);\n");
        html.append("    const highStart = clamp(UPPER, 0, MAXV);\n");
        html.append("    segLow.style.width = (lowEnd / MAXV) * 100 + '%';\n");
        html.append("    segNormal.style.width = (Math.max(0, highStart - lowEnd) / MAXV) * 100 + '%';\n");
        html.append("    segHigh.style.width = (Math.max(0, MAXV - highStart) / MAXV) * 100 + '%';\n\n");

        html.append("    let i = 0;\n");
        html.append("    function step() {\n");
        html.append("      const s = pickSeries();\n");
        html.append("      if (s.length === 0) { valueEl.textContent = 'No data'; timeEl.textContent=''; return; }\n\n");

        html.append("      const v = Number(s[i]);\n");
        html.append("      const t = (Array.isArray(timeData) && timeData.length > i) ? Number(timeData[i]) : NaN;\n");
        html.append("      i = (i + 1) % s.length;\n");
        html.append("      if (!Number.isFinite(v)) return;\n\n");

        html.append("      valueEl.innerHTML = (Math.round(v * 10) / 10) + ' &micro;M';\n");

        // Show time from timeData if available, else show clock
        html.append("      if (Number.isFinite(t)) {\n");
        html.append("        timeEl.textContent = ' (t=' + (Math.round(t * 10) / 10) + ')';\n");
        html.append("      } else {\n");
        html.append("        const now = new Date();\n");
        html.append("        timeEl.textContent = ' (' + now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ')';\n");
        html.append("      }\n\n");

        html.append("      const clamped = clamp(v, 0, MAXV);\n");
        html.append("      marker.style.left = ((clamped / MAXV) * 100) + '%';\n");
        html.append("    }\n\n");

        html.append("    step();\n");
        html.append("    setInterval(step, INTERVAL_MS);\n");
        html.append("  </script>\n");

        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String jsArray(List<Double> values) {
        return values == null ? "[]" : values.toString();
    }

    private String tidy(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-9) return String.valueOf((long) Math.round(v));
        return String.valueOf(v);
    }
}
