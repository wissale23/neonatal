import java.util.List;

public class ParentChart {

    private final List<Double> rawData;
    private final List<Double> smoothData;

    private final double lower;
    private final double upper;

    private final double maxValue;
    private final int intervalMs;

    public ParentChart(List<Double> timeData, // kept for signature compatibility, unused for now
                       List<Double> rawData,
                       List<Double> smoothData,
                       double lower,
                       double upper) {
        this(rawData, smoothData, lower, upper, 40.0, 400);
    }

    public ParentChart(List<Double> rawData,
                       List<Double> smoothData,
                       double lower,
                       double upper,
                       double maxValue,
                       int intervalMs) {
        this.rawData = rawData;
        this.smoothData = smoothData;
        this.lower = lower;
        this.upper = upper;
        this.maxValue = maxValue > 0 ? maxValue : 40.0;
        this.intervalMs = intervalMs > 0 ? intervalMs : 400;
    }

    public String generateHTML() {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <meta charset=\"utf-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        html.append("  <title>Parents Glucose View OOP Branch</title>\n");
        html.append("  <style>\n");
        html.append("    body { font-family: Arial, sans-serif; padding: 18px; }\n");
        html.append("    .wrap { max-width: 640px; }\n");
        html.append("    .sub { margin-top: -8px; opacity: 0.7; }\n");
        html.append("    .value { font-size: 38px; margin: 12px 0 14px 0; }\n");
        html.append("    .bar { position: relative; width: 520px; height: 18px; border-radius: 10px; overflow: hidden; border: 1px solid rgba(0,0,0,0.15); }\n");
        html.append("    .seg { height: 100%; float: left; }\n");
        html.append("    .low { background: rgba(200,60,60,0.14); }\n");
        html.append("    .normal { background: rgba(60,160,110,0.14); }\n");
        html.append("    .high { background: rgba(210,160,60,0.14); }\n");
        html.append("    .marker { position: absolute; top: -6px; width: 3px; height: 30px; background: #111; border-radius: 2px; }\n");
        html.append("    .labels { width: 520px; display: flex; justify-content: space-between; margin-top: 8px; font-size: 13px; opacity: 0.75; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");

        html.append("<body>\n");
        html.append("  <div class=\"wrap\">\n");
        html.append("    <h2>Baby&#39;s Skin Glucose (Demo)</h2>\n");
        html.append("    <p class=\"sub\">Sped-up playback using sample data.</p>\n");
        html.append("    <div class=\"value\">Current: <b id=\"currentValue\">--</b></div>\n");

        html.append("    <div class=\"bar\" id=\"bar\">\n");
        html.append("      <div class=\"seg low\" id=\"segLow\"></div>\n");
        html.append("      <div class=\"seg normal\" id=\"segNormal\"></div>\n");
        html.append("      <div class=\"seg high\" id=\"segHigh\"></div>\n");
        html.append("      <div class=\"marker\" id=\"marker\"></div>\n");
        html.append("    </div>\n");

        html.append("    <div class=\"labels\">\n");
        html.append("      <span>0</span>\n");
        html.append("      <span>").append(tidy(lower)).append("</span>\n");
        html.append("      <span>").append(tidy(upper)).append("</span>\n");
        html.append("      <span>").append(tidy(maxValue)).append("</span>\n");
        html.append("    </div>\n");

        html.append("  </div>\n");

        html.append("  <script>\n");
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
        html.append("    const bar = document.getElementById('bar');\n\n");

        html.append("    function clamp(x, a, b) { return Math.max(a, Math.min(b, x)); }\n");
        html.append("    function series() {\n");
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
        html.append("      const s = series();\n");
        html.append("      if (s.length === 0) { valueEl.textContent = 'No data'; return; }\n");
        html.append("      const v = Number(s[i]);\n");
        html.append("      i = (i + 1) % s.length;\n");
        html.append("      if (!Number.isFinite(v)) return;\n");
        html.append("      valueEl.textContent = (Math.round(v * 10) / 10) + ' μM';\n");
        html.append("      const clamped = clamp(v, 0, MAXV);\n");
        html.append("      const px = (clamped / MAXV) * bar.clientWidth;\n");
        html.append("      marker.style.left = (px - 1) + 'px';\n");
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
