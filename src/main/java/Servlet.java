import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(
        urlPatterns = {"/", "/login", "/logout", "/consultants", "/nurses", "/researchers", "/parents"},
        loadOnStartup = 1
)
public class Servlet extends HttpServlet {

    // In-memory storage for user-submitted values
    private final List<Double> userRawValues = new ArrayList<>();
    private final List<Double> userSmoothValues = new ArrayList<>();

    // Demo "hospital accounts" for MVP (pre-provisioned externally)
    private final Map<String, String> passwords = new HashMap<>();   // username -> password
    private final Map<String, String> roles = new HashMap<>();       // username -> role

    // Resource file paths
    private final String TIME_FILE = "/t_glu.txt";
    private final String RAW_FILE = "/glu_uM_unsmoothed.txt";
    private final String SMOOTH_FILE = "/glu_uM_smoothed.txt";

    private final double defaultLower = 2.6;
    private final double defaultUpper = 10.0;    

    @Override
    public void init() {
        // Demo accounts (replace with real hospital identity system later)
        passwords.put("nurse1", "nursepass");
        roles.put("nurse1", "nurse");

        passwords.put("consult1", "consultpass");
        roles.put("consult1", "consultant");

        passwords.put("parent1", "parentpass");
        roles.put("parent1", "parent");

        passwords.put("research1", "researchpass");
        roles.put("research1", "researcher");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/".equals(path)) {
            resp.setContentType("text/html");
            resp.getWriter().write(
                    "<h1>Neonatal App</h1>" +
                            "<p>Choose your role:</p>" +
                            "<a href=\"" + req.getContextPath() + "/login?role=nurse\">Nurse</a><br/>" +
                            "<a href=\"" + req.getContextPath() + "/login?role=consultant\">Consultant</a><br/>" +
                            "<a href=\"" + req.getContextPath() + "/login?role=parent\">Parent</a><br/>" +
                            "<a href=\"" + req.getContextPath() + "/login?role=researcher\">Researcher</a><br/>"
            );
            return;
        }

        if ("/login".equals(path)) {
            String roleParam = req.getParameter("role");
            String error = req.getParameter("error");
            String msg = "";
            if ("user_not_found".equals(error)) msg = "<p style='color:red'>User does not exist. Please contact admin to create an account.</p>";
            else if ("wrong_password".equals(error)) msg = "<p style='color:red'>Incorrect password.</p>";
            else if ("role_mismatch".equals(error)) msg = "<p style='color:red'>Please choose the correct user role.</p>";
            if (roleParam == null) roleParam = "";

            resp.setContentType("text/html");
            resp.getWriter().write(
                    "<h1>Login</h1>" + msg +
                            "<form method=\"POST\" action=\"" + req.getContextPath() + "/login\">" +
                            "<input type=\"hidden\" name=\"role\" value=\"" + roleParam + "\"/>" +
                            "Username: <input name=\"username\"/><br/>" +
                            "Password: <input name=\"password\" type=\"password\"/><br/>" +
                            "<button type=\"submit\">Sign in</button>" +
                            "</form>" +
                            "<p><a href=\"" + req.getContextPath() + "/\">Back</a></p>"
            );
            return;
        }

