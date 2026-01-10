import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Baby extends Person{
    // Original loaded data
    private final List<Double> timeData;
    private final List<Double> rawData;
    private final List<Double> smoothData;

    // Nurse-entered glucose
    private final List<Double> sampleTimes = new ArrayList<>();
    private final List<Double> sampleValues = new ArrayList<>();

    // Feeding info
    private final List<Double> feedStarts = new ArrayList<>();
    private final List<Double> feedDurations = new ArrayList<>();
    private final List<String> feedTypes = new ArrayList<>();

    // Consultant ranges
    private double lowerRange = 2.6;
    private double upperRange = 10.0;

    // Comments
    private final List<String> comments = new ArrayList<>();

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

    public List<Double> getTimeData() { return timeData; }
    public List<Double> getRawData() { return rawData; }
    public List<Double> getSmoothData() { return smoothData; }

    public List<Double> getSampleTimes() { return sampleTimes; }
    public List<Double> getSampleValues() { return sampleValues; }

    public List<Double> getFeedStarts() { return feedStarts; }
    public List<Double> getFeedDurations() { return feedDurations; }
    public List<String> getFeedTypes() { return feedTypes; }

    public double getLowerRange() { return lowerRange; }
    public double getUpperRange() { return upperRange; }

    public List<String> getComments() { return comments; }

    public void addSample(double time, double value) {
        sampleTimes.add(time);
        sampleValues.add(value);
    }

    public void addFeeding(double start, double duration, String type) {
        feedStarts.add(start);
        feedDurations.add(duration);
        feedTypes.add(type);
    }
 //adding new comment to select whenever there is a new one
    public void setRanges(double lower, double upper) {
        this.lowerRange = lower;
        this.upperRange = upper;
    }
    
    public List<String> getComments() {
        return comments;
    }

    public void addComment(String username, String commentText) {
        if (commentText == null || commentText.isEmpty()) return;
    
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        );
        comments.add(time + "\n" + username + ": " + commentText);
    }

}
