import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthManager {

    public enum Error { MISSING_CREDENTIALS, USER_NOT_FOUND, WRONG_PASSWORD }

    public static class AuthResult {
        private final boolean ok;
        private final String role;
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

    private static class Account {
        final String password;
        final String role;

        Account(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, List<Integer>> inMemoryAssignments = new HashMap<>();

    private final boolean useDb;
    private final UserAccessDao dao;

    public AuthManager() {
        boolean db = Db.isConfigured() || envDbConfigured();
        UserAccessDao d = null;

        if (db) {
            d = new UserAccessDao();
            try {
                d.ensureSchema();
                d.ensureBootstrapAdmin(getBootstrapAdminUser(), getBootstrapAdminPass());
                d.ensureDemoUsers();
                seedDemoAssignmentsDb(d);
            } catch (Exception e) {
                System.err.println("!!!!! DB INIT FAILED — FALLING BACK TO IN-MEMORY !!!!!");
                e.printStackTrace();
                db = false;
                d = null;
            }
        }

        this.useDb = db;
        this.dao = d;

        System.err.println("AuthManager started in " + (useDb ? "DB MODE" : "IN-MEMORY MODE"));

        if (!useDb) {
            addUser("admin1", "adminpass", "admin");
            addUser("nurse1", "nursepass", "nurse");
            addUser("consult1", "consultpass", "consultant");
            addUser("research1", "researchpass", "researcher");

            List<Integer> all = allBabyIds();
            inMemoryAssignments.put("nurse1", new ArrayList<>(all));
            inMemoryAssignments.put("consult1", new ArrayList<>(all));
            inMemoryAssignments.put("research1", new ArrayList<>(all));
        }
    }

    private void seedDemoAssignmentsDb(UserAccessDao d) {
        List<Integer> all = allBabyIds();
        for (int id : all) {
            try { d.assignBaby("nurse1", id); } catch (Exception ignored) {}
            try { d.assignBaby("consult1", id); } catch (Exception ignored) {}
            try { d.assignBaby("research1", id); } catch (Exception ignored) {}
        }
    }

    private String getBootstrapAdminUser() {
        String u = System.getenv("ADMIN_USER");
        if (u == null || u.trim().isEmpty()) return "admin1";
        return u.trim();
    }

    private boolean envDbConfigured() {
        String host = System.getenv("PGHOST");
        String db   = System.getenv("PGDATABASE");
        String user = System.getenv("PGUSER");
        String pass = System.getenv("PGPASSWORD");
        return host != null && !host.trim().isEmpty()
                && db   != null && !db.trim().isEmpty()
                && user != null && !user.trim().isEmpty()
                && pass != null && !pass.trim().isEmpty();
    }

    private String getBootstrapAdminPass() {
        String p = System.getenv("ADMIN_PASS");
        if (p == null || p.trim().isEmpty()) return "adminpass";
        return p.trim();
    }

    public boolean isDbMode() {
        return useDb;
    }

    public AuthResult authenticate(String username, String password) {
        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password;

        if (u.isEmpty() || p.isEmpty()) return AuthResult.fail(Error.MISSING_CREDENTIALS);

        if (useDb) {
            try {
                UserAccessDao.UserRow row = dao.findUser(u);
                if (row == null) return AuthResult.fail(Error.USER_NOT_FOUND);
                if (!row.password.equals(p)) return AuthResult.fail(Error.WRONG_PASSWORD);
                return AuthResult.ok(normalizeRole(row.role));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Account acc = accounts.get(u);
        if (acc == null) return AuthResult.fail(Error.USER_NOT_FOUND);
        if (!acc.password.equals(p)) return AuthResult.fail(Error.WRONG_PASSWORD);
        return AuthResult.ok(acc.role);
    }

    public CreateResult createUser(String username, String password, String role) {
        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password;
        String r = normalizeRole(role);

        if (u.isEmpty() || p.isEmpty() || r.isEmpty()) return CreateResult.fail(CreateError.MISSING_FIELDS);
        if (!isValidRole(r)) return CreateResult.fail(CreateError.INVALID_ROLE);

        if (useDb) {
            try {
                boolean inserted = dao.insertUser(u, p, r);
                if (!inserted) return CreateResult.fail(CreateError.USERNAME_TAKEN);
                return CreateResult.ok();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (accounts.containsKey(u)) return CreateResult.fail(CreateError.USERNAME_TAKEN);
        accounts.put(u, new Account(p, r));
        inMemoryAssignments.put(u, new ArrayList<>(allBabyIds()));
        return CreateResult.ok();
    }

    public boolean deleteUser(String username) {
        String u = (username == null) ? "" : username.trim();
        if (u.isEmpty()) return false;

        if (useDb) {
            try {
                return dao.deleteUser(u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        boolean removed = accounts.remove(u) != null;
        inMemoryAssignments.remove(u);
        return removed;
    }

    public boolean assignBaby(String username, int babyId) {
        String u = (username == null) ? "" : username.trim();
        if (u.isEmpty()) return false;

        if (useDb) {
            try {
                return dao.assignBaby(u, babyId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        List<Integer> ids = inMemoryAssignments.get(u);
        if (ids == null) {
            ids = new ArrayList<>();
            inMemoryAssignments.put(u, ids);
        }
        if (ids.contains(babyId)) return false;
        ids.add(babyId);
        Collections.sort(ids);
        return true;
    }

    public boolean unassignBaby(String username, int babyId) {
        String u = (username == null) ? "" : username.trim();
        if (u.isEmpty()) return false;

        if (useDb) {
            try {
                return dao.unassignBaby(u, babyId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        List<Integer> ids = inMemoryAssignments.get(u);
        if (ids == null) return false;
        return ids.remove((Integer) babyId);
    }

    public List<Integer> getAssignedBabies(String username) {
        String u = (username == null) ? "" : username.trim();
        if (u.isEmpty()) return allBabyIds();

        if (useDb) {
            try {
                return dao.getAssignedBabies(u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        List<Integer> ids = inMemoryAssignments.get(u);
        if (ids == null || ids.isEmpty()) return allBabyIds();
        return new ArrayList<>(ids);
    }

    public List<String> listUsers() {
        if (useDb) {
            try {
                return dao.listUsers();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<String> out = new ArrayList<>(accounts.keySet());
        Collections.sort(out);
        return out;
    }

    public void startSession(HttpServletRequest req, String username, String role) {
        HttpSession session = req.getSession(true);
        session.setAttribute("username", username);
        session.setAttribute("role", normalizeRole(role));
        session.setAttribute("allowedBabyIds", getAssignedBabies(username));
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
        if ("admin".equals(r)) return "/admin";
        if ("nurse".equals(r)) return "/nurses";
        if ("consultant".equals(r)) return "/consultants";
        if ("researcher".equals(r)) return "/researchers";
        return "/login?error=login_required";
    }

    public String redirectIfNotAllowed(String servletPath, HttpServletRequest req) {
        String role = currentRole(req);
        if (role == null) return "/login?error=login_required";

        if ("/admin".equals(servletPath) && !"admin".equals(role)) return homeForRole(role);
        if ("/nurses".equals(servletPath) && !"nurse".equals(role)) return homeForRole(role);
        if ("/consultants".equals(servletPath) && !"consultant".equals(role)) return homeForRole(role);
        if ("/researchers".equals(servletPath) && !"researcher".equals(role)) return homeForRole(role);

        if ("/parents".equals(servletPath)) {
            if (!("nurse".equals(role) || "consultant".equals(role) || "admin".equals(role))) {
                return homeForRole(role);
            }
        }

        return null;
    }

    private List<Integer> allBabyIds() {
        List<Integer> ids = new ArrayList<>();
        for (Baby b : BabyPatientList.getAll()) ids.add(b.getId());
        return ids;
    }

    private boolean isValidRole(String role) {
        return "admin".equals(role) || "nurse".equals(role) || "consultant".equals(role) || "researcher".equals(role);
    }

    private String normalizeRole(String role) {
        return (role == null) ? "" : role.trim().toLowerCase();
    }

    public void addUser(String username, String password, String role) {
        String u = (username == null) ? "" : username.trim();
        String p = (password == null) ? "" : password;
        String r = normalizeRole(role);
        if (u.isEmpty() || p.isEmpty() || r.isEmpty()) return;
        accounts.put(u, new Account(p, r));
    }
}
