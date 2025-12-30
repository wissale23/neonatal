import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Single place for:
 * - accounts (in-memory demo provisioning)
 * - username/password authentication
 * - session setup (role + username)
 * - role-based home redirects
 * - access checks for role endpoints
 * - ADMIN: create new accounts
 */
public class AuthManager {

    public enum Error { MISSING_CREDENTIALS, USER_NOT_FOUND, WRONG_PASSWORD }

    public static class AuthResult {
        private final boolean ok;
        private final String role;   // "nurse", "consultant", "parent", "researcher", "admin"
        private final Error error;

        private AuthResult(boolean ok, String role, Error error) {
            this.ok = ok;
            this.role = role;
            this.error = error;
        }

        public static AuthResult ok(String role) { return new AuthResult(true, role, null); }
        public static AuthResult fail(Error error) { return new AuthResult(false, null, error); }

        public boolean isOk() { return ok; }
        public String getRole() { return role; }
        public Error getError() { return error; }
    }

    // ===== ADMIN account creation result types =====
    public enum CreateError { MISSING_FIELDS, USERNAME_TAKEN, INVALID_ROLE }

    public static class CreateResult {
        private final boolean ok;
        private final CreateError error;

        private CreateResult(boolean ok, CreateError error) {
            this.ok = ok;
            this.error = error;
        }

        public static CreateResult ok() { return new CreateResult(true, null); }
        public static CreateResult fail(CreateError e) { return new CreateResult(false, e); }

        public boolean isOk() { return ok; }
        public CreateError getError() { return error; }
    }
    // ==============================================

    private static class Account {
        final String password;
        final String role;
        Account(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();

    public void addUser(String username, String password, String role) {
        if (username == null) throw new IllegalArgumentException("username null");
        String u = username.trim();
        if (u.isEmpty()) throw new IllegalArgumentException("username empty");
        accounts.put(u, new Account(password == null ? "" : password, normalizeRole(role)));
    }

    public AuthResult authenticate(String username, String password) {
        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password;

        if (u.isEmpty() || p.isEmpty()) return AuthResult.fail(Error.MISSING_CREDENTIALS);

        Account acc = accounts.get(u);
        if (acc == null) return AuthResult.fail(Error.USER_NOT_FOUND);

        if (!acc.password.equals(p)) return AuthResult.fail(Error.WRONG_PASSWORD);

        return AuthResult.ok(acc.role);
    }

    public void startSession(HttpServletRequest req, String username, String role) {
        HttpSession session = req.getSession(true);
        session.setAttribute("username", username);
        session.setAttribute("role", normalizeRole(role));
    }

    public void logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
    }

    public String currentRole(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object r = s.getAttribute("role");
        return (r instanceof String) ? normalizeRole((String) r) : null;
    }

    public String homeForRole(String role) {
        String r = normalizeRole(role);
        if ("admin".equals(r)) return "/admin";              // ✅ admin home
        if ("nurse".equals(r)) return "/nurses";
        if ("consultant".equals(r)) return "/consultants";
        if ("parent".equals(r)) return "/parents";
        if ("researcher".equals(r)) return "/researchers";
        return "/login?error=login_required";
    }

    /**
     * Returns a relative redirect path (starting with "/") if access is not allowed, else null.
     * Use ONLY for role pages (/nurses, /consultants, /parents, /researchers, /admin).
     */
    public String redirectIfNotAllowed(String servletPath, HttpServletRequest req) {
        String role = currentRole(req);

        // Not logged in => must login
        if (role == null) {
            return "/login?error=login_required";
        }

        // Logged in but wrong page => send them to THEIR home
        if ("/admin".equals(servletPath) && !"admin".equals(role)) return homeForRole(role); // ✅ protect admin
        if ("/nurses".equals(servletPath) && !"nurse".equals(role)) return homeForRole(role);
        if ("/consultants".equals(servletPath) && !"consultant".equals(role)) return homeForRole(role);
        if ("/parents".equals(servletPath) && !"parent".equals(role)) return homeForRole(role);
        if ("/researchers".equals(servletPath) && !"researcher".equals(role)) return homeForRole(role);

        return null;
    }

    // ===== ADMIN: create a new account =====
    public CreateResult createUser(String username, String password, String role) {
        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password;
        String r = normalizeRole(role);

        if (u.isEmpty() || p.isEmpty() || r.isEmpty()) return CreateResult.fail(CreateError.MISSING_FIELDS);
        if (!isValidRole(r)) return CreateResult.fail(CreateError.INVALID_ROLE);
        if (accounts.containsKey(u)) return CreateResult.fail(CreateError.USERNAME_TAKEN);

        accounts.put(u, new Account(p, r));
        return CreateResult.ok();
    }

    private boolean isValidRole(String role) {
        // admin exists, but you can choose not to expose it in the admin UI
        return "admin".equals(role) || "nurse".equals(role) || "consultant".equals(role)
                || "parent".equals(role) || "researcher".equals(role);
    }
    // ======================================

    private String normalizeRole(String role) {
        return (role == null) ? "" : role.trim().toLowerCase();
    }
}
