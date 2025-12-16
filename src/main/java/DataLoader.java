package loader;

import java.io.*;
import java.util.*;

public class DataLoader {

    public static List<Double> load(String filename) throws IOException {
        InputStream is = DataLoader.class.getResourceAsStream("/" + filename);

        if (is == null) {
            throw new FileNotFoundException(
                    "Resource not found: " + filename +
                            " (check src/main/resources)"
            );
        }

        List<Double> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Skip empty lines
            if (line.isEmpty()) continue;

            // Skip non-numeric lines (headers)
            try {
                data.add(Double.parseDouble(line));
            } catch (NumberFormatException e) {
                // Ignore header or text rows
            }
        }

        return data;
    }
}

