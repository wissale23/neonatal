import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

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
    private final String TIME_FILE = "/t_glu1.txt";
    private final String RAW_FILE = "/glu_uM_unsmoothed1.txt";
    private final String SMOOTH_FILE = "/glu_uM_smoothed1.txt";
    
    private AuthManager auth;
    private LoginPageView loginView;

    private ArrayList<Adult> users = new ArrayList<Adult>();

    @Override
    public void init() {
        // Adding babies
        Baby baby1 = new Baby("baby1",2,TIME_FILE,RAW_FILE,SMOOTH_FILE);

        // Demo accounts (replace with real hospital identity system later)

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

        passwords.put("nurse1", "nursepass");
        roles.put("nurse1", "nurse");
        Nurse nurse1 = new Nurse("nurse1",5,"/nurses");
        nurse1.addPatient(baby1);
        users.add(nurse1); 
            
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


        if ("/".equals(path)){
            resp.setContentType("text/html");
            resp.getWriter().write(Homepage.generatePage(req));
            return;
        }
                
        if ("/login".equals(path)) {
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



        // Require login + correct role before allowing access to role-specific endpoints
        HttpSession session = req.getSession(false);
                
        if ("/consultants".equals(path)) {

            users.get(2).doGet(req,resp);
            return;    

        } else if ("/nurses".equals(path)) {
            users.get(3).doGet(req,resp);
            return;    

                
        } else if("/researchers".equals(path)){
            users.get(1).doGet(req,resp);
                
        } else if("/parents".equals(path)){
            users.get(0).doGet(req, resp);
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
            users.get(1).doPost(req,resp);
        }

        if ("/consultants".equals(req.getServletPath())) {
            users.get(2).doPost(req,resp);
            return;    
        }
            
        
        if ("/nurses".equals(req.getServletPath())) {
            users.get(3).doPost(req,resp);
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

    }

}
