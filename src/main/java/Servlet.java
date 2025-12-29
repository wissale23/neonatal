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

    private final double defaultLower = 2.6; //move these to another class
    private final double defaultUpper = 10.0;
    private final double defaultGlucose = 0.0;
    private final double defaultTime = 0.0;

    private ArrayList<Adult> users = new ArrayList<Adult>();

    @Override
    public void init() {
        // Adding babies
        Baby baby1 = new Baby("baby1",2,TIME_FILE,RAW_FILE,SMOOTH_FILE);

        // Demo accounts (replace with real hospital identity system later)
        passwords.put("nurse1", "nursepass");
        roles.put("nurse1", "nurse");

              

        passwords.put("parent1", "parentpass");
        roles.put("parent1", "parent");
        Parent parent1 = new Parent ("parent1",3,"/parents");
        parent1.addPatient(baby1);
        users.add(parent1);

        passwords.put("research1", "researchpass");
        roles.put("research1", "researcher");
        Researcher research1 = new Researcher("research1", 1, "/researchers");
        research1.addPatient(baby1);
        users.add(research1);
        
        passwords.put("consult1", "consultpass");
        roles.put("consult1", "consultant");  
        Consultant consult1 = new Consultant("consult1",4,"/consultants");
        consult1.addPatient(baby1);
        users.add(consult1);      
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/".equals(path)) {
            resp.setContentType("text/html");
            resp.getWriter().write(
                    "<h1>Neonatal App OOP Branch</h1>" +
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
                    "<h1>Login OOP Branch</h1>" + msg +
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

            if (low != null){
                lower = (double) low;
            }
            if (upp != null) {
                upper = (double) upp;
            }

            if (gl != null){
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
            if (com != null){
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
                times = (List<Double>) t; //casting current lists to the ones used for classes 
                glucoseValues = (List<Double>) g;
                feedStarts = (List<Double>) fs;
                feedDurations = (List<Double>) fd;
                feedTypes = (List<String>) ft;
                comments = (List<String>)com;    

            }
        }

        req.setAttribute("timeList", times);
        req.setAttribute("glucoseList", glucoseValues);  
        req.setAttribute("startList", feedStarts); 
        req.setAttribute("durationList", feedDurations);
        req.setAttribute("typeList", feedTypes);
        req.setAttribute("commentsList",comments);  
                
        if ("/consultants".equals(path)) {

            users.get(2).doGet(req,resp,lower,upper,glucoseValues,times,feedStarts,feedDurations,feedTypes,comments);

        } else if ("/nurses".equals(path)) {
            // Load data from files
            List<Double> timeData = loadDataFromResource(TIME_FILE);
            List<Double> rawData = loadDataFromResource(RAW_FILE);
            List<Double> smoothData = loadDataFromResource(SMOOTH_FILE);

            // Nurses can add their own raw values
            //rawData.addAll(userRawValues);
    

            NurseServlet nurseServ = new NurseServlet(gluc,time_);
            //GlucoseChart chart = new GlucoseChart(timeData, rawData, smoothData, lower, upper);
            resp.getWriter().write(nurseServ.nursePage(timeData, rawData, smoothData, lower, upper,glucoseValues,times,req.getContextPath()));



                
        } else if("/researchers".equals(path)){
            users.get(1).doGet(req,resp);
                
        } else if("/parents".equals(path)){
            users.get(0).doGet(req, resp);
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
            users.get(1).doPost(req,resp);
        }

        if ("/consultants".equals(req.getServletPath())) {
            users.get(2).doPost(session,req,resp);
        }
            
        
        if ("/nurses".equals(req.getServletPath())) {
            HttpSession session = req.getSession(true);

            String glucoseString = req.getParameter("glucoseInp");
            String timeString = req.getParameter("timeInp");
//move logic to nurse class
            List<Double> times = (List<Double>) session.getAttribute("timeList");
            List<Double> glucoseValues = (List<Double>) session.getAttribute("glucoseList");
            if (times == null) {
                times = new ArrayList<>();
                glucoseValues = new ArrayList<>();
                session.setAttribute("timeList", times);
                session.setAttribute("glucoseList", glucoseValues);
            }

            try {
                if (glucoseString != null && !glucoseString.isEmpty() &&
                        timeString != null && !timeString.isEmpty()) {

                    times.add(Double.parseDouble(timeString));
                    glucoseValues.add(Double.parseDouble(glucoseString));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace(); // later again
            }

            resp.sendRedirect(req.getContextPath() + "/nurses");
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
