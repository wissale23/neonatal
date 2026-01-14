import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet(
        urlPatterns = {"/", "/home", "/login", "/logout", "/consultants", "/nurses", "/researchers", "/parents", "/admin"},
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

    //private ArrayList<Adult> users = new ArrayList<Adult>();

    @Override
    public void init() {
        // Adding babies
        Baby baby1 = new Baby("baby1",2,TIME_FILE,RAW_FILE,SMOOTH_FILE);

//        // Demo seeded users
//        passwords.put("research1", "researchpass");
//        roles.put("research1", "researcher");
//        Researcher research1 = new Researcher("research1", 1, "/researchers");
//        research1.addPatient(baby1);
//        users.add(research1);
//
//        passwords.put("consult1", "consultpass");
//        roles.put("consult1", "consultant");
//        Consultant consult1 = new Consultant("consult1",4,"/consultants");
//        consult1.addPatient(baby1);
//        users.add(consult1);
//
//        passwords.put("nurse1", "nursepass");
//        roles.put("nurse1", "nurse");
//        Nurse nurse1 = new Nurse("nurse1",5,"/nurses");
//        nurse1.addPatient(baby1);
//        users.add(nurse1);

        auth = new AuthManager();
        loginView = new LoginPageView();

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

        if ("/home".equals(path)){
            resp.setContentType("text/html");
            resp.getWriter().write(Testpage.generatePage(req));
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
            new Consultant("consult", 0, "/consultants").doGet(req, resp);
            return;
        }

        if ("/nurses".equals(path)) {
            new Nurse("nurse", 0, "/nurses").doGet(req, resp);
            return;
        }

        if ("/researchers".equals(path)) {
            new Researcher("research", 0, "/researchers").doGet(req, resp);
            return;
        }

        if ("/parents".equals(path)) {
            new Parent("parentview", 0, "/parents").doGet(req, resp);
            return;
        }

        else if ("/admin".equals(path)) {
            String status = req.getParameter("status");
            String error = req.getParameter("error");
            String msg = "";

            if ("created".equals(status)) {
                msg = LoginPageView.okBox("Account created successfully.");
            } else if ("assigned".equals(status)) {
                msg = LoginPageView.okBox("Baby assigned successfully.");
            } else if ("unassigned".equals(status)) {
                msg = LoginPageView.okBox("Baby removed successfully.");
            } else if ("deleted".equals(status)) {
                msg = LoginPageView.okBox("User deleted successfully.");
            } else if ("username_taken".equals(error)) {
                msg = LoginPageView.errorBox("That username is already taken.");
            } else if ("invalid_role".equals(error)) {
                msg = LoginPageView.errorBox("Invalid role selected.");
            } else if ("missing_fields".equals(error)) {
                msg = LoginPageView.errorBox("Please fill in all fields.");
            }

            resp.setContentType("text/html");
            resp.getWriter().write(loginView.renderAdminPage(req.getContextPath(), msg, auth.listUsers(), BabyPatientList.getAll()));
            return;
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

            if (role == null) {
                resp.sendRedirect(req.getContextPath() + "/login?error=login_required");
                return;
            }

            if (!"admin".equals(role)) {
                resp.sendRedirect(req.getContextPath() + auth.homeForRole(role));
                return;
            }

            String action = req.getParameter("action");
            if (action == null) action = "create";

            if ("create".equals(action)) {
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

                String cleanNewUser = (newUsername == null) ? "" : newUsername.trim();
                String[] babyIds = req.getParameterValues("babyIds");
                if (babyIds != null) {
                    for (String s : babyIds) {
                        try {
                            int id = Integer.parseInt(s);
                            auth.assignBaby(cleanNewUser, id);
                        } catch (Exception ignored) {}
                    }
                }

                resp.sendRedirect(req.getContextPath() + "/admin?status=created");
                return;
            }

            if ("assign".equals(action)) {
                String targetUser = req.getParameter("targetUser");
                String babyStr = req.getParameter("babyId");
                try {
                    int id = Integer.parseInt(babyStr);
                    auth.assignBaby(targetUser, id);
                    resp.sendRedirect(req.getContextPath() + "/admin?status=assigned");
                } catch (Exception e) {
                    resp.sendRedirect(req.getContextPath() + "/admin?error=missing_fields");
                }
                return;
            }

            if ("unassign".equals(action)) {
                String targetUser = req.getParameter("targetUser");
                String babyStr = req.getParameter("babyId");
                try {
                    int id = Integer.parseInt(babyStr);
                    auth.unassignBaby(targetUser, id);
                    resp.sendRedirect(req.getContextPath() + "/admin?status=unassigned");
                } catch (Exception e) {
                    resp.sendRedirect(req.getContextPath() + "/admin?error=missing_fields");
                }
                return;
            }

            if ("delete".equals(action)) {
                String targetUser = req.getParameter("targetUser");
                if (targetUser == null || targetUser.trim().isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/admin?error=missing_fields");
                    return;
                }
                auth.deleteUser(targetUser.trim());
                resp.sendRedirect(req.getContextPath() + "/admin?status=deleted");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin");
            return;
        }

        if ("/consultants".equals(req.getServletPath())) {
            new Consultant("consult", 0, "/consultants").doPost(req, resp);
            return;
        }

        if ("/nurses".equals(req.getServletPath())) {
            new Nurse("nurse", 0, "/nurses").doPost(req, resp);
            return;
        }

        if ("/researchers".equals(req.getServletPath())) {
            new Researcher("research", 0, "/researchers").doPost(req, resp);
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
