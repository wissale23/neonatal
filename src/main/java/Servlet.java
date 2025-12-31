import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = {"/", "/login", "/logout", "/consultants", "/nurses", "/researchers", "/parents", "/admin"},
        loadOnStartup = 1
)
public class Servlet extends HttpServlet {

    // In-memory storage for user-submitted values
    private final List<Double> userRawValues = new ArrayList<>();
    private final List<Double> userSmoothValues = new ArrayList<>();

    // Auth and login
    private AuthManager auth;
    private LoginPageView loginView;

    // Resource file paths
    private final String TIME_FILE = "/t_glu.txt";
    private final String RAW_FILE = "/glu_uM_unsmoothed.txt";
    private final String SMOOTH_FILE = "/glu_uM_smoothed.txt";

    // Default input values
    private final double defaultLower = 2.6;
    private final double defaultUpper = 10.0;
    private final double defaultGlucose = 0.0;
    private final double defaultTime = 0.0;
    private final double defaultFeedStart = 0.0;
    private final double defaultFeedDuration = 0.0;
    private final String defaultFeedType = "";
    private final String defaultComment = "Add a comment";

    @Override
    public void init() {
        auth = new AuthManager();
        loginView = new LoginPageView();

        auth.addUser("nurse1", "nursepass", "nurse");
        auth.addUser("consult1", "consultpass", "consultant");
        auth.addUser("parent1", "parentpass", "parent");
        auth.addUser("research1", "researchpass", "researcher");
        auth.addUser("admin1", "adminpass", "admin");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // Login page
        if ("/".equals(path) || "/login".equals(path)) {
            String error = req.getParameter("error");
            String msg = "";

            if ("user_not_found".equals(error)) {
                msg = LoginPageView.errorBox("User does not exist. Please contact admin to create an account.");
            } else if ("wrong_password".equals(error)) {
                msg = LoginPageView.errorBox("Incorrect password.");
            } else if ("missing_credentials".equals(error)) {
                msg = LoginPageView.errorBox("Please enter both username and password.");
            } else if ("login_required".equals(error)) {
                msg = LoginPageView.errorBox("Please sign in to continue.");
            }

            resp.setContentType("text/html");
            resp.getWriter().write(loginView.render(req.getContextPath(), msg));
            return;
        }

        // Logout
        if ("/logout".equals(path)) {
            auth.logout(req);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        resp.setContentType("text/html");

        // Protect role pages
        if ("/nurses".equals(path) || "/consultants".equals(path) || "/parents".equals(path) || "/researchers".equals(path) || "/admin".equals(path)) {
            String redirect = auth.redirectIfNotAllowed(path, req);
            if (redirect != null) {
                resp.sendRedirect(req.getContextPath() + redirect);
                return;
            }
        }

        HttpSession session = req.getSession(false);

        double lower = defaultLower;
        double upper = defaultUpper;
        double gluc = defaultGlucose;
        double time_ = defaultTime;
        double feedStart = defaultFeedStart;
        double feedDur = defaultFeedDuration;
        String feedType = defaultFeedType;
        String commentt = defaultComment;

        if (session != null) {
            Object low = session.getAttribute("lowerLimit");
            Object upp = session.getAttribute("upperLimit");
            Object gl = session.getAttribute("glucoseInp");
            Object tm = session.getAttribute("timeInp");
            Object fs = session.getAttribute("startInp");
            Object fd = session.getAttribute("durInp");
            Object ft = session.getAttribute("typeInp");
            Object com = session.getAttribute("commInp");

            if (low != null) {
                lower = (double) low;
            }
            if (upp != null) {
                upper = (double) upp;
            }

            if (gl != null) {
                gluc = (double) gl;
            }
            if (tm != null) {
                time_ = (double) tm;
            }
            if (fs != null) {
                feedStart = (double) fs;
            }
            if (fd != null) {
                feedDur = (double) fd;
            }
            if (ft != null) {
                feedType = (String) ft;
            }
            if (com != null) {
                commentt = (String) com;
            }
        }

        List<Double> times = new ArrayList<>();
        List<Double> glucoseValues = new ArrayList<>();
        List<Double> feedStarts = new ArrayList<>();
        List<Double> feedDurations = new ArrayList<>();
        List<String> feedTypes = new ArrayList<>();
        List<String> comments = new ArrayList<>();

        if (session != null) {
            Object t = session.getAttribute("timeList");
            Object g = session.getAttribute("glucoseList");
            Object fs = session.getAttribute("startList");
            Object fd = session.getAttribute("durationList");
            Object ft = session.getAttribute("typeList");
            Object com = session.getAttribute("commentsList");

            if (t instanceof List && g instanceof List && fs instanceof List && fd instanceof List && ft instanceof List && com instanceof List) {
                times = (List<Double>) t;
                glucoseValues = (List<Double>) g;
                feedStarts = (List<Double>) fs;
                feedDurations = (List<Double>) fd;
                feedTypes = (List<String>) ft;
                comments = (List<String>) com;
            }
        }

        req.setAttribute("timeList", times);
        req.setAttribute("glucoseList", glucoseValues);
        req.setAttribute("startList", feedStarts);
        req.setAttribute("durationList", feedDurations);
        req.setAttribute("typeList", feedTypes);
        req.setAttribute("commentsList", comments);

        if ("/consultants".equals(path)) {

            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            ConsultantServlet consult = new ConsultantServlet(lower, upper);
            resp.getWriter().write(consult.consultPage(session, timeData, rawData, smoothData, glucoseValues, times, feedStarts, feedDurations, feedTypes, comments, req.getContextPath()));

        } else if ("/nurses".equals(path)) {

            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            rawData.addAll(userRawValues);

            NurseServlet nurseServ = new NurseServlet(gluc, time_, feedStart, feedDur, feedType);
            resp.getWriter().write(nurseServ.nursePage(timeData, rawData, smoothData, lower, upper, glucoseValues, times, feedStarts, feedDurations, feedTypes, comments, req.getContextPath()));

        } else if ("/researchers".equals(path)) {
            resp.setContentType("text/html");
            resp.getWriter().write(
                    "<h1>Researcher Portal</h1>" +
                            "<p>Download glucose monitoring data:</p>" +
                            "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
                            "<button type=\"submit\" name=\"action\" value=\"download\">Download Data</button>" +
                            "</form>" +
                            "<p><a href=\"" + req.getContextPath() + "/logout\">Logout</a></p>"
            );

        } else if ("/parents".equals(path)) {

            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            ParentChart chart = new ParentChart(timeData, rawData, smoothData, 2.6, 10.0);
            resp.getWriter().write(chart.generateHTML());

        } else if ("/admin".equals(path)) {
            String status = req.getParameter("status");
            String error = req.getParameter("error");
            String msg = "";

            if ("created".equals(status)) {
                msg = LoginPageView.okBox("Account created successfully.");
            } else if ("username_taken".equals(error)) {
                msg = LoginPageView.errorBox("That username is already taken.");
            } else if ("invalid_role".equals(error)) {
                msg = LoginPageView.errorBox("Invalid role selected.");
            } else if ("missing_fields".equals(error)) {
                msg = LoginPageView.errorBox("Please fill in all fields.");
            }

            resp.setContentType("text/html");
            resp.getWriter().write(loginView.renderAdminPage(req.getContextPath(), msg));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if ("/login".equals(req.getServletPath())) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            AuthManager.AuthResult result = auth.authenticate(username, password);

            if (!result.isOk()) {
                String errorParam;
                if (result.getError() == AuthManager.Error.USER_NOT_FOUND) errorParam = "user_not_found";
                else if (result.getError() == AuthManager.Error.WRONG_PASSWORD) errorParam = "wrong_password";
                else errorParam = "missing_credentials";

                resp.sendRedirect(req.getContextPath() + "/login?error=" + errorParam);
                return;
            }

            // Role comes from the account
            String cleanUsername = (username == null) ? "" : username.trim();
            String role = result.getRole();

            auth.startSession(req, cleanUsername, role);
            resp.sendRedirect(req.getContextPath() + auth.homeForRole(role));
            return;
        }

        if ("/admin".equals(req.getServletPath())) {
            String role = auth.currentRole(req);

            // must be logged in
            if (role == null) {
                resp.sendRedirect(req.getContextPath() + "/login?error=login_required");
                return;
            }

            // must be admin
            if (!"admin".equals(role)) {
                resp.sendRedirect(req.getContextPath() + auth.homeForRole(role));
                return;
            }

            String newUsername = req.getParameter("newUsername");
            String newPassword = req.getParameter("newPassword");
            String newRole = req.getParameter("newRole");

            AuthManager.CreateResult cr = auth.createUser(newUsername, newPassword, newRole);

            if (!cr.isOk()) {
                String err;
                if (cr.getError() == AuthManager.CreateError.USERNAME_TAKEN) err = "username_taken";
                else if (cr.getError() == AuthManager.CreateError.INVALID_ROLE) err = "invalid_role";
                else err = "missing_fields";

                resp.sendRedirect(req.getContextPath() + "/admin?error=" + err);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin?status=created");
            return;
        }

        if ("/researchers".equals(req.getServletPath())) {

            HttpSession s = req.getSession(false);
            String role = (s == null) ? null : (String) s.getAttribute("role");
            if (!"researcher".equals(role)) {
                resp.sendError(403);
                return;
            }

            String action = req.getParameter("action");
            if ("download".equals(action)) {
                List<Double> timeData = loadDataFromResource(TIME_FILE);
                List<Double> rawData = loadDataFromResource(RAW_FILE);
                List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

                resp.setContentType("text/csv");
                resp.setHeader("Content-Disposition", "attachment; filename=\"glucose_data.csv\"");

                PrintWriter writer = resp.getWriter();
                writer.println("Time,Raw_Glucose_uM,Smoothed_Glucose_uM");

                int maxSize = Math.max(timeData.size(), Math.max(rawData.size(), smoothData.size()));
                for (int i = 0; i < maxSize; i++) {
                    String time = i < timeData.size() ? String.valueOf(timeData.get(i)) : "";
                    String raw = i < rawData.size() ? String.valueOf(rawData.get(i)) : "";
                    String smooth = i < smoothData.size() ? String.valueOf(smoothData.get(i)) : "";
                    writer.println(time + "," + raw + "," + smooth);
                }
                writer.flush();
                writer.close();
            }

            return;
        }

        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login?error=login_required");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"nurse".equals(role)) {
            resp.sendError(403);
            return;
        }

        String lowerString = req.getParameter("lowerLimit");
        String upperString = req.getParameter("upperLimit");
        String glucString = req.getParameter("glucoseInp");
        String timeString = req.getParameter("timeInp");
        String startString = req.getParameter("startInp");
        String durString = req.getParameter("durInp");
        String typeString = req.getParameter("typeInp");
        String commentString = req.getParameter("commInp");

        if (lowerString != null && !lowerString.isEmpty()) {
            session.setAttribute("lowerLimit", Double.parseDouble(lowerString));
        }
        if (upperString != null && !upperString.isEmpty()) {
            session.setAttribute("upperLimit", Double.parseDouble(upperString));
        }

        if (glucString != null && !glucString.isEmpty()) {
            session.setAttribute("glucoseInp", Double.parseDouble(glucString));
        }
        if (timeString != null && !timeString.isEmpty()) {
            session.setAttribute("timeInp", Double.parseDouble(timeString));
        }
        if (startString != null && !startString.isEmpty()) {
            session.setAttribute("startInp", Double.parseDouble(startString));
        }
        if (durString != null && !durString.isEmpty()) {
            session.setAttribute("durInp", Double.parseDouble(durString));
        }
        if (typeString != null && !typeString.isEmpty()) {
            session.setAttribute("typeInp", typeString);
        }
        if (commentString != null && !commentString.isEmpty()) {
            session.setAttribute("commInp", commentString);
        }

        List<Double> times = (List<Double>) session.getAttribute("timeList");
        List<Double> glucoseValues = (List<Double>) session.getAttribute("glucoseList");
        List<Double> feedStarts = (List<Double>) session.getAttribute("startList");
        List<Double> feedDurations = (List<Double>) session.getAttribute("durationList");
        List<String> feedTypes = (List<String>) session.getAttribute("typeList");
        List<String> comments = (List<String>) session.getAttribute("commentsList");

        if (times == null) {
            times = new ArrayList<>();
            session.setAttribute("timeList", times);
        }
        if (glucoseValues == null) {
            glucoseValues = new ArrayList<>();
            session.setAttribute("glucoseList", glucoseValues);
        }
        if (feedStarts == null) {
            feedStarts = new ArrayList<>();
            session.setAttribute("startList", feedStarts);
        }
        if (feedDurations == null) {
            feedDurations = new ArrayList<>();
            session.setAttribute("durationList", feedDurations);
        }
        if (feedTypes == null) {
            feedTypes = new ArrayList<>();
            session.setAttribute("typeList", feedTypes);
        }

        if (glucString != null && !glucString.isEmpty() && timeString != null && !timeString.isEmpty()) {
            try {
                glucoseValues.add(Double.parseDouble(glucString));
                times.add(Double.parseDouble(timeString));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (startString != null && !startString.isEmpty() &&
                durString != null && !durString.isEmpty() &&
                typeString != null && !typeString.isEmpty()) {

            try {
                feedStarts.add(Double.parseDouble(startString));
                feedDurations.add(Double.parseDouble(durString));
                feedTypes.add(typeString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (comments == null) {
            comments = new ArrayList<>();
            session.setAttribute("commentsList", comments);
        }

        if (commentString != null && !commentString.isEmpty()) {
            String nurseUsername = (String) session.getAttribute("username");
            comments.add(nurseUsername + ": " + commentString);
        }

        resp.sendRedirect(req.getContextPath() + "/nurses");
    }

    private List<Double> loadDataFromResource(String filename) {
        List<Double> data = new ArrayList<>();
        try (InputStream input = getClass().getResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        data.add(Double.parseDouble(line));
                    } catch (NumberFormatException ignored) {
                        // skip headers or labels
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
