import java.util.List;

public class MonitoringChart {

    private final Baby baby;

    public MonitoringChart(Baby baby) {
        this.baby = baby;
    }

    public String generateHTML() {

        final double maxValue = 50.0;
        final int intervalMs = 25;

        List<Double> timeData = baby.getTimeData();
        List<Double> rawData = baby.getRawData();
        List<Double> smoothData = baby.getSmoothData();

        double lowerBlood = baby.getLowerRange();
        double upperBlood = baby.getUpperRange();
        double lower = lowerBlood * 3.5 + 1.5; // convert to skin
        double upper = upperBlood * 3.5 + 1.5;

        StringBuilder html = new StringBuilder();

        html.append("<style>\n");
        html.append("  .mon-wrap { max-width: 900px; width: 100%; }\n");
        html.append("  .mon-title { font-family: Arial, sans-serif; }\n");
        html.append("  .mon-value { font-family: Arial, sans-serif; font-size: 44px; margin: 12px 0 14px 0; }\n");
        html.append("  .mon-time { font-size: 16px; opacity: 0.7; margin-left: 10px; }\n");
        html.append("  .mon-bar { position: relative; width: 900px; max-width: 100%; height: 18px; border-radius: 10px; overflow: hidden; border: 1px solid rgba(0,0,0,0.15); }\n");
        html.append("  .mon-seg { height: 100%; float: left; }\n");
        html.append("  .mon-marker { position: absolute; top: -6px; width: 3px; height: 30px; background: #111; border-radius: 2px; transform: translateX(-50%); }\n");
        html.append("  .mon-low { background: rgba(255,165,0,0.22); }\n");
        html.append("  .mon-normal { background: rgba(60,160,110,0.20); }\n");
        html.append("  .mon-high { background: rgba(255,165,0,0.22); }\n");
        html.append("  .mon-axis { position: relative; width: 900px; max-width: 100%; height: 26px; margin-top: 8px; }\n");
        html.append("  .mon-tick { position: absolute; top: 0; width: 1px; height: 8px; background: rgba(0,0,0,0.35); }\n");
        html.append("  .mon-tickLabel { position: absolute; top: 10px; transform: translateX(-50%); font-size: 12px; opacity: 0.75; font-family: Arial, sans-serif; }\n");
        html.append("  .mon-rangeText { margin-top: 8px; font-size: 13px; opacity: 0.75; font-family: Arial, sans-serif; }\n");
        html.append("</style>\n");

        html.append("<div class='mon-wrap'>\n");
        html.append("  <h2 class='mon-title'>Real-time Monitoring (Skin Glucose)</h2>\n");
        html.append("  <div class='mon-value'>Current: <b id='mon_currentValue'>--</b><span class='mon-time' id='mon_timeLabel'>--</span></div>\n");

        html.append("  <div class='mon-bar' id='mon_bar'>\n");
        html.append("    <div class='mon-seg mon-low' id='mon_segLow'></div>\n");
        html.append("    <div class='mon-seg mon-normal' id='mon_segNormal'></div>\n");
        html.append("    <div class='mon-seg mon-high' id='mon_segHigh'></div>\n");
        html.append("    <div class='mon-marker' id='mon_marker'></div>\n");
        html.append("  </div>\n");

        html.append("  <div class='mon-axis' id='mon_axis'></div>\n");
        html.append("  <div class='mon-rangeText'>Safe range: ")
                .append(tidy(lower)).append(" – ").append(tidy(upper))
                .append(" &micro;M</div>\n");
        html.append("</div>\n");

        html.append("<script>\n");
        html.append("  (function(){\n");
        html.append("    const timeData = ").append(jsArray(timeData)).append(";\n");
        html.append("    const rawData = ").append(jsArray(rawData)).append(";\n");
        html.append("    const smoothData = ").append(jsArray(smoothData)).append(";\n");
        html.append("    const LOWER = ").append(lower).append(";\n");
        html.append("    const UPPER = ").append(upper).append(";\n");
        html.append("    const MAXV = ").append(maxValue).append(";\n");
        html.append("    const INTERVAL_MS = ").append(intervalMs).append(";\n\n");

        html.append("    const segLow = document.getElementById('mon_segLow');\n");
        html.append("    const segNormal = document.getElementById('mon_segNormal');\n");
        html.append("    const segHigh = document.getElementById('mon_segHigh');\n");
        html.append("    const marker = document.getElementById('mon_marker');\n");
        html.append("    const valueEl = document.getElementById('mon_currentValue');\n");
        html.append("    const timeEl = document.getElementById('mon_timeLabel');\n");
        html.append("    const axis = document.getElementById('mon_axis');\n\n");

        html.append("    function clamp(x, a, b) { return Math.max(a, Math.min(b, x)); }\n");
        html.append("    function pickSeries() {\n");
        html.append("      if (Array.isArray(smoothData) && smoothData.length > 0) return smoothData;\n");
        html.append("      if (Array.isArray(rawData) && rawData.length > 0) return rawData;\n");
        html.append("      return [];\n");
        html.append("    }\n\n");

        html.append("    function buildAxis() {\n");
        html.append("      axis.innerHTML = '';\n");
        html.append("      const step = 5;\n");
        html.append("      for (let v = 0; v <= MAXV + 1e-9; v += step) {\n");
        html.append("        const p = (v / MAXV) * 100;\n");
        html.append("        const tick = document.createElement('div');\n");
        html.append("        tick.className = 'mon-tick';\n");
        html.append("        tick.style.left = p + '%';\n");
        html.append("        axis.appendChild(tick);\n");
        html.append("        const lab = document.createElement('div');\n");
        html.append("        lab.className = 'mon-tickLabel';\n");
        html.append("        lab.style.left = p + '%';\n");
        html.append("        lab.textContent = v;\n");
        html.append("        axis.appendChild(lab);\n");
        html.append("      }\n");
        html.append("    }\n");
        html.append("    buildAxis();\n\n");

        html.append("    const lowEnd = clamp(LOWER, 0, MAXV);\n");
        html.append("    const highStart = clamp(UPPER, 0, MAXV);\n");
        html.append("    segLow.style.width = (lowEnd / MAXV) * 100 + '%';\n");
        html.append("    segNormal.style.width = (Math.max(0, highStart - lowEnd) / MAXV) * 100 + '%';\n");
        html.append("    segHigh.style.width = (Math.max(0, MAXV - highStart) / MAXV) * 100 + '%';\n\n");

        html.append("    let i = 0;\n");
        html.append("    function step() {\n");
        html.append("      const s = pickSeries();\n");
        html.append("      if (s.length === 0) { valueEl.textContent = 'No data'; timeEl.textContent=''; return; }\n");
        html.append("      const v = Number(s[i]);\n");
        html.append("      const t = (Array.isArray(timeData) && timeData.length > i) ? Number(timeData[i]) : NaN;\n");
        html.append("      i = (i + 1) % s.length;\n");
        html.append("      if (!Number.isFinite(v)) return;\n");
        html.append("      valueEl.innerHTML = (Math.round(v * 10) / 10) + ' &micro;M';\n");
        html.append("      if (Number.isFinite(t)) {\n");
        html.append("        timeEl.textContent = ' (t=' + (Math.round(t * 10) / 10) + ')';\n");
        html.append("      } else {\n");
        html.append("        const now = new Date();\n");
        html.append("        timeEl.textContent = ' (' + now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ')';\n");
        html.append("      }\n");
        html.append("      const clamped = clamp(v, 0, MAXV);\n");
        html.append("      marker.style.left = ((clamped / MAXV) * 100) + '%';\n");
        html.append("    }\n\n");

        html.append("    step();\n");
        html.append("    setInterval(step, INTERVAL_MS);\n");
        html.append("  })();\n");
        html.append("</script>\n");

        return html.toString();
    }

    private static String jsArray(List<Double> values) {
        return values == null ? "[]" : values.toString();
    }

    private static String tidy(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-9) return String.valueOf((long) Math.round(v));
        return String.valueOf(v);
    }
}
