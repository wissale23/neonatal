package Servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getPathInfo(); 
        if (path == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream is = getServletContext()
                .getResourceAsStream("/images" + path);

        if (is == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(path);
        if (mime != null) {
            resp.setContentType(mime);
        }

        is.transferTo(resp.getOutputStream());
    }
}


