import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class Parent extends Adult implements Pageable{

    public Parent(String name, int id, String endpoint) {
        super(name, id, endpoint);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Double> timeData = getPatients().get(0).getTimeData();
        List<Double> rawData = getPatients().get(0).getRawData();
        List<Double> smoothData = getPatients().get(0).getSmoothData();

        ParentChart chart = new ParentChart(timeData, rawData, smoothData, 2.6, 10.0);
        resp.getWriter().write(chart.generateHTML());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        return;
    }
}