        if ("/logout".equals(path)) {
            HttpSession ss = req.getSession(false);
            if (ss != null) ss.invalidate();
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        resp.setContentType("text/html");

        // Require login + correct role before allowing access to role-specific endpoints
        HttpSession session = req.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");

        if ("/nurses".equals(path) && !"nurse".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login?role=nurse");
            return;
        }
        if ("/consultants".equals(path) && !"consultant".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login?role=consultant");
            return;
        }
        if ("/parents".equals(path) && !"parent".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login?role=parent");
            return;
        }
        if ("/researchers".equals(path) && !"researcher".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login?role=researcher");
            return;
        }


        double lower = defaultLower;
        double upper = defaultUpper;

        if (session != null) {
            Object low = session.getAttribute("lowerLimit");
            Object upp = session.getAttribute("upperLimit");
            if (low != null){
                lower = (double) low;
            }
            if (upp != null) {
                upper = (double) upp;
            }
        }

        if ("/consultants".equals(path)) {

            // Load data from files

            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Consultants only view the file data, no user input

            ConsultantServlet consult = new ConsultantServlet(lower, upper);
            resp.getWriter().write(consult.consultPage(timeData,rawData,smoothData,req.getContextPath()));

        } else if ("/nurses".equals(path)) {
            // Load data from files
            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Nurses can add their own raw values
            rawData.addAll(userRawValues);

            GlucoseChart chart = new GlucoseChart(timeData, rawData, smoothData, lower, upper);
            resp.getWriter().write(chart.generateHTML());


                
        } else if("/researchers".equals(path)){
            resp.getWriter().write(
                    "<h1>Researcher Portal</h1>" +
                            "<p>Download glucose monitoring data:</p>" +
                            "<form method=\"POST\" action=\"" + req.getContextPath() + "/researchers\">" +
                            "<button type=\"button\" name=\"action\" value=\"download\">Download Data</button>" +
                            "</form>" +
                            "<p><a href=\"" + req.getContextPath() + "/logout\">Logout</a></p>"
            );
                
        } else if("/parents".equals(path)){
            resp.getWriter().write("Test for parents endpoint");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/login".equals(req.getServletPath())) {
            String chosenRole = req.getParameter("role");
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if (username == null) username = "";
            if (password == null) password = "";
            username = username.trim();

            // 1) user not found
            if (!passwords.containsKey(username)) {
                resp.sendRedirect(req.getContextPath() + "/login?role=" + chosenRole + "&error=user_not_found");
                return;
            }

            // 2) wrong password
            if (!passwords.get(username).equals(password)) {
                resp.sendRedirect(req.getContextPath() + "/login?role=" + chosenRole + "&error=wrong_password");
                return;
            }

            // 3) role mismatch (picked Nurse but used consultant account, etc.)
            String actualRole = roles.get(username);
            if (chosenRole == null) chosenRole = "";
            if (!chosenRole.equals(actualRole)) {
                resp.sendRedirect(req.getContextPath() + "/login?role=" + chosenRole + "&error=role_mismatch");
                return;
            }

            // success
            HttpSession session = req.getSession(true);
            session.setAttribute("role", actualRole);
            session.setAttribute("username", username);

            String target;
            if ("nurse".equals(actualRole)) target = "/nurses";
            else if ("consultant".equals(actualRole)) target = "/consultants";
            else if ("parent".equals(actualRole)) target = "/parents";
            else target = "/researchers";

            resp.sendRedirect(req.getContextPath() + target);
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
                // Load all data files
                List<Double> timeData = loadDataFromResource(TIME_FILE);
                List<Double> rawData = loadDataFromResource(RAW_FILE);
                List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

                // Set headers for file download
                resp.setContentType("text/csv");
                resp.setHeader("Content-Disposition", "attachment; filename=\"glucose_data.csv\"");

                // Write CSV content
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
                return;
            }
        }

        if ("/consultants".equals(req.getServletPath())) {
            HttpSession session = req.getSession(true);
        
            String lowerString = req.getParameter("lowerLimit");
            String upperString = req.getParameter("upperLimit");
        
            try {
                if (lowerString != null && !lowerString.isEmpty()) {
                    session.setAttribute("lowerLimit", Double.parseDouble(lowerString));
                }
                if (upperString != null && !upperString.isEmpty()) {
                    session.setAttribute("upperLimit", Double.parseDouble(upperString));
                }
            } catch (NumberFormatException e) {
               e.printStackTrace(); // change this later to link that displays erro rmessage

            }
        
            resp.sendRedirect(req.getContextPath() + "/consultants");
            return;    
        }

    

        // Basic session-based access control for POST requests (nurses only)
        if (!"/nurses".equals(req.getServletPath())) {
           resp.sendError(405);
           return;
        }

        HttpSession s = req.getSession(false);
        String role = (s == null) ? null : (String) s.getAttribute("role");
        if (!"nurse".equals(role) && !"consultant".equals(role)) {
            resp.sendError(403);
            return;
        }

        // Read user-submitted blood glucose value
        String reqBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            double raw = Double.parseDouble(reqBody);
            userRawValues.add(raw);
            resp.getWriter().write("Value submitted: " + raw);
        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid number");
        }


            

   
    }

        

    // Helper method to read doubles from resources in the WAR
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
}
