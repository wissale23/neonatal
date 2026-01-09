import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Baby extends Person{
    private List<Double> timeData;
    private List<Double> rawData;
    private List<Double> smoothData;
    private final List<Double> sampleTimes = new ArrayList<>();
    private final List<Double> sampleValues = new ArrayList<>();

    public Baby(String name, int id, String timePath, String rawPath, String smoothPath) {
        super(name, id);
        this.timeData = loadDataFromResource(timePath);
        this.rawData = loadDataFromResource(rawPath);
        this.smoothData = loadDataFromResource(smoothPath);
    }

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

    public List<Double> getTimeData() {
        return timeData;
    }

    public List<Double> getRawData() {
        return rawData;
    }

    public List<Double> getSmoothData() {
        return smoothData;
    }
}
