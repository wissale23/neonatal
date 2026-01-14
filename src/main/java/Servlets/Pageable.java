package Servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Pageable {
    void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException;

    String getEndpoint();
}
